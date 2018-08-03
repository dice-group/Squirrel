package org.aksw.simba.squirrel.analyzer.impl;

import static org.junit.Assert.assertEquals;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
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

public class MicroformatParserTest extends RDFParserTest {

	private static String context = "http://rdfa.info/test-suite/test-cases/rdfa1.1/";
	private static String pathextensionmixed = "\\html_scraper_analyzer\\MicroformatParserTestResources\\microformats-mixed\\";
	private static String pathextensionv1 = "\\html_scraper_analyzer\\MicroformatParserTestResources\\microformats-v1\\";
	private static String pathextensionv2 = "\\html_scraper_analyzer\\MicroformatParserTestResources\\microformats-v2\\";
	
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
		analyzer = new MicroformatMF2JParser();
	}
	
	@Parameter(0)
    public String testData;
    @Parameter(1)
    public String resultData; 
    @Rule public TestName test = new TestName();
    
	
    @Parameters(name = "{index},{0},{1}")
    public static Collection<Object[]> data() {
        Object[][] data = new Object[][] { //	"@context": {"@vocab": "http://www.w3.org/2006/vcard/ns#"},
        	{ pathextensionmixed+"h-card\\"+"mixedpropertries.html", pathextensionmixed+"h-card\\"+"mixedpropertries.json" },
        	{ pathextensionmixed+"h-card\\"+"tworoots.html", pathextensionmixed+"h-card\\"+"tworoots.json" },
        	{pathextensionmixed+"h-entry\\"+"mixedroots.html", pathextensionmixed+"h-entry\\"+"mixedroots.json" },
        	{pathextensionmixed+"h-resume\\"+"mixedroots.html", pathextensionmixed+"h-resume\\"+"mixedroots.json" },
        	{pathextensionv1+"adr\\"+"simpleproperties.html", pathextensionv1+"adr\\"+"simpleproperties.json" },
        	{pathextensionv1+"geo\\"+"abbrpattern.html", pathextensionv1+"geo\\"+"abbrpattern.json" },
        	{pathextensionv1+"geo\\"+"hidden.html", pathextensionv1+"geo\\"+"hidden.json" },
        	{pathextensionv1+"geo\\"+"simpleproperties.html", pathextensionv1+"geo\\"+"simpleproperties.json" },
        	{pathextensionv1+"geo\\"+"valuetitleclass.html", pathextensionv1+"geo\\"+"valuetitleclass.json" },
//        	{pathextensionv1+"hcalendar\\"+"ampm.html", pathextensionv1+"hcalendar\\"+"ampm.json" },
//        	{pathextensionv1+"hcalendar\\"+"attendees.html", pathextensionv1+"hcalendar\\"+"attendees.json" },
//        	{pathextensionv1+"hcalendar\\"+"combining.html", pathextensionv1+"hcalendar\\"+"combining.json" },
//        	{pathextensionv1+"hcalendar\\"+"concatenate.html", pathextensionv1+"hcalendar\\"+"concatenate.json" },
//        	{pathextensionv1+"hcalendar\\"+"time.html", pathextensionv1+"hcalendar\\"+"time.json" },
        	{pathextensionv1+"hcard\\"+"email.html", pathextensionv1+"hcard\\"+"email.json" },
        	{pathextensionv1+"hcard\\"+"format.html", pathextensionv1+"hcard\\"+"format.json" },
        	/*{pathextensionv1+"hcard\\"+"hyperlinkedphoto.html", pathextensionv1+"hcard\\"+"hyperlinkedphoto.json" },
        	{pathextensionv1+"hcard\\"+"justahyperlink.html", pathextensionv1+"hcard\\"+"justahyperlink.json" },
        	{pathextensionv1+"hcard\\"+"justaname.html", pathextensionv1+"hcard\\"+"justaname.json" },
        	{pathextensionv1+"hcard\\"+"multiple.html", pathextensionv1+"hcard\\"+"multiple.json" },
        	{pathextensionv1+"hcard\\"+"name.html", pathextensionv1+"hcard\\"+"name.json" },
        	{pathextensionv1+"hcard\\"+"single.html", pathextensionv1+"hcard\\"+"single.json" },
//        	{pathextensionv1+"hentry\\"+"summarycontent.html", pathextensionv1+"hentry\\"+"summarycontent.json" },
//        	{pathextensionv1+"hfeed\\"+"simple.html", pathextensionv1+"hfeed\\"+"simple.json" },
//        	{pathextensionv1+"hnews\\"+"all.html", pathextensionv1+"hnews\\"+"all.json" },
//        	{pathextensionv1+"hnews\\"+"minimum.html", pathextensionv1+"hnews\\"+"minimum.json" },
//        	{pathextensionv1+"hproduct\\"+"aggregate.html", pathextensionv1+"hproduct\\"+"aggregate.json" },
//        	{pathextensionv1+"hproduct\\"+"simpleproperties.html", pathextensionv1+"hproduct\\"+"simpleproperties.json" },
        	{pathextensionv1+"hresume\\"+"affiliation.html", pathextensionv1+"hresume\\"+"affiliation.json" },
        	{pathextensionv1+"hresume\\"+"contact.html", pathextensionv1+"hresume\\"+"contact.json" },
        	{pathextensionv1+"hresume\\"+"education.html", pathextensionv1+"hresume\\"+"education.json" },
        	{pathextensionv1+"hresume\\"+"skill.html", pathextensionv1+"hresume\\"+"skill.json" },
        	{pathextensionv1+"hresume\\"+"work.html", pathextensionv1+"hresume\\"+"work.json" },
        	{pathextensionv1+"hreview\\"+"item.html", pathextensionv1+"hreview\\"+"item.json" },
        	{pathextensionv1+"hreview\\"+"vcard.html", pathextensionv1+"hreview\\"+"vcard.json" },
//        	{pathextensionv1+"hreview-aggregate\\"+"hcard.html", pathextensionv1+"hreview-aggregate\\"+"hcard.json" },
//        	{pathextensionv1+"hreview-aggregate\\"+"justahyperlink.html", pathextensionv1+"hreview-aggregate\\"+"justahyperlink.json" },
//        	{pathextensionv1+"hreview-aggregate\\"+"vevent.html", pathextensionv1+"hreview-aggregate\\"+"vevent.json" },
        	{pathextensionv1+"includes\\"+"hcarditemref.html", pathextensionv1+"includes\\"+"hcarditemref.json" },
        	{pathextensionv1+"includes\\"+"heventitemref.html", pathextensionv1+"includes\\"+"heventitemref.json" },
        	{pathextensionv1+"includes\\"+"hyperlink.html", pathextensionv1+"includes\\"+"hyperlink.json" },
        	{pathextensionv1+"includes\\"+"object.html", pathextensionv1+"includes\\"+"object.json" },
        	{pathextensionv1+"includes\\"+"table.html", pathextensionv1+"includes\\"+"table.json" },
        	{pathextensionv2+"h-adr\\"+"geo.html", pathextensionv2+"h-adr\\"+"geo.json" },
        	{pathextensionv2+"h-adr\\"+"geourl.html", pathextensionv2+"h-adr\\"+"geourl.json" },
        	{pathextensionv2+"h-adr\\"+"justaname.html", pathextensionv2+"h-adr\\"+"justaname.json" },
        	{pathextensionv2+"h-adr\\"+"lettercase.html", pathextensionv2+"h-adr\\"+"lettercase.json" },
        	{pathextensionv2+"h-adr\\"+"simpleproperties.html", pathextensionv2+"h-adr\\"+"simpleproperties.json" },
        	{pathextensionv2+"h-card\\"+"baseurl.html", pathextensionv2+"h-card\\"+"baseurl.json" },
        	{pathextensionv2+"h-card\\"+"childimplied.html", pathextensionv2+"h-card\\"+"childimplied.json" },
        	{pathextensionv2+"h-card\\"+"extendeddescription.html", pathextensionv2+"h-card\\"+"extendeddescription.json" },
        	{pathextensionv2+"h-card\\"+"hcard.html", pathextensionv2+"h-card\\"+"hcard.json" },
        	{pathextensionv2+"h-card\\"+"hyperlinkedphoto.html", pathextensionv2+"h-card\\"+"hyperlinkedphoto.json" },
        	{pathextensionv2+"h-card\\"+"impliedname.html", pathextensionv2+"h-card\\"+"impliedname.json" },
        	{pathextensionv2+"h-card\\"+"impliedphoto.html", pathextensionv2+"h-card\\"+"impliedphoto.json" },
        	{pathextensionv2+"h-card\\"+"impliedurl.html", pathextensionv2+"h-card\\"+"impliedurl.json" },
        	{pathextensionv2+"h-card\\"+"impliedurlempty.html", pathextensionv2+"h-card\\"+"impliedurlempty.json" },
        	{pathextensionv2+"h-card\\"+"justahyperlink.html", pathextensionv2+"h-card\\"+"justahyperlink.json" },
        	{pathextensionv2+"h-card\\"+"justaname.html", pathextensionv2+"h-card\\"+"justaname.json" },
        	{pathextensionv2+"h-card\\"+"nested.html", pathextensionv2+"h-card\\"+"nested.json" },
        	{pathextensionv2+"h-card\\"+"p-property.html", pathextensionv2+"h-card\\"+"p-property.json" },
        	{pathextensionv2+"h-card\\"+"relativeurls.html", pathextensionv2+"h-card\\"+"relativeurls.json" },
        	{pathextensionv2+"h-card\\"+"relativeurlsempty.html", pathextensionv2+"h-card\\"+"relativeurlsempty.json" },
//        	{pathextensionv2+"h-entry\\"+"encoding.html", pathextensionv2+"h-entry\\"+"encoding.json" },
//        	{pathextensionv2+"h-entry\\"+"impliedvalue-nested.html", pathextensionv2+"h-entry\\"+"impliedvalue-nested.json" },
//        	{pathextensionv2+"h-entry\\"+"justahyperlink.html", pathextensionv2+"h-entry\\"+"justahyperlink.json" },
//        	{pathextensionv2+"h-entry\\"+"justaname.html", pathextensionv2+"h-entry\\"+"justaname.json" },
//        	{pathextensionv2+"h-entry\\"+"scriptstyletags.html", pathextensionv2+"h-entry\\"+"scriptstyletags.json" },
//        	{pathextensionv2+"h-entry\\"+"summarycontent.html", pathextensionv2+"h-entry\\"+"summarycontent.json" },
//        	{pathextensionv2+"h-entry\\"+"u-property.html", pathextensionv2+"h-entry\\"+"u-property.json" },
//        	{pathextensionv2+"h-entry\\"+"urlincontent.html", pathextensionv2+"h-entry\\"+"urlincontent.json" },
        	{pathextensionv2+"h-event\\"+"ampm.html", pathextensionv2+"h-event\\"+"ampm.json" },
        	{pathextensionv2+"h-event\\"+"attendees.html", pathextensionv2+"h-event\\"+"attendees.json" },
        	{pathextensionv2+"h-event\\"+"combining.html", pathextensionv2+"h-event\\"+"combining.json" },
        	{pathextensionv2+"h-event\\"+"concatenate.html", pathextensionv2+"h-event\\"+"concatenate.json" },
        	{pathextensionv2+"h-event\\"+"dates.html", pathextensionv2+"h-event\\"+"dates.json" },
        	{pathextensionv2+"h-event\\"+"dt-property.html", pathextensionv2+"h-event\\"+"dt-property.json" },
        	{pathextensionv2+"h-event\\"+"justahyperlink.html", pathextensionv2+"h-event\\"+"justahyperlink.json" },
        	{pathextensionv2+"h-event\\"+"justaname.html", pathextensionv2+"h-event\\"+"justaname.json" },
        	{pathextensionv2+"h-event\\"+"time.html", pathextensionv2+"h-event\\"+"time.json" },
//        	{pathextensionv2+"h-feed\\"+"implied-title.html", pathextensionv2+"h-feed\\"+"implied-title.json" },
//        	{pathextensionv2+"h-feed\\"+"simple.html", pathextensionv2+"h-feed\\"+"simple.json" },
        	{pathextensionv2+"h-geo\\"+"abbrpattern.html", pathextensionv2+"h-geo\\"+"abbrpattern.json" },
        	{pathextensionv2+"h-geo\\"+"altitude.html", pathextensionv2+"h-geo\\"+"altitude.json" },
        	{pathextensionv2+"h-geo\\"+"hidden.html", pathextensionv2+"h-geo\\"+"hidden.json" },
        	{pathextensionv2+"h-geo\\"+"justaname.html", pathextensionv2+"h-geo\\"+"justaname.json" },
        	{pathextensionv2+"h-geo\\"+"simpleproperties.html", pathextensionv2+"h-geo\\"+"simpleproperties.json" },
        	{pathextensionv2+"h-geo\\"+"valuetitleclass.html", pathextensionv2+"h-geo\\"+"valuetitleclass.json" },
//        	{pathextensionv2+"h-product\\"+"aggregate.html", pathextensionv2+"h-product\\"+"aggregate.json" },
//        	{pathextensionv2+"h-product\\"+"justahyperlink.html", pathextensionv2+"h-product\\"+"justahyperlink.json" },
//        	{pathextensionv2+"h-product\\"+"justaname.html", pathextensionv2+"h-product\\"+"justaname.json" },
//        	{pathextensionv2+"h-product\\"+"simpleproperties.html", pathextensionv2+"h-product\\"+"simpleproperties.json" },
//        	{pathextensionv2+"h-recipe\\"+"all.html", pathextensionv2+"h-recipe\\"+"all.json" },
//        	{pathextensionv2+"h-recipe\\"+"minimum.html", pathextensionv2+"h-recipe\\"+"minimum.json" },
        	{pathextensionv2+"h-resume\\"+"affiliation.html", pathextensionv2+"h-resume\\"+"affiliation.json" },
        	{pathextensionv2+"h-resume\\"+"contact.html", pathextensionv2+"h-resume\\"+"contact.json" },
        	{pathextensionv2+"h-resume\\"+"education.html", pathextensionv2+"h-resume\\"+"education.json" },
        	{pathextensionv2+"h-resume\\"+"justaname.html", pathextensionv2+"h-resume\\"+"justaname.json" },
        	{pathextensionv2+"h-resume\\"+"skill.html", pathextensionv2+"h-resume\\"+"skill.json" },
        	{pathextensionv2+"h-resume\\"+"work.html", pathextensionv2+"h-resume\\"+"work.json" },
        	{pathextensionv2+"h-review\\"+"hyperlink.html", pathextensionv2+"h-review\\"+"hyperlink.json" },
        	{pathextensionv2+"h-review\\"+"implieditem.html", pathextensionv2+"h-review\\"+"implieditem.json" },
        	{pathextensionv2+"h-review\\"+"item.html", pathextensionv2+"h-review\\"+"item.json" },
        	{pathextensionv2+"h-review\\"+"justaname.html", pathextensionv2+"h-review\\"+"justaname.json" },
        	{pathextensionv2+"h-review\\"+"photo.html", pathextensionv2+"h-review\\"+"photo.json" },
        	{pathextensionv2+"h-review\\"+"vcard.html", pathextensionv2+"h-review\\"+"vcard.json" },
//        	{pathextensionv2+"h-review-aggregate\\"+"hevent.html", pathextensionv2+"h-review-aggregate\\"+"hevent.json" },
//        	{pathextensionv2+"h-review-aggregate\\"+"justahyperlink.html", pathextensionv2+"h-review-aggregate\\"+"justahyperlink.json" },
//        	{pathextensionv2+"h-review-aggregate\\"+"simpleproperties.html", pathextensionv2+"h-review-aggregate\\"+"simpleproperties.json" },
//        	{pathextensionv2+"rel\\"+"duplicate-rels.html", pathextensionv2+"rel\\"+"duplicate-rels.json" },
        	{pathextensionv2+"rel\\"+"license.html", pathextensionv2+"rel\\"+"license.json" },
//        	{pathextensionv2+"rel\\"+"nofollow.html", pathextensionv2+"rel\\"+"nofollow.json" },
//        	{pathextensionv2+"rel\\"+"rel-urls.html", pathextensionv2+"rel\\"+"rel-urls.json" },
//        	{pathextensionv2+"rel\\"+"varying-text-duplicate-rels.html", pathextensionv2+"rel\\"+"varying-text-duplicate-rels.json" },
//        	{pathextensionv2+"rel\\"+"xfn-all.html", pathextensionv2+"rel\\"+"xfn-all.json" },
//        	{pathextensionv2+"rel\\"+"xfn-elsewhere.html", pathextensionv2+"rel\\"+"xfn-elsewhere.json" },*/
        	
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
		correctresult = addContextToJSON(correctresult);
		correctresult = replaceVocab(correctresult);
		Model correctmodel = createModelFromJSONLD(correctresult);
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
		
		//if(falsenegativ[index] != 0) {
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
		//}
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

	}
	
	private static String fileToString(File file) throws FileNotFoundException, IOException {
		String data = "";
		try (BufferedReader br = new BufferedReader(new FileReader(file))) {
		    String line;
		    while ((line = br.readLine()) != null) {
		       data+= line+"\n";
		    }
		}	
		return data;
	}
	
	private static String addContextToJSON(String data) {
		data = data.substring(1);
		data = "{\r\n" + 
				"\"@context\": {\"@vocab\": \"http://www.dummy.org/\"},\n"+data;
		return data;
	}
	
	private static String replaceVocab(String data) {
		return data.replace("http://www.dummy.org/", "http://www.w3.org/2006/vcard/ns#");
	}
	
}
