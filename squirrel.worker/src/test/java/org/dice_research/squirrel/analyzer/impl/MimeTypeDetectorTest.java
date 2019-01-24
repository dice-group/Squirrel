package org.dice_research.squirrel.analyzer.impl;

import org.apache.jena.riot.Lang;
import org.apache.jena.riot.RDFLanguages;
import org.apache.jena.vocabulary.RDF;
import org.dice_research.squirrel.analyzer.mime.MimeTypeDetector;
import org.dice_research.squirrel.analyzer.mime.TypeDetector;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

import java.io.File;
import java.net.URL;
import java.util.Arrays;
import java.util.Collection;

import static org.junit.Assert.assertEquals;

/**
 * Test
 * {@link MimeTypeDetector#detectMimeType(File)}}.
 *
 * @author Abhishek Hassan Chandrashekar (abhihc@mail.uni-paderborn.de)
 */
@RunWith(Parameterized.class)
public class MimeTypeDetectorTest {

    private TypeDetector typeDetector;
    private ClassLoader classLoader;
    private String fileName;
    private Lang expectedType;

    public MimeTypeDetectorTest(String fileName, Lang type) {
        this.fileName = fileName;
        this.expectedType = type;
    }

    @Before
    public void initialize() {
        typeDetector = new MimeTypeDetector();
        classLoader = getClass().getClassLoader();
    }

    private Lang validate(String fileName) {
        if(fileName == null) {
            return RDFLanguages.RDFNULL;
        }
        else {
            URL url = classLoader.getResource(fileName);
            if(url != null) {
                File file = new File(url.getFile());
                return typeDetector.detectMimeType(file);
            }
            else {
                return RDFLanguages.RDFNULL;
            }
        }
    }

    @Parameterized.Parameters
    public static Collection filesToTest() {
        return Arrays.asList(new Object[][]{
            {"rdf_analyzer/new_york/RDFXML", RDFLanguages.RDFXML},
            {"Sample_Files/Test_File_2", RDFLanguages.RDFNULL},
            {"rdf_analyzer/new_york/new_york_turtle", RDFLanguages.TURTLE},
            {"Sample_Files/sample.nt", RDFLanguages.NTRIPLES},
            {"rdf_analyzer/new_york/new_york_rdfjson", RDFLanguages.RDFJSON},
            {"rdf_analyzer/new_york/new_york_jsonld", RDFLanguages.JSONLD},
            {null, RDFLanguages.RDFNULL}
        });
    }

    @Test
    public void test() {
        assertEquals(expectedType, validate(fileName));
    }
}

