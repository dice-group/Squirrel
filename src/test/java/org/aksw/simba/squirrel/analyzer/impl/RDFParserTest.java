package org.aksw.simba.squirrel.analyzer.impl;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.aksw.simba.squirrel.analyzer.Analyzer;
import org.aksw.simba.squirrel.data.uri.CrawleableUri;
import org.aksw.simba.squirrel.sink.impl.mem.InMemorySink;
import org.apache.commons.io.IOUtils;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.ModelFactory;
import org.apache.jena.rdf.model.Property;
import org.apache.jena.rdf.model.RDFNode;
import org.apache.jena.rdf.model.Resource;
import org.apache.jena.rdf.model.ResourceFactory;
import org.apache.jena.rdf.model.Statement;
import org.apache.jena.rdf.model.StmtIterator;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Rule;
import org.junit.rules.TestName;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameter;
import org.junit.runners.Parameterized.Parameters;

@RunWith(Parameterized.class)
public abstract class RDFParserTest {
	
	protected static String schemaorg = "http://schema.org/";
	
	protected static double sumdoublearray(double[] array) {
		double sum = 0;
		for (double element : array) {
	        sum += element;
	    }
		return sum;
	}
	
	protected static String[] getItems(String content) {
		String[] parts = content.split(" ");
		String items="";
		for (String string : parts) {
			if(string.length() > 1 && string.contains(".")) items += string+",";
		}
		items = items.substring(0,items.length()-1);
		return items.split(",");
	}
	
	public static Model createModelFromN3Strings(String content) {
		
	    Model model = null;
		try {
			model = ModelFactory.createDefaultModel()
			        .read(IOUtils.toInputStream(content, "UTF-8"), null, "N-TRIPLES");
		} catch (IOException e) {
			e.printStackTrace();
		}
		return model;
	}
	
	public static Model createModelFromTurtle(String content) {
		Model model = null;
		try {
			model = ModelFactory.createDefaultModel()
			        .read(IOUtils.toInputStream(content, "UTF-8"), null, "TURTLE");
		} catch (IOException e) {
			e.printStackTrace();
		}
	    //System.out.println("model size: " + model.size());
	    return model;
	}
	
	public static Model createModelFromJSONLD(String content) {
		Model model = null;
		try {
			model = ModelFactory.createDefaultModel()
			        .read(IOUtils.toInputStream(content, "UTF-8"), null, "JSON-LD");
		} catch (IOException e) {
			e.printStackTrace();
		}
	    //System.out.println("model size: " + model.size());
	    return model;
	}
	
	public static Model createModelFromRDFJSON(String content) {
		Model model = null;
		try {
			model = ModelFactory.createDefaultModel()
			        .read(IOUtils.toInputStream(content, "UTF-8"), null, "RDF/JSON");
		} catch (IOException e) {
			e.printStackTrace();
		}
	    //System.out.println("model size: " + model.size());
	    return model;
	}
	
	protected static void printModel(Model model) {
		StmtIterator iter = model.listStatements();

		while (iter.hasNext()) {
		    Statement stmt      = iter.nextStatement();  //next statement
		    Resource  subject   = stmt.getSubject();     //subject
		    Property  predicate = stmt.getPredicate();   //predicate
		    RDFNode   object    = stmt.getObject();      //object

		    System.out.print(subject.toString());
		    System.out.print(" " + predicate.toString() + " ");
		    if (object instanceof Resource) {
		       System.out.print(object.toString());
		    } else {
		        // object is a literal
		        System.out.print(" \"" + object.toString() + "\"");
		    }
		    System.out.println(" .");
		} 
	}
	
	protected static void correctObject(Model model,String baseuri) {
		StmtIterator iter = model.listStatements();
		List<Statement> oldstatements = new ArrayList<Statement>();
		List<Statement> newstatements = new ArrayList<Statement>();
		while (iter.hasNext()) {
			Statement stmt      = iter.nextStatement();  //statement
		    Resource  subject   = stmt.getSubject();     //subject
		    Property  predicate = stmt.getPredicate();   //predicate
		    RDFNode   object    = stmt.getObject();      //object
		    if(object.toString().contains("file://")) {
		    	String value = object.toString();
		    	value = baseuri+"TestSuit"+value.substring(value.indexOf("Squirrel")+8);
		    	RDFNode newobject = ResourceFactory.createProperty(value);
		    	Statement newstmt = ResourceFactory.createStatement(subject, predicate, newobject);
		    	oldstatements.add(stmt);
		    	newstatements.add(newstmt);    	
		    }
		    if(predicate.toString().contains("file://")) {
		    	String value = predicate.toString();
		    	value = schemaorg+value.substring(value.indexOf("#")+1);
		    	Property newpredicate = ResourceFactory.createProperty(value);
		    	Statement newstmt = ResourceFactory.createStatement(subject, newpredicate, object);
		    	oldstatements.add(stmt);
		    	newstatements.add(newstmt);
		    }
		}
		for (Statement statement : oldstatements) {
			model.remove(statement);			
		}
		for (Statement statement : newstatements) {
			model.add(statement);
		}
	}
	
    /**
     * Collects statements that can be found in model A but not in model B. If A
     * and B are seen as sets of statements, this method returns the difference
     * A\B.
     *
     * @param modelA
     *            the model that should be fully contained inside model B.
     * @param modelB
     *            the model that should fully contain model A.
     * @return the difference A\B which is empty if A is a subset of B
     */
	protected static Set<Statement> getMissingStatements(Model modelA, Model modelB) {
        Set<Statement> statements = new HashSet<>();
        StmtIterator iterator = modelA.listStatements();
        Statement s;
        while (iterator.hasNext()) {
            s = iterator.next();
            if (!modelContainsStatement(modelB, s)) {
                statements.add(s);
            }
        }
        return statements;
    }
    
    /**
     * Checks whether the given statement can be found in the given model. If
     * the given statement contains blank nodes (= Anon nodes) they are replaced
     * by variables.
     *
     * @param model
     *            the model that might contain the given statement
     * @param s
     *            the statement which could be contained in the given model
     * @return <code>true</code> if the statement can be found in the model,
     *         <code>false</code> otherwise
     */
	protected static boolean modelContainsStatement(Model model, Statement s) {
        Resource subject = s.getSubject();
        RDFNode object = s.getObject();
        if (subject.isAnon()) {
            if (object.isAnon()) {
                return model.contains(null, s.getPredicate(), (RDFNode) null);
            } else {
                return model.contains(null, s.getPredicate(), object);
            }
        } else {
            if (object.isAnon()) {
                return model.contains(subject, s.getPredicate(), (RDFNode) null);
            } else {
                return model.contains(subject, s.getPredicate(), object);
            }
        }
    }
	
	protected static String fileToString(File file) throws FileNotFoundException, IOException {
		String data = "";
		try (BufferedReader br = new BufferedReader(new FileReader(file))) {
		    String line;
		    while ((line = br.readLine()) != null) {
		       data+= line+"\n";
		    }
		}	
		return data;
	}
	
	protected static String getFilePath(String content) {
		String filepath = "";
		int start = content.indexOf("<file:");
		if(start > -1) {
		content = content.substring(start, content.length()-1);
		filepath = content.substring(1, content.indexOf(" ")-1);
		}
		return filepath;
	}
	
	protected static String getpath(String filepath) {
		String path ="";
		if(!filepath.equals("")) {
			path = filepath.substring(0, filepath.lastIndexOf("/"));
		}
		return path;
	}
	
}
