package org.aksw.simba.squirrel.analyzer.impl;

import java.io.File;
import java.io.FileInputStream;
import org.aksw.simba.squirrel.analyzer.Guess;
import org.apache.tika.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class GuessMimeType implements Guess {
	private static final Logger LOGGER = LoggerFactory.getLogger(RDFAnalyzer.class);

	public String guess(File data) {
		String mimeType = null;
		try {
			FileInputStream inputStream = new FileInputStream(data.getAbsolutePath());
			String fileContent = IOUtils.toString(inputStream);
			if (fileContent.toString().isEmpty()) {
				mimeType=null;
			} else if (fileContent.toString().contains("<?xml") && fileContent.toString().contains("<rdf:RDF")) {
				mimeType = "RDF/XML";
			} else if (fileContent.toString().contains("@prefix") || fileContent.toString().contains("@base")) {
				// Turtle/Notation3
				if (fileContent.toString().contains("@keywords") || fileContent.toString().contains("@forAll")
						|| fileContent.toString().contains("@forsome")) {
					// Notation3
					mimeType = "N3";
				} else {
					// Turtle
					mimeType = "Turtle";
				}
			} else if (fileContent.toString().contains("[") && fileContent.toString().contains("]")
					&& fileContent.toString().contains("{") && fileContent.toString().contains("}")) {
				// JSON-LD/RDFJSON
				if (fileContent.toString().startsWith("[")) {
					mimeType = "JSON-LD";
				} else {
					mimeType = "RDFJSON";
				}
			} else {
				mimeType ="N-Triples";
			}
		} catch (Exception e) {
			LOGGER.error("Exception while analyzing. Aborting.");
		} finally {

		}
		return mimeType;
	}

}
