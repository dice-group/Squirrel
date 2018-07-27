package org.aksw.simba.squirrel.analyzer.impl;

import static org.junit.Assert.assertEquals;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.charset.Charset;
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
import org.apache.jena.rdf.model.Statement;
import org.apache.jena.rdf.model.StmtIterator;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TestName;
import org.junit.runners.Parameterized.Parameter;
import org.junit.runners.Parameterized.Parameters;

import com.google.common.io.Files;

public class MicroformatParserTest {

	private static String context = "http://rdfa.info/test-suite/test-cases/rdfa1.1/";
	private static String pathextensiontestsuit = "\\html_scraper_analyzer\\MicrodataParserTestResources\\TestSuit\\";
	
	private static Analyzer analyzer;
	private CrawleableUri curi;
	private static InMemorySink sink;
	ClassLoader classLoader = getClass().getClassLoader();
	static double[] truepositiv = new double[data().size()];
	static double[] falsenegativ = new double[data().size()];
	static double[] falsepositiv = new double[data().size()];
	
	@BeforeClass
	public static void initialization () throws URISyntaxException {
		sink = new InMemorySink();
		analyzer = new MicroformatParser();
	}
	
	@Parameter(0)
    public String testData;
    @Parameter(1)
    public String resultData; 
    @Rule public TestName test = new TestName();
    
	
    @Parameters(name = "{index},{0},{1}")
    public static Collection<Object[]> data() {
        Object[][] data = new Object[][] {
        	{ pathextensiontestsuit+"0055.htm",pathextensiontestsuit+"0055.ttl" },
        };
        return Arrays.asList(data);
    }
    
	@Test
	public void parsertest() throws URISyntaxException, IOException {
		
		String strindex = test.getMethodName();
		strindex = strindex.substring(11, strindex.indexOf(","));
		int index = Integer.parseInt(strindex);		
		//curi = new CrawleableUri(new URI("microdataTest"));
		URL test_url = ClassLoader.getSystemResource(testData);
		File test = new File(test_url.toURI());
		URL result_url = ClassLoader.getSystemResource(resultData);
		File result = new File(result_url.toURI());
		
		String pathcontext = testData.substring(0,testData.lastIndexOf('\\'));
		pathcontext = context+pathcontext.substring(pathcontext.lastIndexOf('\\')+1,pathcontext.length())+"/"+testData.substring(testData.lastIndexOf('\\')+1,testData.length());
		//System.out.println(pathcontext);
		curi = new CrawleableUri(new URI(pathcontext));
		
		analyzer.analyze(curi, test, sink);
		
		List<byte[]> tdp = sink.getCrawledUnstructuredData().get(pathcontext);
		String decodedtest = new String(tdp.get(0), "UTF-8");
		//if(!decodedtest.equals(""))decodedtest = decodedtest.substring(0, decodedtest.length()-1);
		
		Model decodedmodel = createModelFromN3Strings(decodedtest);
		System.out.print("created decodemodel ");
				
		//System.out.println(decodedtest);
		//System.out.println();
		
//		String correctresult = Files.readLines(result, Charset.forName("utf-8")).toString().replaceAll(", " ,"\n");
//	    correctresult = correctresult.substring(1,correctresult.length()-1);
		String correctresult = "";
		try (BufferedReader br = new BufferedReader(new FileReader(result))) {
		    String line;
		    while ((line = br.readLine()) != null) {
		       correctresult+= line+"\n";
		    }
		}			
		Model correctmodel = createModelFromTurtle(correctresult);
		System.out.print("created correctmodel ");
			
		//System.out.println(turtleresult);
		//System.out.println();
		
		Set<Statement> missingstatements = getMissingStatements(correctmodel, decodedmodel);
		for (Statement statement : missingstatements) {
			//System.out.println(statement.toString());
			falsenegativ[index]++;
		}
		System.out.println();
		Set<Statement> morestatements = getMissingStatements(decodedmodel, correctmodel);
		for (Statement statement : morestatements) {
			falsepositiv[index]++;
			//System.out.println(statement.toString());
		}
		truepositiv[index]+=correctmodel.size()-falsenegativ[index];
		System.out.println();
		
		if(falsenegativ[index] != 0) {
			System.out.println("DecodedModel");
			printModel(decodedmodel);
			System.out.println("CorrectModel");
			printModel(correctmodel);
			System.out.println("MissingStatements");
			
			for (Statement statement : missingstatements) {
				System.out.println(statement.toString());
			}
			System.out.println("MoreStatements");
			for (Statement statement : morestatements) {
				System.out.println(statement.toString());
			}
			System.out.println();
		}
		assertEquals(0.0,falsenegativ[index],0.0);
	}
	
	@AfterClass
	public static void binaryclassifiers() throws URISyntaxException {
		double[] p = new double[data().size()];
		double[] r = new double[data().size()];
		for(int i = 0;i<p.length;i++) {
			if((truepositiv[i]+falsepositiv[i]) != 0)p[i] = truepositiv[i]/(truepositiv[i]+falsepositiv[i]);
			else p[i] = 0;
			if((truepositiv[i]+falsenegativ[i]) != 0)r[i] = truepositiv[i]/(truepositiv[i]+falsenegativ[i]);
			else r[i] = 0;
		}
		double psum = sumdoublearray(p);
		double rsum = sumdoublearray(r);
		double macrop = (1.0/p.length)*psum;
		double macror = (1.0/r.length)*rsum;
		double microp = (psum/(psum+sumdoublearray(falsepositiv)));
		double micror = (psum/(psum+sumdoublearray(falsenegativ)));
		
		System.out.println("Macro Precision");
		System.out.println(macrop);
		System.out.println("Micro Precision");
		System.out.println(microp);
		System.out.println("Macro Recall");
		System.out.println(macror);
		System.out.println("Micro Recall");
		System.out.println(micror);
//		System.out.println("True positive rate (TPR), Recall");
//		System.out.println(truepositiv[0]/(truepositiv[0]+falsenegativ[0]));
//		System.out.println("Post predictive value (PPV), Precision");
//		System.out.println(truepositiv[0]/(truepositiv[0]+falsepositiv[0]));
	}
	
	public static double sumdoublearray(double[] array) {
		double sum = 0;
		for (double element : array) {
	        sum += element;
	    }
		return sum;
	}
	
	public Model createModelFromN3Strings(String content) {
		
	    Model model = null;
		try {
			model = ModelFactory.createDefaultModel()
			        .read(IOUtils.toInputStream(content, "UTF-8"), null, "N-TRIPLES");
		} catch (IOException e) {
			e.printStackTrace();
		}
		return model;
	}
	
	public Model createModelFromTurtle(String content) {
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
	
	private void printModel(Model model) {
		// list the statements in the Model
		StmtIterator iter = model.listStatements();

		// print out the predicate, subject and object of each statement
		while (iter.hasNext()) {
		    Statement stmt      = iter.nextStatement();  // get next statement
		    Resource  subject   = stmt.getSubject();     // get the subject
		    Property  predicate = stmt.getPredicate();   // get the predicate
		    RDFNode   object    = stmt.getObject();      // get the object

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
    public static Set<Statement> getMissingStatements(Model modelA, Model modelB) {
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
    public static boolean modelContainsStatement(Model model, Statement s) {
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
}
