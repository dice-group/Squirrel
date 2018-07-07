package org.aksw.simba.squirrel.analyzer.impl;

import org.aksw.simba.squirrel.analyzer.mime.MimeTypeDetector;
import org.aksw.simba.squirrel.analyzer.mime.TypeDetector;
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
        this.expectedType = type;
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
        return Arrays.asList(new Object[][]{
            {"rdf_analyzer/new_york/new_york_rdf", RDFLanguages.RDFXML},
            {"sample.ttl", RDFLanguages.TURTLE}
        });
    }

    @Test
    public void test() {
        assertEquals(expectedType, validate(fileName));
    }
}

