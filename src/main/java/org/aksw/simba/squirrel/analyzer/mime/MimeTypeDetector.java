package org.aksw.simba.squirrel.analyzer.mime;

import org.aksw.simba.squirrel.analyzer.impl.RDFAnalyzer;
import org.apache.jena.riot.Lang;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

public class MimeTypeDetector implements TypeDetector {
    private static final Logger LOGGER = LoggerFactory.getLogger(RDFAnalyzer.class);

    public Lang detectMimeType(File data) {

        Lang mimeType = null;
        try {
            FileInputStream inputStream = new FileInputStream(data.getAbsolutePath());
            FiniteStateMachine machine1 = RdfAutomata.buildRDFStateMachine();
            FiniteStateMachine machine2 = TurtleAutomata.buildTurtleStateMachine();
            char current;
            while (inputStream.available() > 0 && (!machine1.canStop() || !machine2.canStop())) {
                current = (char) inputStream.read();
                if (!machine1.isError()) //whichever leads to error state is processed anymore.
                    machine1 = machine1.switchState(String.valueOf(current));
                if (!machine2.isError())
                    machine2 = machine2.switchState(String.valueOf(current));
            }

            if (machine1.canStop() && !machine1.isError())
                mimeType = machine1.getMimeType();
            if (machine2.canStop() && !machine2.isError())
                mimeType = machine2.getMimeType();
            inputStream.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return mimeType;
    }
}
