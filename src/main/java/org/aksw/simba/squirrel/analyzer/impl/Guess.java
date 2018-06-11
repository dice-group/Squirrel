package org.aksw.simba.squirrel.analyzer.impl;

public class Guess {

	public static void parse(String fileContent)
	{
		if(fileContent.toString().isEmpty()) {
			System.out.println("File is empty");
		}
		else if(fileContent.toString().contains("<?xml") && fileContent.toString().contains("<rdf:RDF")) {
        	//RDF/XML
			System.out.println("Guessing the RDF serialization type based on the file content LANG : RDF/XML");
        }else if(fileContent.toString().contains("@prefix") || fileContent.toString().contains("@base")) {
        	//Turtle/Notation3
        	if(fileContent.toString().contains("@keywords") || fileContent.toString().contains("@forall") || fileContent.toString().contains("@forsome")) {
        	//Notation3	
        		System.out.println("Guessing the RDF serialization type based on the file content LANG : N3");	
        	}
        	else {
        	//Turtle	
        		System.out.println("Guessing the RDF serialization type based on the file content LANG : Turtle");
        	}
        }else if(fileContent.toString().contains("[") && fileContent.toString().contains("]") && fileContent.toString().contains("{") && fileContent.toString().contains("}")) {
        	//JSON-LD/RDFJSON
        	if(fileContent.toString().startsWith("[")) {
        		System.out.println("Guessing the RDF serialization type based on the file content LANG : JSON-LD");
        	}
        	else {
        		System.out.println("Guessing the RDF serialization type based on the file content LANG : RDF/JSON");
        	}
        }else {
        	System.out.println("Guessing the RDF serialization type based on the file content LANG : N-Triples");
        }
		
	}
	
	
}
