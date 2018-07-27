package org.aksw.simba.squirrel.analyzer.impl;

import static org.junit.Assert.assertEquals;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
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
import org.apache.any23.Any23;
import org.apache.any23.source.DocumentSource;
import org.apache.any23.source.StringDocumentSource;
import org.apache.any23.writer.NTriplesWriter;
import org.apache.any23.writer.TripleHandler;
import org.apache.commons.io.IOUtils;
import org.apache.commons.net.smtp.SMTP;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.ModelFactory;
import org.apache.jena.rdf.model.Property;
import org.apache.jena.rdf.model.RDFNode;
import org.apache.jena.rdf.model.Resource;
import org.apache.jena.rdf.model.ResourceFactory;
import org.apache.jena.rdf.model.Statement;
import org.apache.jena.rdf.model.StmtIterator;
import org.apache.jena.update.UpdateAction;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TestName;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameter;
import org.junit.runners.Parameterized.Parameters;

import com.google.common.io.Files;

@RunWith(Parameterized.class)
public class MicrodataParserTest {
	
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
		analyzer = new MicrodataParser();
	}
	
	@Parameter(0)
    public String testData;
    @Parameter(1)
    public String resultData; 
    @Rule public TestName test = new TestName();
    
	
    @Parameters(name = "{index},{0},{1}")
    public static Collection<Object[]> data() {
        Object[][] data = new Object[][] {	//Test+73 = Der jeweilige Test
        	{ pathextensiontestsuit+"0001.htm",pathextensiontestsuit+"0001.ttl" },
        	{ pathextensiontestsuit+"0002.htm",pathextensiontestsuit+"0002.ttl" },
        	{ pathextensiontestsuit+"0003.htm",pathextensiontestsuit+"0003.ttl" },
        	{ pathextensiontestsuit+"0004.htm",pathextensiontestsuit+"0004.ttl" },
        	{ pathextensiontestsuit+"0005.htm",pathextensiontestsuit+"0005.ttl" },
        	{ pathextensiontestsuit+"0006.htm",pathextensiontestsuit+"0006.ttl" },
        	{ pathextensiontestsuit+"0007.htm",pathextensiontestsuit+"0007.ttl" },
        	{ pathextensiontestsuit+"0008.htm",pathextensiontestsuit+"0008.ttl" },
        	{ pathextensiontestsuit+"0009.htm",pathextensiontestsuit+"0009.ttl" },
        	{ pathextensiontestsuit+"0010.htm",pathextensiontestsuit+"0010.ttl" },
        	{ pathextensiontestsuit+"0011.htm",pathextensiontestsuit+"0011.ttl" },
        	{ pathextensiontestsuit+"0012.htm",pathextensiontestsuit+"0012.ttl" },
        	{ pathextensiontestsuit+"0013.htm",pathextensiontestsuit+"0013.ttl" },
        	{ pathextensiontestsuit+"0014.htm",pathextensiontestsuit+"0014.ttl" },
        	{ pathextensiontestsuit+"0015.htm",pathextensiontestsuit+"0015.ttl" },
        	{ pathextensiontestsuit+"0046.htm",pathextensiontestsuit+"0046.ttl" },
        	{ pathextensiontestsuit+"0047.htm",pathextensiontestsuit+"0047.ttl" },
        	{ pathextensiontestsuit+"0048.htm",pathextensiontestsuit+"0048.ttl" },
        	{ pathextensiontestsuit+"0049.htm",pathextensiontestsuit+"0049.ttl" },
        	{ pathextensiontestsuit+"0050.htm",pathextensiontestsuit+"0050.ttl" },
        	{ pathextensiontestsuit+"0051.htm",pathextensiontestsuit+"0051.ttl" },
        	{ pathextensiontestsuit+"0052.htm",pathextensiontestsuit+"0052.ttl" },
        	{ pathextensiontestsuit+"0053.htm",pathextensiontestsuit+"0053.ttl" },
        	{ pathextensiontestsuit+"0054.htm",pathextensiontestsuit+"0054.ttl" },
        	{ pathextensiontestsuit+"0055.htm",pathextensiontestsuit+"0055.ttl" },
        	{ pathextensiontestsuit+"0056.htm",pathextensiontestsuit+"0056.ttl" },
        	{ pathextensiontestsuit+"0057.htm",pathextensiontestsuit+"0057.ttl" },
        	{ pathextensiontestsuit+"0058.htm",pathextensiontestsuit+"0058.ttl" },
        	{ pathextensiontestsuit+"0059.htm",pathextensiontestsuit+"0059.ttl" },
        	{ pathextensiontestsuit+"0060.htm",pathextensiontestsuit+"0060.ttl" },
        	{ pathextensiontestsuit+"0061.htm",pathextensiontestsuit+"0061.ttl" },
        	{ pathextensiontestsuit+"0062.htm",pathextensiontestsuit+"0062.ttl" },
        	{ pathextensiontestsuit+"0063.htm",pathextensiontestsuit+"0063.ttl" },
        	{ pathextensiontestsuit+"0064.htm",pathextensiontestsuit+"0064.ttl" },
        	{ pathextensiontestsuit+"0065.htm",pathextensiontestsuit+"0065.ttl" },
        	{ pathextensiontestsuit+"0066.htm",pathextensiontestsuit+"0066.ttl" },
        	{ pathextensiontestsuit+"0067.htm",pathextensiontestsuit+"0067.ttl" },
        	{ pathextensiontestsuit+"0068.htm",pathextensiontestsuit+"0068.ttl" },
        	{ pathextensiontestsuit+"0069.htm",pathextensiontestsuit+"0069.ttl" },
        	{ pathextensiontestsuit+"0071.htm",pathextensiontestsuit+"0071.ttl" },
        	{ pathextensiontestsuit+"0073.htm",pathextensiontestsuit+"0073.ttl" },
        	{ pathextensiontestsuit+"0074.htm",pathextensiontestsuit+"0074.ttl" },
        	{ pathextensiontestsuit+"0075.htm",pathextensiontestsuit+"0075.ttl" },
        	{ pathextensiontestsuit+"0076.htm",pathextensiontestsuit+"0076.ttl" },
        	{ pathextensiontestsuit+"0077.htm",pathextensiontestsuit+"0077.ttl" },
        	{ pathextensiontestsuit+"0078.htm",pathextensiontestsuit+"0078.ttl" },
        	{ pathextensiontestsuit+"0079.htm",pathextensiontestsuit+"0079.ttl" },
        	{ pathextensiontestsuit+"0080.htm",pathextensiontestsuit+"0080.ttl" },
        	{ pathextensiontestsuit+"0081.htm",pathextensiontestsuit+"0081.ttl" },
        	{ pathextensiontestsuit+"0082.htm",pathextensiontestsuit+"0082.ttl" },
        	{ pathextensiontestsuit+"0083.htm",pathextensiontestsuit+"0083.ttl" },
        	{ pathextensiontestsuit+"0084.htm",pathextensiontestsuit+"0084.ttl" },
        	//Keine ttl Datei!{ pathextensiontestsuit+"0085.htm",pathextensiontestsuit+"0085.ttl" },
        	{ pathextensiontestsuit+"sdo_eg_md_1.htm",pathextensiontestsuit+"sdo_eg_md_1.ttl" },
        	{ pathextensiontestsuit+"sdo_eg_md_2.htm",pathextensiontestsuit+"sdo_eg_md_2.ttl" },
        	{ pathextensiontestsuit+"sdo_eg_md_3.htm",pathextensiontestsuit+"sdo_eg_md_3.ttl" },
        	{ pathextensiontestsuit+"sdo_eg_md_4.htm",pathextensiontestsuit+"sdo_eg_md_4.ttl" },
        	{ pathextensiontestsuit+"sdo_eg_md_5.htm",pathextensiontestsuit+"sdo_eg_md_5.ttl" },
        	{ pathextensiontestsuit+"sdo_eg_md_6.htm",pathextensiontestsuit+"sdo_eg_md_6.ttl" },
        	{ pathextensiontestsuit+"sdo_eg_md_7.htm",pathextensiontestsuit+"sdo_eg_md_7.ttl" },
        	{ pathextensiontestsuit+"sdo_eg_md_8.htm",pathextensiontestsuit+"sdo_eg_md_8.ttl" },
        	{ pathextensiontestsuit+"sdo_eg_md_9.htm",pathextensiontestsuit+"sdo_eg_md_9.ttl" },
        	{ pathextensiontestsuit+"sdo_eg_md_10.htm",pathextensiontestsuit+"sdo_eg_md_10.ttl" },
        	{ pathextensiontestsuit+"sdo_eg_md_11.htm",pathextensiontestsuit+"sdo_eg_md_11.ttl" },
        	{ pathextensiontestsuit+"sdo_eg_md_12.htm",pathextensiontestsuit+"sdo_eg_md_12.ttl" },
        	{ pathextensiontestsuit+"sdo_eg_md_13.htm",pathextensiontestsuit+"sdo_eg_md_13.ttl" },
        	{ pathextensiontestsuit+"sdo_eg_md_14.htm",pathextensiontestsuit+"sdo_eg_md_14.ttl" },
        	{ pathextensiontestsuit+"sdo_eg_md_15.htm",pathextensiontestsuit+"sdo_eg_md_15.ttl" },
        	{ pathextensiontestsuit+"sdo_eg_md_16.htm",pathextensiontestsuit+"sdo_eg_md_16.ttl" },
        	{ pathextensiontestsuit+"sdo_eg_md_17.htm",pathextensiontestsuit+"sdo_eg_md_17.ttl" },
        	{ pathextensiontestsuit+"sdo_eg_md_18.htm",pathextensiontestsuit+"sdo_eg_md_18.ttl" },
        	{ pathextensiontestsuit+"sdo_eg_md_19.htm",pathextensiontestsuit+"sdo_eg_md_19.ttl" },
        	{ pathextensiontestsuit+"sdo_eg_md_20.htm",pathextensiontestsuit+"sdo_eg_md_20.ttl" },
        	{ pathextensiontestsuit+"sdo_eg_md_21.htm",pathextensiontestsuit+"sdo_eg_md_21.ttl" },
        	{ pathextensiontestsuit+"sdo_eg_md_22.htm",pathextensiontestsuit+"sdo_eg_md_22.ttl" },
        	{ pathextensiontestsuit+"sdo_eg_md_23.htm",pathextensiontestsuit+"sdo_eg_md_23.ttl" },
        	{ pathextensiontestsuit+"sdo_eg_md_24.htm",pathextensiontestsuit+"sdo_eg_md_24.ttl" },
        	{ pathextensiontestsuit+"sdo_eg_md_25.htm",pathextensiontestsuit+"sdo_eg_md_25.ttl" },
        	{ pathextensiontestsuit+"sdo_eg_md_26.htm",pathextensiontestsuit+"sdo_eg_md_26.ttl" },
        	{ pathextensiontestsuit+"sdo_eg_md_27.htm",pathextensiontestsuit+"sdo_eg_md_27.ttl" },
        	{ pathextensiontestsuit+"sdo_eg_md_28.htm",pathextensiontestsuit+"sdo_eg_md_28.ttl" },
        	{ pathextensiontestsuit+"sdo_eg_md_29.htm",pathextensiontestsuit+"sdo_eg_md_29.ttl" },
        	{ pathextensiontestsuit+"sdo_eg_md_30.htm",pathextensiontestsuit+"sdo_eg_md_30.ttl" },
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
		correctObject(correctmodel, curi.getUri().toString());
			
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
	
	private void correctObject(Model model,String baseuri) {
		StmtIterator iter = model.listStatements();
		Statement newstatement = null;
		while (iter.hasNext()) {
			Statement stmt      = iter.nextStatement();  // get next statement
		    Resource  subject   = stmt.getSubject();     // get the subject
		    Property  predicate = stmt.getPredicate();   // get the predicate
		    RDFNode   object    = stmt.getObject();      // get the object
		    if(object.toString().contains("file://")) {
		    	String value = object.toString();
		    	value = baseuri+"/"+value.substring(value.lastIndexOf('/'));
		    	System.out.println(value);
		    	//stmt.changeObject(value);
//		    	RDFNode newobject = ResourceFactory.createTypedLiteral(value);
//		    	Statement newstmt = ResourceFactory.createStatement(subject, predicate, newobject);
//		    	model.remove(stmt);
//		    	model.add(newstmt);		    	
		    }
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
