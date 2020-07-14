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
import org.apache.jena.rdf.model.Property;
import org.apache.jena.rdf.model.RDFNode;
import org.apache.jena.rdf.model.Resource;
import org.apache.jena.rdf.model.ResourceFactory;
import org.apache.jena.rdf.model.Statement;
import org.apache.jena.rdf.model.StmtIterator;
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
import org.junit.runners.Parameterized.Parameter;
import org.junit.runners.Parameterized.Parameters;

@Ignore
public class MicroformatParserTest extends RDFParserTest {

	private static String context = "http://example.com/";
	private static String pathextensionmixed = "html_scraper_analyzer/MicroformatParserTestResources/microformats-mixed/";
	private static String pathextensionv1 = "html_scraper_analyzer/MicroformatParserTestResources/microformats-v1/";
	private static String pathextensionv2 = "html_scraper_analyzer/MicroformatParserTestResources/microformats-v2/";
	
	private static Analyzer analyzer;
	private static UriCollector collector = new SimpleUriCollector(new GzipJavaUriSerializer());
	private CrawleableUri curi;
	private static InMemorySink sink;
	public static Map<String, List<Double>> testresults = new HashMap<String,List<Double>>();
	@Parameter(0)
    public String testData;
    @Parameter(1)
    public String resultData; 
    @Rule public TestName test = new TestName();
	

    @Parameters(name = "{index},{0},{1}")
    public static Collection<Object[]> data() {
        Object[][] data = new Object[][] { //	"@context": {"@vocab": "http://www.w3.org/2006/vcard/ns#"},
        	{ pathextensionmixed+"h-card/"+"mixedpropertries.html", pathextensionmixed+"h-card/"+"mixedpropertries.json" },
        	{ pathextensionmixed+"h-card/"+"tworoots.html", pathextensionmixed+"h-card/"+"tworoots.json" },
        	{pathextensionmixed+"h-entry/"+"mixedroots.html", pathextensionmixed+"h-entry/"+"mixedroots.json" },
        	{pathextensionmixed+"h-resume/"+"mixedroots.html", pathextensionmixed+"h-resume/"+"mixedroots.json" },
        	{pathextensionv1+"adr/"+"simpleproperties.html", pathextensionv1+"adr/"+"simpleproperties.json" },
        	{pathextensionv1+"geo/"+"abbrpattern.html", pathextensionv1+"geo/"+"abbrpattern.json" },
        	{pathextensionv1+"geo/"+"hidden.html", pathextensionv1+"geo/"+"hidden.json" },
        	{pathextensionv1+"geo/"+"simpleproperties.html", pathextensionv1+"geo/"+"simpleproperties.json" },
        	{pathextensionv1+"geo/"+"valuetitleclass.html", pathextensionv1+"geo/"+"valuetitleclass.json" },
        	{pathextensionv1+"hcalendar/"+"ampm.html", pathextensionv1+"hcalendar/"+"ampm.json" },
        	{pathextensionv1+"hcalendar/"+"attendees.html", pathextensionv1+"hcalendar/"+"attendees.json" },
        	{pathextensionv1+"hcalendar/"+"combining.html", pathextensionv1+"hcalendar/"+"combining.json" },
        	{pathextensionv1+"hcalendar/"+"concatenate.html", pathextensionv1+"hcalendar/"+"concatenate.json" },
        	{pathextensionv1+"hcalendar/"+"time.html", pathextensionv1+"hcalendar/"+"time.json" },
        	{pathextensionv1+"hcard/"+"email.html", pathextensionv1+"hcard/"+"email.json" },
        	{pathextensionv1+"hcard/"+"format.html", pathextensionv1+"hcard/"+"format.json" },
        	{pathextensionv1+"hcard/"+"hyperlinkedphoto.html", pathextensionv1+"hcard/"+"hyperlinkedphoto.json" },
        	{pathextensionv1+"hcard/"+"justahyperlink.html", pathextensionv1+"hcard/"+"justahyperlink.json" },
        	{pathextensionv1+"hcard/"+"justaname.html", pathextensionv1+"hcard/"+"justaname.json" },
        	{pathextensionv1+"hcard/"+"multiple.html", pathextensionv1+"hcard/"+"multiple.json" },
        	{pathextensionv1+"hcard/"+"name.html", pathextensionv1+"hcard/"+"name.json" },
        	{pathextensionv1+"hcard/"+"single.html", pathextensionv1+"hcard/"+"single.json" },
        	{pathextensionv1+"hentry/"+"summarycontent.html", pathextensionv1+"hentry/"+"summarycontent.json" },
        	{pathextensionv1+"hfeed/"+"simple.html", pathextensionv1+"hfeed/"+"simple.json" },
        	{pathextensionv1+"hnews/"+"all.html", pathextensionv1+"hnews/"+"all.json" },
        	{pathextensionv1+"hnews/"+"minimum.html", pathextensionv1+"hnews/"+"minimum.json" },
        	{pathextensionv1+"hproduct/"+"aggregate.html", pathextensionv1+"hproduct/"+"aggregate.json" },
        	{pathextensionv1+"hproduct/"+"simpleproperties.html", pathextensionv1+"hproduct/"+"simpleproperties.json" },
        	{pathextensionv1+"hresume/"+"affiliation.html", pathextensionv1+"hresume/"+"affiliation.json" },
        	{pathextensionv1+"hresume/"+"contact.html", pathextensionv1+"hresume/"+"contact.json" },
        	{pathextensionv1+"hresume/"+"education.html", pathextensionv1+"hresume/"+"education.json" },
        	{pathextensionv1+"hresume/"+"skill.html", pathextensionv1+"hresume/"+"skill.json" },
        	{pathextensionv1+"hresume/"+"work.html", pathextensionv1+"hresume/"+"work.json" },
            {pathextensionv1+"hreview/"+"item.html", pathextensionv1+"hreview/"+"item.json" },
        	{pathextensionv1+"hreview/"+"vcard.html", pathextensionv1+"hreview/"+"vcard.json" },
        	{pathextensionv1+"hreview-aggregate/"+"hcard.html", pathextensionv1+"hreview-aggregate/"+"hcard.json" },
        	{pathextensionv1+"hreview-aggregate/"+"justahyperlink.html", pathextensionv1+"hreview-aggregate/"+"justahyperlink.json" },
        	{pathextensionv1+"hreview-aggregate/"+"vevent.html", pathextensionv1+"hreview-aggregate/"+"vevent.json" },
        	{pathextensionv1+"includes/"+"hcarditemref.html", pathextensionv1+"includes/"+"hcarditemref.json" },
        	{pathextensionv1+"includes/"+"heventitemref.html", pathextensionv1+"includes/"+"heventitemref.json" },
        	{pathextensionv1+"includes/"+"hyperlink.html", pathextensionv1+"includes/"+"hyperlink.json" },
        	{pathextensionv1+"includes/"+"object.html", pathextensionv1+"includes/"+"object.json" },
        	{pathextensionv1+"includes/"+"table.html", pathextensionv1+"includes/"+"table.json" },//*/
        	//Any23 kann nur bis Microformats-v1 die Modelle höherer Version sind leer
        	{pathextensionv2+"h-adr/"+"geo.html", pathextensionv2+"h-adr/"+"geo.json" },
        	{pathextensionv2+"h-adr/"+"geourl.html", pathextensionv2+"h-adr/"+"geourl.json" },
        	{pathextensionv2+"h-adr/"+"justaname.html", pathextensionv2+"h-adr/"+"justaname.json" },
        	{pathextensionv2+"h-adr/"+"lettercase.html", pathextensionv2+"h-adr/"+"lettercase.json" },
        	{pathextensionv2+"h-adr/"+"simpleproperties.html", pathextensionv2+"h-adr/"+"simpleproperties.json" },
        	{pathextensionv2+"h-card/"+"baseurl.html", pathextensionv2+"h-card/"+"baseurl.json" },
        	{pathextensionv2+"h-card/"+"childimplied.html", pathextensionv2+"h-card/"+"childimplied.json" },
        	{pathextensionv2+"h-card/"+"extendeddescription.html", pathextensionv2+"h-card/"+"extendeddescription.json" },
        	{pathextensionv2+"h-card/"+"hcard.html", pathextensionv2+"h-card/"+"hcard.json" },
        	{pathextensionv2+"h-card/"+"hyperlinkedphoto.html", pathextensionv2+"h-card/"+"hyperlinkedphoto.json" },
        	{pathextensionv2+"h-card/"+"impliedname.html", pathextensionv2+"h-card/"+"impliedname.json" },
        	{pathextensionv2+"h-card/"+"impliedphoto.html", pathextensionv2+"h-card/"+"impliedphoto.json" },
        	{pathextensionv2+"h-card/"+"impliedurl.html", pathextensionv2+"h-card/"+"impliedurl.json" },
        	{pathextensionv2+"h-card/"+"impliedurlempty.html", pathextensionv2+"h-card/"+"impliedurlempty.json" },
        	{pathextensionv2+"h-card/"+"justahyperlink.html", pathextensionv2+"h-card/"+"justahyperlink.json" },
        	{pathextensionv2+"h-card/"+"justaname.html", pathextensionv2+"h-card/"+"justaname.json" },
        	{pathextensionv2+"h-card/"+"nested.html", pathextensionv2+"h-card/"+"nested.json" },
        	{pathextensionv2+"h-card/"+"p-property.html", pathextensionv2+"h-card/"+"p-property.json" },
        	{pathextensionv2+"h-card/"+"relativeurls.html", pathextensionv2+"h-card/"+"relativeurls.json" },
        	{pathextensionv2+"h-card/"+"relativeurlsempty.html", pathextensionv2+"h-card/"+"relativeurlsempty.json" },
        	{pathextensionv2+"h-entry/"+"encoding.html", pathextensionv2+"h-entry/"+"encoding.json" },
        	{pathextensionv2+"h-entry/"+"impliedvalue-nested.html", pathextensionv2+"h-entry/"+"impliedvalue-nested.json" },
        	{pathextensionv2+"h-entry/"+"justahyperlink.html", pathextensionv2+"h-entry/"+"justahyperlink.json" },
        	{pathextensionv2+"h-entry/"+"justaname.html", pathextensionv2+"h-entry/"+"justaname.json" },
        	{pathextensionv2+"h-entry/"+"scriptstyletags.html", pathextensionv2+"h-entry/"+"scriptstyletags.json" },
        	{pathextensionv2+"h-entry/"+"summarycontent.html", pathextensionv2+"h-entry/"+"summarycontent.json" },
        	{pathextensionv2+"h-entry/"+"u-property.html", pathextensionv2+"h-entry/"+"u-property.json" },
        	{pathextensionv2+"h-entry/"+"urlincontent.html", pathextensionv2+"h-entry/"+"urlincontent.json" },
        	{pathextensionv2+"h-event/"+"ampm.html", pathextensionv2+"h-event/"+"ampm.json" },
        	{pathextensionv2+"h-event/"+"attendees.html", pathextensionv2+"h-event/"+"attendees.json" },
        	{pathextensionv2+"h-event/"+"combining.html", pathextensionv2+"h-event/"+"combining.json" },
        	{pathextensionv2+"h-event/"+"concatenate.html", pathextensionv2+"h-event/"+"concatenate.json" },
        	{pathextensionv2+"h-event/"+"dates.html", pathextensionv2+"h-event/"+"dates.json" },
        	{pathextensionv2+"h-event/"+"dt-property.html", pathextensionv2+"h-event/"+"dt-property.json" },
        	{pathextensionv2+"h-event/"+"justahyperlink.html", pathextensionv2+"h-event/"+"justahyperlink.json" },
        	{pathextensionv2+"h-event/"+"justaname.html", pathextensionv2+"h-event/"+"justaname.json" },
        	{pathextensionv2+"h-event/"+"time.html", pathextensionv2+"h-event/"+"time.json" },
        	{pathextensionv2+"h-feed/"+"implied-title.html", pathextensionv2+"h-feed/"+"implied-title.json" },
        	{pathextensionv2+"h-feed/"+"simple.html", pathextensionv2+"h-feed/"+"simple.json" },
        	{pathextensionv2+"h-geo/"+"abbrpattern.html", pathextensionv2+"h-geo/"+"abbrpattern.json" },
        	{pathextensionv2+"h-geo/"+"altitude.html", pathextensionv2+"h-geo/"+"altitude.json" },
        	{pathextensionv2+"h-geo/"+"hidden.html", pathextensionv2+"h-geo/"+"hidden.json" },
        	{pathextensionv2+"h-geo/"+"justaname.html", pathextensionv2+"h-geo/"+"justaname.json" },
        	{pathextensionv2+"h-geo/"+"simpleproperties.html", pathextensionv2+"h-geo/"+"simpleproperties.json" },
        	{pathextensionv2+"h-geo/"+"valuetitleclass.html", pathextensionv2+"h-geo/"+"valuetitleclass.json" },
        	{pathextensionv2+"h-product/"+"aggregate.html", pathextensionv2+"h-product/"+"aggregate.json" },
        	{pathextensionv2+"h-product/"+"justahyperlink.html", pathextensionv2+"h-product/"+"justahyperlink.json" },
        	{pathextensionv2+"h-product/"+"justaname.html", pathextensionv2+"h-product/"+"justaname.json" },
        	{pathextensionv2+"h-product/"+"simpleproperties.html", pathextensionv2+"h-product/"+"simpleproperties.json" },
        	{pathextensionv2+"h-recipe/"+"all.html", pathextensionv2+"h-recipe/"+"all.json" },
        	{pathextensionv2+"h-recipe/"+"minimum.html", pathextensionv2+"h-recipe/"+"minimum.json" },
        	{pathextensionv2+"h-resume/"+"affiliation.html", pathextensionv2+"h-resume/"+"affiliation.json" },
        	{pathextensionv2+"h-resume/"+"contact.html", pathextensionv2+"h-resume/"+"contact.json" },
        	{pathextensionv2+"h-resume/"+"education.html", pathextensionv2+"h-resume/"+"education.json" },
        	{pathextensionv2+"h-resume/"+"justaname.html", pathextensionv2+"h-resume/"+"justaname.json" },
        	{pathextensionv2+"h-resume/"+"skill.html", pathextensionv2+"h-resume/"+"skill.json" },
        	{pathextensionv2+"h-resume/"+"work.html", pathextensionv2+"h-resume/"+"work.json" },
        	{pathextensionv2+"h-review/"+"hyperlink.html", pathextensionv2+"h-review/"+"hyperlink.json" },
        	{pathextensionv2+"h-review/"+"implieditem.html", pathextensionv2+"h-review/"+"implieditem.json" },
        	{pathextensionv2+"h-review/"+"item.html", pathextensionv2+"h-review/"+"item.json" },
        	{pathextensionv2+"h-review/"+"justaname.html", pathextensionv2+"h-review/"+"justaname.json" },
        	{pathextensionv2+"h-review/"+"photo.html", pathextensionv2+"h-review/"+"photo.json" },
        	{pathextensionv2+"h-review/"+"vcard.html", pathextensionv2+"h-review/"+"vcard.json" }, //Any23 ausnahme
        	{pathextensionv2+"h-review-aggregate/"+"hevent.html", pathextensionv2+"h-review-aggregate/"+"hevent.json" },
        	{pathextensionv2+"h-review-aggregate/"+"justahyperlink.html", pathextensionv2+"h-review-aggregate/"+"justahyperlink.json" },
        	{pathextensionv2+"h-review-aggregate/"+"simpleproperties.html", pathextensionv2+"h-review-aggregate/"+"simpleproperties.json" },
        	{pathextensionv2+"rel/"+"duplicate-rels.html", pathextensionv2+"rel/"+"duplicate-rels.json" },
        	{pathextensionv2+"rel/"+"license.html", pathextensionv2+"rel/"+"license.json" }, //Any23 ausnahme
        	{pathextensionv2+"rel/"+"nofollow.html", pathextensionv2+"rel/"+"nofollow.json" },
        	{pathextensionv2+"rel/"+"rel-urls.html", pathextensionv2+"rel/"+"rel-urls.json" },
        	{pathextensionv2+"rel/"+"varying-text-duplicate-rels.html", pathextensionv2+"rel/"+"varying-text-duplicate-rels.json" },
        	{pathextensionv2+"rel/"+"xfn-all.html", pathextensionv2+"rel/"+"xfn-all.json" }, //Any23 ausnahme
        	{pathextensionv2+"rel/"+"xfn-elsewhere.html", pathextensionv2+"rel/"+"xfn-elsewhere.json" }, //Any23 ausnahme*/
        	
        };
        return Arrays.asList(data);
    }
    
	@Test
	public void parsertest() throws URISyntaxException, IOException {
		sink = new InMemorySink();
		analyzer = new MicroformatMF2JAnalyzer(collector);
		boolean pastprocess = true; //true falls das Ergebnis im Nachhinein noch überarbeitet werden soll
		
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
		correctresult = addContextToJSON(correctresult);
		correctresult = replaceVocab(correctresult);	
		Model correctmodel = createModelFromJSONLD(correctresult);
		if(pastprocess)replacePropertieVocabs(correctmodel);
		System.out.print("created correctmodel ");
			
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
		
	public static String addContextToJSON(String data) {
		data = data.trim();
		data = data.substring(1);
		data = "{\r\n" + 
				"\"@context\": {\"@vocab\": \"http://www.dummy.org/#\"},\n"+data;
		return data;
	}
	
	public static String replaceVocab(String data) {
		return data.replace("http://www.dummy.org/#", "http://www.w3.org/2006/vcard/ns#");
	}
	
	//Jeder Eintrag muss ein Leerzeichen am Ende haben um ihn eindeutig zu machen
	private static Map<String,String> replacePredicates = new HashMap<String,String>(){
		{
			put("http://www.w3.org/2006/vcard/ns#count ", "http://purl.org/stuff/revagg#count ");
			put("http://www.w3.org/2006/vcard/ns#average ", "http://purl.org/stuff/revagg#average ");
			put("http://www.w3.org/2006/vcard/ns#best ", "http://purl.org/stuff/revagg#best ");
			put("http://www.w3.org/2006/vcard/ns#rating ", "http://purl.org/stuff/rev#rating ");
			//put("http://purl.org/stuff/revagg#country-name ", "http://www.w3.org/2006/vcard/ns#country-name "); //Unter dem http://www.w3.org/2006/vcard/ns# nutzlos
			put("http://www.w3.org/2006/vcard/ns#type ", "http://www.w3.org/1999/02/22-rdf-syntax-ns#type ");
			put("http://www.w3.org/2006/vcard/ns#location ", "http://www.w3.org/2002/12/cal/icaltzd#location ");
			put("http://www.w3.org/2006/vcard/ns#start ", "http://www.w3.org/2002/12/cal/icaltzd#dtstart ");
			put("http://www.w3.org/2006/vcard/ns#end ", "http://www.w3.org/2002/12/cal/icaltzd#dtend ");
			put("http://www.w3.org/2006/vcard/ns#url ", "http://www.w3.org/2002/12/cal/icaltzd#url ");
			put("http://www.w3.org/2006/vcard/ns#org ", "http://www.w3.org/2006/vcard/ns#organization-name ");
			put("http://www.w3.org/2006/vcard/ns#affiliation ", "http://ramonantonio.net/doac/0.1/#affiliation "); //http://ramonantonio.net/doac/0.1/# nicht gefunden!
			put("http://www.w3.org/2006/vcard/ns#summary ", "http://ramonantonio.net/doac/0.1/#summary ");
			put("http://www.w3.org/2006/vcard/ns#education ", "http://ramonantonio.net/doac/0.1/#education ");
			put("http://www.w3.org/2006/vcard/ns#experience ", "http://ramonantonio.net/doac/0.1/#experience ");
			put("http://www.w3.org/2006/vcard/ns#job-title ","http://ramonantonio.net/doac/0.1/#title ");// job-title schema unbekannt.
			put("http://www.w3.org/2006/vcard/ns#license ", "http://www.w3.org/1999/xhtml/vocab#license ");
		}
	};
	
	private static Map<String,String> replaceObjects = new HashMap<String,String>(){{
		put("h-card", "http://www.w3.org/2006/vcard/ns#VCard");
		put("h-adr", "http://www.w3.org/2006/vcard/ns#Address");
		//put("\"h-event\" ", "http://www.w3.org/2002/12/cal/icaltzd#vcalendar  ");
		put("h-event", "http://www.w3.org/2002/12/cal/icaltzd#Vevent");
		//put("\"h-resume\" ", "");
		put("h-geo", "http://www.w3.org/2006/vcard/ns#Location");
		//put("\"h-entry\" ", "");
		//put("\"h-product\" ", "");
		put("h-review", "http://purl.org/stuff/rev#Review");
		//put("\"h-item\" ", "");
		put("h-review-aggregate", "http://purl.org/stuff/revagg#ReviewAggregate");
	 }
	};
	
	public static void replacePropertieVocabs(Model model) {	
		List<Statement> oldstatements = new ArrayList<Statement>();
		List<Statement> newstatements = new ArrayList<Statement>();
		StmtIterator iter = model.listStatements();
		while (iter.hasNext()) {
			Statement stmt      = iter.nextStatement();  //statement
		    Resource  subject   = stmt.getSubject();     //subject
		    Property  predicate = stmt.getPredicate();   //predicate
		    RDFNode   object    = stmt.getObject();      //object
		    String predicatestr = predicate.toString()+" ";		//Ein Leerzeichen am Ende das Property eindeutig zu machen
		    String objectstr = object.toString();
		    if(subjectReplace(predicatestr)) {
		    	
		    	Iterator ite = replacePredicates.entrySet().iterator();
				while(ite.hasNext()) {
					Map.Entry pair = (Map.Entry)ite.next();
					String oldvalue = pair.getKey().toString();
					String newvalue = pair.getValue().toString();
					predicatestr = predicatestr.replace(oldvalue, newvalue);
				}				
				
				RDFNode newobject = null;
				if(predicatestr.contains("#type ")) {
					Iterator ite2 = replaceObjects.entrySet().iterator();
					while(ite2.hasNext()) {
						Map.Entry pair = (Map.Entry)ite2.next();
						String oldvalue = pair.getKey().toString();
						String newvalue = pair.getValue().toString();
						objectstr = objectstr.replace(oldvalue, newvalue);
					}
			    	newobject = ResourceFactory.createProperty(objectstr);
			    	System.out.println(objectstr);
				} else newobject = object;
		    	
				predicatestr = predicatestr.substring(0, predicatestr.length()-1); //Entfernt das Leerzeichen wieder was den Einträgen hinzugefügt wurde
		    	Property newpredicate = ResourceFactory.createProperty(predicatestr);
		    	
		    	Statement newstmt = ResourceFactory.createStatement(subject, newpredicate, newobject);
		    	oldstatements.add(stmt);
		    	newstatements.add(newstmt);
		    }
		    if(predicatestr.contains("#email ") || predicatestr.contains("#photo ")) {
		    	//RDFNode newobject = ResourceFactory.createStringLiteral(object.toString());
		    	RDFNode newobject = ResourceFactory.createProperty(object.toString());
		    	Statement newstmt = ResourceFactory.createStatement(subject, predicate, newobject);
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
	
	private static boolean subjectReplace(String data) {
		boolean replace =false;		
		Iterator ite = replacePredicates.entrySet().iterator();
		while(ite.hasNext()) {
			Map.Entry pair = (Map.Entry)ite.next();
			String statement = pair.getKey().toString();
			if(data.contains(statement)) {
				replace = true;
			}		
		}	
		return replace;
	}
	
}