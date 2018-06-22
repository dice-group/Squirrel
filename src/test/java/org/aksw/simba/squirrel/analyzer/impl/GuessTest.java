package org.aksw.simba.squirrel.analyzer.impl;

import static org.junit.Assert.*;

import java.io.File;
import java.util.Arrays;
import java.util.Collection;

import org.aksw.simba.squirrel.analyzer.Guess;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

@RunWith(Parameterized.class)
public class GuessTest {

    private Guess guess;
    private ClassLoader classLoader;
    private String fileName;
    private String expectedType;

    public GuessTest(String fileName, String type) {
        this.fileName = fileName;
        this.expectedType= type;
    }

    @Before
    public void initialize() {
        guess = new GuessMimeType();
        classLoader = getClass().getClassLoader();
    }

    public String validate(String fileName) {
        File file = new File(classLoader.getResource(fileName).getFile());
        return guess.guess(file);
    }

    @Parameterized.Parameters
    public static Collection filesToTest() {
        return Arrays.asList(new Object[][] {
            {"rdf_analyzer/new_york/new_york_rdf", "RDF/XML"},
            {"sample.n3", "N3"},
            {"sample.jsonld", "JSON-LD"},
            {"sample.nt", "N-Triples"},
            {"sample.rj", "RDFJSON"},
            {"sample.ttl", "Turtle"}
        });
    }

    @Test
    public void test() {
        System.out.println("Parameterized file is : " + fileName);
        assertEquals(expectedType, validate(fileName));
    }
}

