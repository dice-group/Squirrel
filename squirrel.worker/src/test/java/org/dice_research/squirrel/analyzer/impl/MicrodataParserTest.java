package org.dice_research.squirrel.analyzer.impl;

import static org.junit.Assert.assertEquals;

import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.Statement;
import org.dice_research.squirrel.analyzer.Analyzer;
import org.dice_research.squirrel.collect.SimpleUriCollector;
import org.dice_research.squirrel.collect.UriCollector;
import org.dice_research.squirrel.data.uri.CrawleableUri;
import org.dice_research.squirrel.data.uri.serialize.java.GzipJavaUriSerializer;
import org.dice_research.squirrel.sink.impl.mem.InMemorySink;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Ignore;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TestName;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameter;
import org.junit.runners.Parameterized.Parameters;

@Ignore
@RunWith(Parameterized.class)
public class MicrodataParserTest extends RDFParserTest {
	
	private static String context = "http://w3c.github.io/microdata-rdf/tests/";
	private static String pathextensiontestsuit = "html_scraper_analyzer/MicrodataParserTestResources/TestSuit/";
	
	private static Analyzer analyzer;
	private static final UriCollector collector = new SimpleUriCollector(new GzipJavaUriSerializer());
	private CrawleableUri curi;
	private static InMemorySink sink;
	ClassLoader classLoader = getClass().getClassLoader();
	public static Map<String, List<Double>> testresults = new HashMap<String,List<Double>>();
	
	@Parameter(0)
    public String testData;
    @Parameter(1)
    public String resultData; 
    @Rule public TestName test = new TestName();
	
//	static double[] truepositiv = new double[data().size()];
//	static double[] falsenegativ = new double[data().size()];
//	static double[] falsepositiv = new double[data().size()];
	

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
        	{ pathextensiontestsuit+"sdo_eg_md_30.htm",pathextensiontestsuit+"sdo_eg_md_30.ttl" },//*/
        };
        return Arrays.asList(data);
    }
    
	@Test
	public void parsertest() throws URISyntaxException, IOException {
		sink = new InMemorySink();
		analyzer = new MicrodataAnalyzer(collector);
		
		String strindex = test.getMethodName();
//		strindex = strindex.substring(11, strindex.indexOf(","));
//		int index = Integer.parseInt(strindex);				
		//curi = new CrawleableUri(new URI("microdataTest"));
		
		ClassLoader classLoader = getClass().getClassLoader();

		
		URL test_url = classLoader.getResource(testData);
		File test = new File(test_url.toURI());
		URL result_url = classLoader.getResource(resultData);
		File result = new File(result_url.toURI());
		
		String pathcontext = testData.substring(0,testData.lastIndexOf('/'));
		pathcontext = context+pathcontext.substring(pathcontext.lastIndexOf('/')+1,pathcontext.length())+"/"+testData.substring(testData.lastIndexOf('/')+1,testData.length());
		//System.out.println(pathcontext);
		curi = new CrawleableUri(new URI(pathcontext));
		
		collector.openSinkForUri(curi);
		sink.openSinkForUri(curi);
		analyzer.analyze(curi, test, sink);
		collector.closeSinkForUri(curi);
		sink.closeSinkForUri(curi);
		
		List<byte[]> tdp = sink.getCrawledUnstructuredData().get(pathcontext);
		String decodedtest = "";
 		if(tdp != null) decodedtest= new String(tdp.get(0), "UTF-8");
		//if(!decodedtest.equals(""))decodedtest = decodedtest.substring(0, decodedtest.length()-1);
		
		Model decodedmodel = createModelFromN3Strings(decodedtest);
		System.out.print("created decodemodel ");
				
		//System.out.println(decodedtest);
		//System.out.println();
		
//		String correctresult = Files.readLines(result, Charset.forName("utf-8")).toString().replaceAll(", " ,"\n");
//	    correctresult = correctresult.substring(1,correctresult.length()-1);
		String correctresult = fileToString(result);		
		Model correctmodel = createModelFromTurtle(correctresult);
		System.out.print("created correctmodel ");
		correctObject(correctmodel, context);
			
		//System.out.println(turtleresult);
		//System.out.println();
		
		List<Double> results = new ArrayList<Double>();
		double fn = 0;
		double fp = 0;
		double tp = 0;
		Set<Statement> missingstatements = getMissingStatements(correctmodel, decodedmodel);
		for (Statement statement : missingstatements) {
//			falsenegativ[index]++;
			fn++;
		}
		System.out.println();
		Set<Statement> morestatements = getMissingStatements(decodedmodel, correctmodel);
		for (Statement statement : morestatements) {
//			falsepositiv[index]++;
			fp++;
		}
//		truepositiv[index]+=correctmodel.size()-falsenegativ[index];
		tp= correctmodel.size()-fn;
		results.add(tp);
		results.add(fp);
		results.add(fn);			
		testresults.put(strindex,results);		
		System.out.println();
		
		if(fn != 0) {
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
		assertEquals(0.0,fn,0.0);
	}
	
	@AfterClass
	public static void binaryclassifiers() throws URISyntaxException {
		double[] pre = new double[testresults.size()]; //The Array for the Precision of each test
		double[] rec = new double[testresults.size()]; //The Array for the Recall of each test
		double[] fsc = new double[testresults.size()]; //The Array for the F1 score of each test
		double tpsum = 0;
		double fpsum = 0;
		double fnsum = 0;
		int index = 0;
		Iterator ite = testresults.entrySet().iterator();
		while(ite.hasNext()) {
			Map.Entry pair = (Map.Entry)ite.next();
			List<Double> tmp = (List<Double>)pair.getValue();	// The Values have the order TruePositiv, FalsePositiv, FalseNegativ
			double tp = tmp.get(0);
			double fp = tmp.get(1);
			double fn = tmp.get(2);
			tpsum+=tp;
			fpsum+=fp;
			fnsum+=fn;
			if((tp+fp) != 0)pre[index] = tp/(tp+fp);
			else pre[index] = 0;
			if((tp+fn) != 0)rec[index] = tp/(tp+fn);
			else rec[index] = 0;
			if(pre[index] != 0 && rec[index] != 0)fsc[index]= 2 / ( (1/pre[index] ) + (1/rec[index] ) );
			else fsc[index] = 0;
			index++;
		}
		
		double psum = sumdoublearray(pre);
		double rsum = sumdoublearray(rec);
		double fsum = sumdoublearray(fsc);
		
		double macrop = (1.0/pre.length)*psum;
		double macror = (1.0/rec.length)*rsum;
		
		double microp = (tpsum/(tpsum+fpsum));
		double micror = (tpsum/(tpsum+fnsum));
		
		double macrofscore = (1.0/fsc.length)*fsum;
		//double macrofscore = 2 / ( (1/macrop) + (1/macror) );
		double microfscore = 2 / ( (1/microp) + (1/micror) );
		
		System.out.println("Macro F-Score");
		System.out.println(macrofscore);
		System.out.println("Micro F-Score");
		System.out.println(microfscore);
		System.out.println();
		System.out.println("Macro Precision");
		System.out.println(macrop);
		System.out.println("Macro Recall");
		System.out.println(macror);
		System.out.println("Micro Precision");
		System.out.println(microp);
		System.out.println("Micro Recall");
		System.out.println(micror);		
	}
		
}