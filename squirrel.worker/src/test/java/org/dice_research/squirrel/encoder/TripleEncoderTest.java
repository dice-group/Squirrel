package org.dice_research.squirrel.encoder;

import java.util.ArrayList;
import java.util.List;

import org.apache.jena.graph.NodeFactory;
import org.apache.jena.graph.Triple;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;


/**
 * 
 * Test for Triple Encoder, available on the Abstract Analyzer
 * 
 * @author Geraldo de Souza Junior  - gsjunior@mail.uni-paderborn.de
 *
 */

public class TripleEncoderTest {

	private List<Triple> listUncodedTriples;
	private List<Triple> listExpectedTriples;

	@Before
	public void createUris() {
		listUncodedTriples = new ArrayList<Triple>();
		listExpectedTriples = new ArrayList<Triple>();

		for (int i = 0; i < 10; i++) {
			String s = "http://dice-research.org/Squirrel/?triple= " + i + " statement " + i;
			String p = "http://dice-research.org/Squirrel/predicate" + i;
			String o = "http://dice-research.org/Squirrel/?triple= " + i + " object " + i;

			Triple tu = new Triple(NodeFactory.createURI(s), NodeFactory.createURI(p), NodeFactory.createURI(o));

			listUncodedTriples.add(tu);

			s = "http://dice-research.org/Squirrel/?triple=%20" + i + "%20statement%20" + i;
			p = "http://dice-research.org/Squirrel/predicate" + i;
			o = "http://dice-research.org/Squirrel/?triple=%20" + i + "%20object%20" + i;

			Triple te = new Triple(NodeFactory.createURI(s), NodeFactory.createURI(p), NodeFactory.createURI(o));

			listExpectedTriples.add(te);

		}

	}

	@Test
	public void testEncoding() {

		TripleEncoder encoder = new TripleEncoder();

		for (int i = 0; i < 10; i++) {
//			System.out.println(encoder.encodeTriple(listUncodedTriples.get(i)));
//			System.out.println(listExpectedTriples.get(i));

			Assert.assertEquals(listExpectedTriples.get(i), encoder.encodeTriple(listUncodedTriples.get(i)));
		}

	}

}
