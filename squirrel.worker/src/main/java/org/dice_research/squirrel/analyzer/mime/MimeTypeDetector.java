package org.dice_research.squirrel.analyzer.mime;

import org.apache.jena.riot.Lang;
import org.dice_research.squirrel.analyzer.impl.RDFAnalyzer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.ListIterator;

/**
 * Standard implementation of the {@link TypeDetector} interface.
 *
 * @author Abhishek Hassan Chandrashekar (abhihc@mail.uni-paderborn.de).
 */
public class MimeTypeDetector implements TypeDetector {

    private static final Logger LOGGER = LoggerFactory.getLogger(RDFAnalyzer.class);

    protected List<Lang> mimeTypes = new ArrayList<>();

    /**
     * The finite state machines are built.
     * Precondtions to the files are checked.
     * Each element in the file is passed one after the
     * other to the built finite state machines until the check is complete.
     *
     */

    public Lang detectMimeType(File data) {

        LinkedList<FiniteStateMachine> machinesList = new LinkedList<FiniteStateMachine>();

        setMimeTypes(Lang.RDFXML, Lang.TURTLE, Lang.NTRIPLES, Lang.RDFJSON, Lang.JSONLD);

        Lang detectedMimeType = null;

        try {
            FileInputStream inputStream = new FileInputStream(data.getAbsolutePath());

            for (Lang type : mimeTypes) {
                machinesList.add(FiniteStateMachineFactory.create(type.getName()));
            }

            char current;

            nextchar:
            while (inputStream.available() > 0 && machinesList.size() > 1) {
                current = (char) inputStream.read();
                // Preconditions checked for the file.
                if (Character.isWhitespace(current)) {
                    continue nextchar;
                } else if (current == '#') {
                    do {
                        current = (char) inputStream.read();
                    } while (current != '\n');
                    continue nextchar;
                }


                ListIterator<FiniteStateMachine> iter = machinesList.listIterator();

                while (iter.hasNext()) {
                    FiniteStateMachine machine = iter.next();
                    machine.switchState(String.valueOf(current));

                    if (machine.isError())
                        iter.remove();
                }

            }

            if (machinesList.size() != 0)
                detectedMimeType = machinesList.get(0).getMimeType();
            else
                detectedMimeType = Lang.RDFNULL;
            inputStream.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return detectedMimeType;
    }

    /**
     * Adding all the mime types to a list
     *
     */
    private void setMimeTypes(Lang... types) {
        for (Lang type : types)
            mimeTypes.add(type);
    }
}
