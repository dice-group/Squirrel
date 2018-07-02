package org.aksw.simba.squirrel.analyzer.impl;

import org.aksw.simba.squirrel.analyzer.TypeDetector;
import org.apache.jena.riot.Lang;
import org.apache.jena.riot.RDFLanguages;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

import java.io.File;
import java.util.Arrays;
import java.util.Collection;

import static org.junit.Assert.assertEquals;


@RunWith(Parameterized.class)
public class MimeTypeDetectorTest {

    private TypeDetector typeDetector;
    private ClassLoader classLoader;
    private String fileName;
    private Lang expectedType;

    public MimeTypeDetectorTest(String fileName, Lang type) {
        this.fileName = fileName;
        this.expectedType= type;
    }

    @Before
    public void initialize() {
        typeDetector = new MimeTypeDetector();
        classLoader = getClass().getClassLoader();
    }

    public Lang validate(String fileName) {
        File file = new File(classLoader.getResource(fileName).getFile());
        return typeDetector.detectMimeType(file);
    }

    @Parameterized.Parameters
    public static Collection filesToTest() {
        return Arrays.asList(new Object[][] {
            {"rdf_analyzer/new_york/new_york_rdf", RDFLanguages.RDFXML},
//            {"sample.n3", RDFLanguages.N3},
//            {"sample.jsonld", RDFLanguages.JSONLD},
//            {"sample.nt", RDFLanguages.NTRIPLES},
//            {"sample.rj", RDFLanguages.RDFJSON},
            {"sample.ttl", RDFLanguages.TURTLE}
        });
    }

    @Test
    public void test() {
        System.out.println("Parameterized file is : " + fileName);
        assertEquals(expectedType, validate(fileName));
    }
}

