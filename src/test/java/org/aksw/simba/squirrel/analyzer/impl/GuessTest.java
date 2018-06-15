package org.aksw.simba.squirrel.analyzer.impl;

import static org.junit.Assert.*;

import java.io.File;
import org.aksw.simba.squirrel.analyzer.Guess;
import org.junit.Before;
import org.junit.Test;

public class GuessTest {

	private Guess guess;
	private String[] filesToTest= {"new_york.rdf","sample.n3","sample.jsonld","sample.nt","sample.rj","sample.ttl"};
	private File[] data = new File[filesToTest.length];
	private String[] expectedMimeTypes = {"RDF/XML","N3","JSON-LD","N-Triples","RDFJSON","Turtle"};

	@Before
	public void prepare() {

		guess = new GuessMimeType();
		ClassLoader classLoader = getClass().getClassLoader();
		for (int i=0; i<filesToTest.length;i++) {
			data[i] = new File(classLoader.getResource(filesToTest[i]).getFile());
		}
	}

	@Test
	public void test() {

		for (int i=0; i<data.length; i++) {
			String mimeType=guess.guess(data[i]);
			assertEquals(expectedMimeTypes[i], mimeType);
		}
	}
}

