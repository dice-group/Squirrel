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
public class RDFaParserTest extends RDFParserTest {
	
	private static String context = "http://rdfa.info/test-suite/test-cases/rdfa1.1/";
	private static String pathextensionsuit = "html_scraper_analyzer/RDFaParserTestResources/rdfa1.1/";
	private static String pathextensionhtml4 = "html4/";
	private static String pathextensionhtml5 = "html5/";
	private static String pathextensionhtml5invalid = "html5-invalid/";
	private static String pathextensionsvg = "svg/";
	private static String pathextensionxhtml1 = "xhtml1/";
	private static String pathextensionxhtml5 = "xhtml5/";
	private static String pathextensionxhtml5invalid = "xhtml5-invalid/";
	private static String pathextensionxml = "xml/";
	
	private static Analyzer analyzer1;
	private UriCollector collector = new SimpleUriCollector(new GzipJavaUriSerializer());
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
        Object[][] data = new Object[][] { //TestZeile= Test+78
        	{ pathextensionsuit+pathextensionhtml4+"0001.html",pathextensionsuit+pathextensionhtml4+"0001.ttl" },	//HMTL4
        	{ pathextensionsuit+pathextensionhtml4+"0006.html",pathextensionsuit+pathextensionhtml4+"0006.ttl" },
        	{ pathextensionsuit+pathextensionhtml4+"0007.html",pathextensionsuit+pathextensionhtml4+"0007.ttl" },
        	{ pathextensionsuit+pathextensionhtml4+"0008.html",pathextensionsuit+pathextensionhtml4+"0008.ttl" },
        	{ pathextensionsuit+pathextensionhtml4+"0009.html",pathextensionsuit+pathextensionhtml4+"0009.ttl" },
        	{ pathextensionsuit+pathextensionhtml4+"0010.html",pathextensionsuit+pathextensionhtml4+"0010.ttl" },
        	{ pathextensionsuit+pathextensionhtml4+"0014.html",pathextensionsuit+pathextensionhtml4+"0014.ttl" },
        	{ pathextensionsuit+pathextensionhtml4+"0015.html",pathextensionsuit+pathextensionhtml4+"0015.ttl" },
        	{ pathextensionsuit+pathextensionhtml4+"0017.html",pathextensionsuit+pathextensionhtml4+"0017.ttl" },
        	{ pathextensionsuit+pathextensionhtml4+"0018.html",pathextensionsuit+pathextensionhtml4+"0018.ttl" },
        	{ pathextensionsuit+pathextensionhtml4+"0019.html",pathextensionsuit+pathextensionhtml4+"0019.ttl" },
        	{ pathextensionsuit+pathextensionhtml4+"0020.html",pathextensionsuit+pathextensionhtml4+"0020.ttl" },
        	{ pathextensionsuit+pathextensionhtml4+"0021.html",pathextensionsuit+pathextensionhtml4+"0021.ttl" },
        	{ pathextensionsuit+pathextensionhtml4+"0023.html",pathextensionsuit+pathextensionhtml4+"0023.ttl" },
        	{ pathextensionsuit+pathextensionhtml4+"0025.html",pathextensionsuit+pathextensionhtml4+"0025.ttl" },
        	{ pathextensionsuit+pathextensionhtml4+"0026.html",pathextensionsuit+pathextensionhtml4+"0026.ttl" },
        	{ pathextensionsuit+pathextensionhtml4+"0027.html",pathextensionsuit+pathextensionhtml4+"0027.ttl" },
        	{ pathextensionsuit+pathextensionhtml4+"0029.html",pathextensionsuit+pathextensionhtml4+"0029.ttl" },
        	{ pathextensionsuit+pathextensionhtml4+"0030.html",pathextensionsuit+pathextensionhtml4+"0030.ttl" },
        	{ pathextensionsuit+pathextensionhtml4+"0031.html",pathextensionsuit+pathextensionhtml4+"0031.ttl" },
        	{ pathextensionsuit+pathextensionhtml4+"0032.html",pathextensionsuit+pathextensionhtml4+"0032.ttl" },
        	{ pathextensionsuit+pathextensionhtml4+"0033.html",pathextensionsuit+pathextensionhtml4+"0033.ttl" },
        	{ pathextensionsuit+pathextensionhtml4+"0034.html",pathextensionsuit+pathextensionhtml4+"0034.ttl" },
        	{ pathextensionsuit+pathextensionhtml4+"0035.html",pathextensionsuit+pathextensionhtml4+"0035.ttl" },
        	{ pathextensionsuit+pathextensionhtml4+"0036.html",pathextensionsuit+pathextensionhtml4+"0036.ttl" },
        	{ pathextensionsuit+pathextensionhtml4+"0037.html",pathextensionsuit+pathextensionhtml4+"0037.ttl" },
        	{ pathextensionsuit+pathextensionhtml4+"0038.html",pathextensionsuit+pathextensionhtml4+"0038.ttl" },
        	{ pathextensionsuit+pathextensionhtml4+"0039.html",pathextensionsuit+pathextensionhtml4+"0039.ttl" },
        	{ pathextensionsuit+pathextensionhtml4+"0041.html",pathextensionsuit+pathextensionhtml4+"0041.ttl" },
        	{ pathextensionsuit+pathextensionhtml4+"0048.html",pathextensionsuit+pathextensionhtml4+"0048.ttl" },
        	{ pathextensionsuit+pathextensionhtml4+"0049.html",pathextensionsuit+pathextensionhtml4+"0049.ttl" },
        	{ pathextensionsuit+pathextensionhtml4+"0050.html",pathextensionsuit+pathextensionhtml4+"0050.ttl" },
        	{ pathextensionsuit+pathextensionhtml4+"0051.html",pathextensionsuit+pathextensionhtml4+"0051.ttl" },
        	{ pathextensionsuit+pathextensionhtml4+"0052.html",pathextensionsuit+pathextensionhtml4+"0052.ttl" },
        	{ pathextensionsuit+pathextensionhtml4+"0053.html",pathextensionsuit+pathextensionhtml4+"0053.ttl" },
        	{ pathextensionsuit+pathextensionhtml4+"0054.html",pathextensionsuit+pathextensionhtml4+"0054.ttl" },
        	{ pathextensionsuit+pathextensionhtml4+"0055.html",pathextensionsuit+pathextensionhtml4+"0055.ttl" },
        	{ pathextensionsuit+pathextensionhtml4+"0056.html",pathextensionsuit+pathextensionhtml4+"0056.ttl" },
        	{ pathextensionsuit+pathextensionhtml4+"0057.html",pathextensionsuit+pathextensionhtml4+"0057.ttl" },
        	{ pathextensionsuit+pathextensionhtml4+"0059.html",pathextensionsuit+pathextensionhtml4+"0059.ttl" },
        	{ pathextensionsuit+pathextensionhtml4+"0060.html",pathextensionsuit+pathextensionhtml4+"0060.ttl" },
        	{ pathextensionsuit+pathextensionhtml4+"0063.html",pathextensionsuit+pathextensionhtml4+"0063.ttl" },
        	{ pathextensionsuit+pathextensionhtml4+"0064.html",pathextensionsuit+pathextensionhtml4+"0064.ttl" },
        	{ pathextensionsuit+pathextensionhtml4+"0065.html",pathextensionsuit+pathextensionhtml4+"0065.ttl" },
        	{ pathextensionsuit+pathextensionhtml4+"0066.html",pathextensionsuit+pathextensionhtml4+"0066.ttl" },
        	{ pathextensionsuit+pathextensionhtml4+"0067.html",pathextensionsuit+pathextensionhtml4+"0067.ttl" },
        	{ pathextensionsuit+pathextensionhtml4+"0068.html",pathextensionsuit+pathextensionhtml4+"0068.ttl" },
        	{ pathextensionsuit+pathextensionhtml4+"0069.html",pathextensionsuit+pathextensionhtml4+"0069.ttl" },
        	{ pathextensionsuit+pathextensionhtml4+"0070.html",pathextensionsuit+pathextensionhtml4+"0070.ttl" },
        	{ pathextensionsuit+pathextensionhtml4+"0071.html",pathextensionsuit+pathextensionhtml4+"0071.ttl" },
        	{ pathextensionsuit+pathextensionhtml4+"0072.html",pathextensionsuit+pathextensionhtml4+"0072.ttl" },
        	{ pathextensionsuit+pathextensionhtml4+"0073.html",pathextensionsuit+pathextensionhtml4+"0073.ttl" },
        	{ pathextensionsuit+pathextensionhtml4+"0074.html",pathextensionsuit+pathextensionhtml4+"0074.ttl" },
        	{ pathextensionsuit+pathextensionhtml4+"0075.html",pathextensionsuit+pathextensionhtml4+"0075.ttl" },
        	{ pathextensionsuit+pathextensionhtml4+"0079.html",pathextensionsuit+pathextensionhtml4+"0079.ttl" },
        	{ pathextensionsuit+pathextensionhtml4+"0080.html",pathextensionsuit+pathextensionhtml4+"0080.ttl" },
        	{ pathextensionsuit+pathextensionhtml4+"0083.html",pathextensionsuit+pathextensionhtml4+"0083.ttl" },
        	{ pathextensionsuit+pathextensionhtml4+"0084.html",pathextensionsuit+pathextensionhtml4+"0084.ttl" },
        	{ pathextensionsuit+pathextensionhtml4+"0085.html",pathextensionsuit+pathextensionhtml4+"0085.ttl" },
        	{ pathextensionsuit+pathextensionhtml4+"0087.html",pathextensionsuit+pathextensionhtml4+"0087.ttl" },
        	{ pathextensionsuit+pathextensionhtml4+"0088.html",pathextensionsuit+pathextensionhtml4+"0088.ttl" },
        	{ pathextensionsuit+pathextensionhtml4+"0089.html",pathextensionsuit+pathextensionhtml4+"0089.ttl" },
        	{ pathextensionsuit+pathextensionhtml4+"0091.html",pathextensionsuit+pathextensionhtml4+"0091.ttl" },
        	{ pathextensionsuit+pathextensionhtml4+"0093.html",pathextensionsuit+pathextensionhtml4+"0093.ttl" },
        	{ pathextensionsuit+pathextensionhtml4+"0099.html",pathextensionsuit+pathextensionhtml4+"0099.ttl" },
        	{ pathextensionsuit+pathextensionhtml4+"0104.html",pathextensionsuit+pathextensionhtml4+"0104.ttl" },
        	{ pathextensionsuit+pathextensionhtml4+"0106.html",pathextensionsuit+pathextensionhtml4+"0106.ttl" },
        	{ pathextensionsuit+pathextensionhtml4+"0107.html",pathextensionsuit+pathextensionhtml4+"0107.ttl" },
        	{ pathextensionsuit+pathextensionhtml4+"0109.html",pathextensionsuit+pathextensionhtml4+"0109.ttl" },
        	{ pathextensionsuit+pathextensionhtml4+"0110.html",pathextensionsuit+pathextensionhtml4+"0110.ttl" },
        	{ pathextensionsuit+pathextensionhtml4+"0111.html",pathextensionsuit+pathextensionhtml4+"0111.ttl" },
        	{ pathextensionsuit+pathextensionhtml4+"0112.html",pathextensionsuit+pathextensionhtml4+"0112.ttl" },
        	{ pathextensionsuit+pathextensionhtml4+"0114.html",pathextensionsuit+pathextensionhtml4+"0114.ttl" },
        	{ pathextensionsuit+pathextensionhtml4+"0115.html",pathextensionsuit+pathextensionhtml4+"0115.ttl" },
        	{ pathextensionsuit+pathextensionhtml4+"0117.html",pathextensionsuit+pathextensionhtml4+"0117.ttl" },
        	{ pathextensionsuit+pathextensionhtml4+"0118.html",pathextensionsuit+pathextensionhtml4+"0118.ttl" },
        	{ pathextensionsuit+pathextensionhtml4+"0119.html",pathextensionsuit+pathextensionhtml4+"0119.ttl" },
        	{ pathextensionsuit+pathextensionhtml4+"0120.html",pathextensionsuit+pathextensionhtml4+"0120.ttl" },
        	{ pathextensionsuit+pathextensionhtml4+"0122.html",pathextensionsuit+pathextensionhtml4+"0122.ttl" },
        	{ pathextensionsuit+pathextensionhtml4+"0126.html",pathextensionsuit+pathextensionhtml4+"0126.ttl" },
        	{ pathextensionsuit+pathextensionhtml4+"0131.html",pathextensionsuit+pathextensionhtml4+"0131.ttl" },
        	{ pathextensionsuit+pathextensionhtml4+"0134.html",pathextensionsuit+pathextensionhtml4+"0134.ttl" },
        	{ pathextensionsuit+pathextensionhtml4+"0140.html",pathextensionsuit+pathextensionhtml4+"0140.ttl" },
        	{ pathextensionsuit+pathextensionhtml4+"0147.html",pathextensionsuit+pathextensionhtml4+"0147.ttl" },
        	{ pathextensionsuit+pathextensionhtml4+"0174.html",pathextensionsuit+pathextensionhtml4+"0174.ttl" },
        	{ pathextensionsuit+pathextensionhtml4+"0175.html",pathextensionsuit+pathextensionhtml4+"0175.ttl" },
        	{ pathextensionsuit+pathextensionhtml4+"0176.html",pathextensionsuit+pathextensionhtml4+"0176.ttl" },
        	{ pathextensionsuit+pathextensionhtml4+"0177.html",pathextensionsuit+pathextensionhtml4+"0177.ttl" },
        	{ pathextensionsuit+pathextensionhtml4+"0178.html",pathextensionsuit+pathextensionhtml4+"0178.ttl" },
        	{ pathextensionsuit+pathextensionhtml4+"0179.html",pathextensionsuit+pathextensionhtml4+"0179.ttl" },
        	{ pathextensionsuit+pathextensionhtml4+"0180.html",pathextensionsuit+pathextensionhtml4+"0180.ttl" },
        	{ pathextensionsuit+pathextensionhtml4+"0181.html",pathextensionsuit+pathextensionhtml4+"0181.ttl" },
        	{ pathextensionsuit+pathextensionhtml4+"0182.html",pathextensionsuit+pathextensionhtml4+"0182.ttl" },
        	{ pathextensionsuit+pathextensionhtml4+"0183.html",pathextensionsuit+pathextensionhtml4+"0183.ttl" },
        	{ pathextensionsuit+pathextensionhtml4+"0186.html",pathextensionsuit+pathextensionhtml4+"0186.ttl" },
        	{ pathextensionsuit+pathextensionhtml4+"0187.html",pathextensionsuit+pathextensionhtml4+"0187.ttl" },
        	{ pathextensionsuit+pathextensionhtml4+"0188.html",pathextensionsuit+pathextensionhtml4+"0188.ttl" },
        	{ pathextensionsuit+pathextensionhtml4+"0189.html",pathextensionsuit+pathextensionhtml4+"0189.ttl" },
        	{ pathextensionsuit+pathextensionhtml4+"0190.html",pathextensionsuit+pathextensionhtml4+"0190.ttl" },
        	{ pathextensionsuit+pathextensionhtml4+"0196.html",pathextensionsuit+pathextensionhtml4+"0196.ttl" },
        	{ pathextensionsuit+pathextensionhtml4+"0197.html",pathextensionsuit+pathextensionhtml4+"0197.ttl" },
        	{ pathextensionsuit+pathextensionhtml4+"0206.html",pathextensionsuit+pathextensionhtml4+"0206.ttl" },
        	{ pathextensionsuit+pathextensionhtml4+"0207.html",pathextensionsuit+pathextensionhtml4+"0207.ttl" },
        	{ pathextensionsuit+pathextensionhtml4+"0213.html",pathextensionsuit+pathextensionhtml4+"0213.ttl" },
        	{ pathextensionsuit+pathextensionhtml4+"0214.html",pathextensionsuit+pathextensionhtml4+"0214.ttl" },
        	{ pathextensionsuit+pathextensionhtml4+"0216.html",pathextensionsuit+pathextensionhtml4+"0216.ttl" },
        	{ pathextensionsuit+pathextensionhtml4+"0217.html",pathextensionsuit+pathextensionhtml4+"0217.ttl" },
        	{ pathextensionsuit+pathextensionhtml4+"0218.html",pathextensionsuit+pathextensionhtml4+"0218.ttl" },
        	{ pathextensionsuit+pathextensionhtml4+"0219.html",pathextensionsuit+pathextensionhtml4+"0219.ttl" },
        	{ pathextensionsuit+pathextensionhtml4+"0220.html",pathextensionsuit+pathextensionhtml4+"0220.ttl" },
        	{ pathextensionsuit+pathextensionhtml4+"0221.html",pathextensionsuit+pathextensionhtml4+"0221.ttl" },
        	{ pathextensionsuit+pathextensionhtml4+"0222.html",pathextensionsuit+pathextensionhtml4+"0222.ttl" },
        	{ pathextensionsuit+pathextensionhtml4+"0223.html",pathextensionsuit+pathextensionhtml4+"0223.ttl" },
        	{ pathextensionsuit+pathextensionhtml4+"0224.html",pathextensionsuit+pathextensionhtml4+"0224.ttl" },
        	{ pathextensionsuit+pathextensionhtml4+"0225.html",pathextensionsuit+pathextensionhtml4+"0225.ttl" },
        	{ pathextensionsuit+pathextensionhtml4+"0226.html",pathextensionsuit+pathextensionhtml4+"0226.ttl" },
        	{ pathextensionsuit+pathextensionhtml4+"0227.html",pathextensionsuit+pathextensionhtml4+"0227.ttl" },
        	{ pathextensionsuit+pathextensionhtml4+"0228.html",pathextensionsuit+pathextensionhtml4+"0228.ttl" },
        	{ pathextensionsuit+pathextensionhtml4+"0229.html",pathextensionsuit+pathextensionhtml4+"0229.ttl" },
        	{ pathextensionsuit+pathextensionhtml4+"0230.html",pathextensionsuit+pathextensionhtml4+"0230.ttl" },
        	{ pathextensionsuit+pathextensionhtml4+"0231.html",pathextensionsuit+pathextensionhtml4+"0231.ttl" },
        	{ pathextensionsuit+pathextensionhtml4+"0232.html",pathextensionsuit+pathextensionhtml4+"0232.ttl" },
        	{ pathextensionsuit+pathextensionhtml4+"0233.html",pathextensionsuit+pathextensionhtml4+"0233.ttl" },
        	{ pathextensionsuit+pathextensionhtml4+"0234.html",pathextensionsuit+pathextensionhtml4+"0234.ttl" },
        	{ pathextensionsuit+pathextensionhtml4+"0246.html",pathextensionsuit+pathextensionhtml4+"0246.ttl" },
        	{ pathextensionsuit+pathextensionhtml4+"0247.html",pathextensionsuit+pathextensionhtml4+"0247.ttl" },
        	{ pathextensionsuit+pathextensionhtml4+"0248.html",pathextensionsuit+pathextensionhtml4+"0248.ttl" },
        	{ pathextensionsuit+pathextensionhtml4+"0249.html",pathextensionsuit+pathextensionhtml4+"0249.ttl" },
        	{ pathextensionsuit+pathextensionhtml4+"0250.html",pathextensionsuit+pathextensionhtml4+"0250.ttl" },
        	{ pathextensionsuit+pathextensionhtml4+"0251.html",pathextensionsuit+pathextensionhtml4+"0251.ttl" },
        	{ pathextensionsuit+pathextensionhtml4+"0252.html",pathextensionsuit+pathextensionhtml4+"0252.ttl" },
        	{ pathextensionsuit+pathextensionhtml4+"0253.html",pathextensionsuit+pathextensionhtml4+"0253.ttl" },
        	{ pathextensionsuit+pathextensionhtml4+"0254.html",pathextensionsuit+pathextensionhtml4+"0254.ttl" },
        	{ pathextensionsuit+pathextensionhtml4+"0255.html",pathextensionsuit+pathextensionhtml4+"0255.ttl" },
        	{ pathextensionsuit+pathextensionhtml4+"0257.html",pathextensionsuit+pathextensionhtml4+"0257.ttl" },
        	{ pathextensionsuit+pathextensionhtml4+"0258.html",pathextensionsuit+pathextensionhtml4+"0258.ttl" },
        	{ pathextensionsuit+pathextensionhtml4+"0259.html",pathextensionsuit+pathextensionhtml4+"0259.ttl" },
        	{ pathextensionsuit+pathextensionhtml4+"0261.html",pathextensionsuit+pathextensionhtml4+"0261.ttl" },
        	{ pathextensionsuit+pathextensionhtml4+"0262.html",pathextensionsuit+pathextensionhtml4+"0262.ttl" },
        	{ pathextensionsuit+pathextensionhtml4+"0263.html",pathextensionsuit+pathextensionhtml4+"0263.ttl" },
        	{ pathextensionsuit+pathextensionhtml4+"0264.html",pathextensionsuit+pathextensionhtml4+"0264.ttl" },
        	{ pathextensionsuit+pathextensionhtml4+"0265.html",pathextensionsuit+pathextensionhtml4+"0265.ttl" },
        	{ pathextensionsuit+pathextensionhtml4+"0266.html",pathextensionsuit+pathextensionhtml4+"0266.ttl" },
        	{ pathextensionsuit+pathextensionhtml4+"0267.html",pathextensionsuit+pathextensionhtml4+"0267.ttl" },
        	{ pathextensionsuit+pathextensionhtml4+"0268.html",pathextensionsuit+pathextensionhtml4+"0268.ttl" },
        	{ pathextensionsuit+pathextensionhtml4+"0269.html",pathextensionsuit+pathextensionhtml4+"0269.ttl" },
        	{ pathextensionsuit+pathextensionhtml4+"0271.html",pathextensionsuit+pathextensionhtml4+"0271.ttl" },
        	{ pathextensionsuit+pathextensionhtml4+"0289.html",pathextensionsuit+pathextensionhtml4+"0289.ttl" },
        	{ pathextensionsuit+pathextensionhtml4+"0291.html",pathextensionsuit+pathextensionhtml4+"0291.ttl" },
        	{ pathextensionsuit+pathextensionhtml4+"0292.html",pathextensionsuit+pathextensionhtml4+"0292.ttl" },
        	{ pathextensionsuit+pathextensionhtml4+"0293.html",pathextensionsuit+pathextensionhtml4+"0293.ttl" },
        	{ pathextensionsuit+pathextensionhtml4+"0295.html",pathextensionsuit+pathextensionhtml4+"0295.ttl" },
        	{ pathextensionsuit+pathextensionhtml4+"0296.html",pathextensionsuit+pathextensionhtml4+"0296.ttl" },
        	{ pathextensionsuit+pathextensionhtml4+"0297.html",pathextensionsuit+pathextensionhtml4+"0297.ttl" },
        	{ pathextensionsuit+pathextensionhtml4+"0298.html",pathextensionsuit+pathextensionhtml4+"0298.ttl" },
        	{ pathextensionsuit+pathextensionhtml4+"0299.html",pathextensionsuit+pathextensionhtml4+"0299.ttl" },
        	{ pathextensionsuit+pathextensionhtml4+"0300.html",pathextensionsuit+pathextensionhtml4+"0300.ttl" },
        	{ pathextensionsuit+pathextensionhtml4+"0301.html",pathextensionsuit+pathextensionhtml4+"0301.ttl" },
        	{ pathextensionsuit+pathextensionhtml4+"0302.html",pathextensionsuit+pathextensionhtml4+"0302.ttl" },
        	{ pathextensionsuit+pathextensionhtml4+"0303.html",pathextensionsuit+pathextensionhtml4+"0303.ttl" },
        	{ pathextensionsuit+pathextensionhtml4+"0311.html",pathextensionsuit+pathextensionhtml4+"0311.ttl" },
        	{ pathextensionsuit+pathextensionhtml4+"0318.html",pathextensionsuit+pathextensionhtml4+"0318.ttl" },
        	{ pathextensionsuit+pathextensionhtml4+"0319.html",pathextensionsuit+pathextensionhtml4+"0319.ttl" },
        	{ pathextensionsuit+pathextensionhtml4+"0320.html",pathextensionsuit+pathextensionhtml4+"0320.ttl" },
        	{ pathextensionsuit+pathextensionhtml4+"0329.html",pathextensionsuit+pathextensionhtml4+"0329.ttl" },
        	{ pathextensionsuit+pathextensionhtml4+"0330.html",pathextensionsuit+pathextensionhtml4+"0330.ttl" },
        	{ pathextensionsuit+pathextensionhtml4+"0331.html",pathextensionsuit+pathextensionhtml4+"0331.ttl" },
        	{ pathextensionsuit+pathextensionhtml4+"0332.html",pathextensionsuit+pathextensionhtml4+"0332.ttl" },        	
        	{ pathextensionsuit+pathextensionhtml5+"0001.html",pathextensionsuit+pathextensionhtml5+"0001.ttl" },	//HMTL5
        	{ pathextensionsuit+pathextensionhtml5+"0006.html",pathextensionsuit+pathextensionhtml5+"0006.ttl" },
        	{ pathextensionsuit+pathextensionhtml5+"0007.html",pathextensionsuit+pathextensionhtml5+"0007.ttl" },
        	{ pathextensionsuit+pathextensionhtml5+"0008.html",pathextensionsuit+pathextensionhtml5+"0008.ttl" },
        	{ pathextensionsuit+pathextensionhtml5+"0009.html",pathextensionsuit+pathextensionhtml5+"0009.ttl" },
        	{ pathextensionsuit+pathextensionhtml5+"0010.html",pathextensionsuit+pathextensionhtml5+"0010.ttl" },
        	{ pathextensionsuit+pathextensionhtml5+"0014.html",pathextensionsuit+pathextensionhtml5+"0014.ttl" },
        	{ pathextensionsuit+pathextensionhtml5+"0015.html",pathextensionsuit+pathextensionhtml5+"0015.ttl" },
        	{ pathextensionsuit+pathextensionhtml5+"0017.html",pathextensionsuit+pathextensionhtml5+"0017.ttl" },
        	{ pathextensionsuit+pathextensionhtml5+"0018.html",pathextensionsuit+pathextensionhtml5+"0018.ttl" },
        	{ pathextensionsuit+pathextensionhtml5+"0020.html",pathextensionsuit+pathextensionhtml5+"0020.ttl" },
        	{ pathextensionsuit+pathextensionhtml5+"0021.html",pathextensionsuit+pathextensionhtml5+"0021.ttl" },
        	{ pathextensionsuit+pathextensionhtml5+"0023.html",pathextensionsuit+pathextensionhtml5+"0023.ttl" },
        	{ pathextensionsuit+pathextensionhtml5+"0025.html",pathextensionsuit+pathextensionhtml5+"0025.ttl" },
        	{ pathextensionsuit+pathextensionhtml5+"0026.html",pathextensionsuit+pathextensionhtml5+"0026.ttl" },
        	{ pathextensionsuit+pathextensionhtml5+"0027.html",pathextensionsuit+pathextensionhtml5+"0027.ttl" },
        	{ pathextensionsuit+pathextensionhtml5+"0029.html",pathextensionsuit+pathextensionhtml5+"0029.ttl" },
        	{ pathextensionsuit+pathextensionhtml5+"0030.html",pathextensionsuit+pathextensionhtml5+"0030.ttl" },
        	{ pathextensionsuit+pathextensionhtml5+"0031.html",pathextensionsuit+pathextensionhtml5+"0031.ttl" },
        	{ pathextensionsuit+pathextensionhtml5+"0032.html",pathextensionsuit+pathextensionhtml5+"0032.ttl" },
        	{ pathextensionsuit+pathextensionhtml5+"0033.html",pathextensionsuit+pathextensionhtml5+"0033.ttl" },
        	{ pathextensionsuit+pathextensionhtml5+"0034.html",pathextensionsuit+pathextensionhtml5+"0034.ttl" },
        	{ pathextensionsuit+pathextensionhtml5+"0036.html",pathextensionsuit+pathextensionhtml5+"0036.ttl" },
        	{ pathextensionsuit+pathextensionhtml5+"0038.html",pathextensionsuit+pathextensionhtml5+"0038.ttl" },
        	{ pathextensionsuit+pathextensionhtml5+"0048.html",pathextensionsuit+pathextensionhtml5+"0048.ttl" },
        	{ pathextensionsuit+pathextensionhtml5+"0049.html",pathextensionsuit+pathextensionhtml5+"0049.ttl" },
        	{ pathextensionsuit+pathextensionhtml5+"0050.html",pathextensionsuit+pathextensionhtml5+"0050.ttl" },
        	{ pathextensionsuit+pathextensionhtml5+"0051.html",pathextensionsuit+pathextensionhtml5+"0051.ttl" },
        	{ pathextensionsuit+pathextensionhtml5+"0052.html",pathextensionsuit+pathextensionhtml5+"0052.ttl" },
        	{ pathextensionsuit+pathextensionhtml5+"0053.html",pathextensionsuit+pathextensionhtml5+"0053.ttl" },
        	{ pathextensionsuit+pathextensionhtml5+"0054.html",pathextensionsuit+pathextensionhtml5+"0054.ttl" },
        	{ pathextensionsuit+pathextensionhtml5+"0055.html",pathextensionsuit+pathextensionhtml5+"0055.ttl" },
        	{ pathextensionsuit+pathextensionhtml5+"0056.html",pathextensionsuit+pathextensionhtml5+"0056.ttl" },
        	{ pathextensionsuit+pathextensionhtml5+"0057.html",pathextensionsuit+pathextensionhtml5+"0057.ttl" },
        	{ pathextensionsuit+pathextensionhtml5+"0059.html",pathextensionsuit+pathextensionhtml5+"0059.ttl" },
        	{ pathextensionsuit+pathextensionhtml5+"0060.html",pathextensionsuit+pathextensionhtml5+"0060.ttl" },
        	{ pathextensionsuit+pathextensionhtml5+"0063.html",pathextensionsuit+pathextensionhtml5+"0063.ttl" },
        	{ pathextensionsuit+pathextensionhtml5+"0064.html",pathextensionsuit+pathextensionhtml5+"0064.ttl" },
        	{ pathextensionsuit+pathextensionhtml5+"0065.html",pathextensionsuit+pathextensionhtml5+"0065.ttl" },
        	{ pathextensionsuit+pathextensionhtml5+"0066.html",pathextensionsuit+pathextensionhtml5+"0066.ttl" },
        	{ pathextensionsuit+pathextensionhtml5+"0067.html",pathextensionsuit+pathextensionhtml5+"0067.ttl" },
        	{ pathextensionsuit+pathextensionhtml5+"0068.html",pathextensionsuit+pathextensionhtml5+"0068.ttl" },
        	{ pathextensionsuit+pathextensionhtml5+"0069.html",pathextensionsuit+pathextensionhtml5+"0069.ttl" },
        	{ pathextensionsuit+pathextensionhtml5+"0070.html",pathextensionsuit+pathextensionhtml5+"0070.ttl" },
        	{ pathextensionsuit+pathextensionhtml5+"0071.html",pathextensionsuit+pathextensionhtml5+"0071.ttl" },
        	{ pathextensionsuit+pathextensionhtml5+"0072.html",pathextensionsuit+pathextensionhtml5+"0072.ttl" },
        	{ pathextensionsuit+pathextensionhtml5+"0073.html",pathextensionsuit+pathextensionhtml5+"0073.ttl" },
        	{ pathextensionsuit+pathextensionhtml5+"0074.html",pathextensionsuit+pathextensionhtml5+"0074.ttl" },
        	{ pathextensionsuit+pathextensionhtml5+"0075.html",pathextensionsuit+pathextensionhtml5+"0075.ttl" },
        	{ pathextensionsuit+pathextensionhtml5+"0080.html",pathextensionsuit+pathextensionhtml5+"0080.ttl" },
        	{ pathextensionsuit+pathextensionhtml5+"0083.html",pathextensionsuit+pathextensionhtml5+"0083.ttl" },
        	{ pathextensionsuit+pathextensionhtml5+"0084.html",pathextensionsuit+pathextensionhtml5+"0084.ttl" },
        	{ pathextensionsuit+pathextensionhtml5+"0088.html",pathextensionsuit+pathextensionhtml5+"0088.ttl" },
        	{ pathextensionsuit+pathextensionhtml5+"0089.html",pathextensionsuit+pathextensionhtml5+"0089.ttl" },
        	{ pathextensionsuit+pathextensionhtml5+"0091.html",pathextensionsuit+pathextensionhtml5+"0091.ttl" },
        	{ pathextensionsuit+pathextensionhtml5+"0093.html",pathextensionsuit+pathextensionhtml5+"0093.ttl" },
        	{ pathextensionsuit+pathextensionhtml5+"0099.html",pathextensionsuit+pathextensionhtml5+"0099.ttl" },
        	{ pathextensionsuit+pathextensionhtml5+"0104.html",pathextensionsuit+pathextensionhtml5+"0104.ttl" },
        	{ pathextensionsuit+pathextensionhtml5+"0106.html",pathextensionsuit+pathextensionhtml5+"0106.ttl" },
        	{ pathextensionsuit+pathextensionhtml5+"0107.html",pathextensionsuit+pathextensionhtml5+"0107.ttl" },
        	{ pathextensionsuit+pathextensionhtml5+"0110.html",pathextensionsuit+pathextensionhtml5+"0110.ttl" },
        	{ pathextensionsuit+pathextensionhtml5+"0111.html",pathextensionsuit+pathextensionhtml5+"0111.ttl" },
        	{ pathextensionsuit+pathextensionhtml5+"0112.html",pathextensionsuit+pathextensionhtml5+"0112.ttl" },
        	{ pathextensionsuit+pathextensionhtml5+"0115.html",pathextensionsuit+pathextensionhtml5+"0115.ttl" },
        	{ pathextensionsuit+pathextensionhtml5+"0117.html",pathextensionsuit+pathextensionhtml5+"0117.ttl" },
        	{ pathextensionsuit+pathextensionhtml5+"0118.html",pathextensionsuit+pathextensionhtml5+"0118.ttl" },
        	{ pathextensionsuit+pathextensionhtml5+"0119.html",pathextensionsuit+pathextensionhtml5+"0119.ttl" },
        	{ pathextensionsuit+pathextensionhtml5+"0120.html",pathextensionsuit+pathextensionhtml5+"0120.ttl" },
        	{ pathextensionsuit+pathextensionhtml5+"0122.html",pathextensionsuit+pathextensionhtml5+"0122.ttl" },
        	{ pathextensionsuit+pathextensionhtml5+"0126.html",pathextensionsuit+pathextensionhtml5+"0126.ttl" },
        	{ pathextensionsuit+pathextensionhtml5+"0134.html",pathextensionsuit+pathextensionhtml5+"0134.ttl" },
        	{ pathextensionsuit+pathextensionhtml5+"0140.html",pathextensionsuit+pathextensionhtml5+"0140.ttl" },
        	{ pathextensionsuit+pathextensionhtml5+"0174.html",pathextensionsuit+pathextensionhtml5+"0174.ttl" },
        	{ pathextensionsuit+pathextensionhtml5+"0175.html",pathextensionsuit+pathextensionhtml5+"0175.ttl" },
        	{ pathextensionsuit+pathextensionhtml5+"0176.html",pathextensionsuit+pathextensionhtml5+"0176.ttl" },
        	{ pathextensionsuit+pathextensionhtml5+"0177.html",pathextensionsuit+pathextensionhtml5+"0177.ttl" },
        	{ pathextensionsuit+pathextensionhtml5+"0178.html",pathextensionsuit+pathextensionhtml5+"0178.ttl" },
        	{ pathextensionsuit+pathextensionhtml5+"0181.html",pathextensionsuit+pathextensionhtml5+"0181.ttl" },
        	{ pathextensionsuit+pathextensionhtml5+"0182.html",pathextensionsuit+pathextensionhtml5+"0182.ttl" },
        	{ pathextensionsuit+pathextensionhtml5+"0186.html",pathextensionsuit+pathextensionhtml5+"0186.ttl" },
        	{ pathextensionsuit+pathextensionhtml5+"0187.html",pathextensionsuit+pathextensionhtml5+"0187.ttl" },
        	{ pathextensionsuit+pathextensionhtml5+"0188.html",pathextensionsuit+pathextensionhtml5+"0188.ttl" },
        	{ pathextensionsuit+pathextensionhtml5+"0189.html",pathextensionsuit+pathextensionhtml5+"0189.ttl" },
        	{ pathextensionsuit+pathextensionhtml5+"0190.html",pathextensionsuit+pathextensionhtml5+"0190.ttl" },
        	{ pathextensionsuit+pathextensionhtml5+"0196.html",pathextensionsuit+pathextensionhtml5+"0196.ttl" },
        	{ pathextensionsuit+pathextensionhtml5+"0197.html",pathextensionsuit+pathextensionhtml5+"0197.ttl" },
        	{ pathextensionsuit+pathextensionhtml5+"0206.html",pathextensionsuit+pathextensionhtml5+"0206.ttl" },
        	{ pathextensionsuit+pathextensionhtml5+"0207.html",pathextensionsuit+pathextensionhtml5+"0207.ttl" },
        	{ pathextensionsuit+pathextensionhtml5+"0213.html",pathextensionsuit+pathextensionhtml5+"0213.ttl" },
        	{ pathextensionsuit+pathextensionhtml5+"0214.html",pathextensionsuit+pathextensionhtml5+"0214.ttl" },
        	{ pathextensionsuit+pathextensionhtml5+"0216.html",pathextensionsuit+pathextensionhtml5+"0216.ttl" },
        	{ pathextensionsuit+pathextensionhtml5+"0217.html",pathextensionsuit+pathextensionhtml5+"0217.ttl" },
        	{ pathextensionsuit+pathextensionhtml5+"0218.html",pathextensionsuit+pathextensionhtml5+"0218.ttl" },
        	{ pathextensionsuit+pathextensionhtml5+"0219.html",pathextensionsuit+pathextensionhtml5+"0219.ttl" },
        	{ pathextensionsuit+pathextensionhtml5+"0220.html",pathextensionsuit+pathextensionhtml5+"0220.ttl" },
        	{ pathextensionsuit+pathextensionhtml5+"0221.html",pathextensionsuit+pathextensionhtml5+"0221.ttl" },
        	{ pathextensionsuit+pathextensionhtml5+"0224.html",pathextensionsuit+pathextensionhtml5+"0224.ttl" },
        	{ pathextensionsuit+pathextensionhtml5+"0225.html",pathextensionsuit+pathextensionhtml5+"0225.ttl" },
        	{ pathextensionsuit+pathextensionhtml5+"0228.html",pathextensionsuit+pathextensionhtml5+"0228.ttl" },
        	{ pathextensionsuit+pathextensionhtml5+"0229.html",pathextensionsuit+pathextensionhtml5+"0229.ttl" },
        	{ pathextensionsuit+pathextensionhtml5+"0231.html",pathextensionsuit+pathextensionhtml5+"0231.ttl" },
        	{ pathextensionsuit+pathextensionhtml5+"0232.html",pathextensionsuit+pathextensionhtml5+"0232.ttl" },
        	{ pathextensionsuit+pathextensionhtml5+"0233.html",pathextensionsuit+pathextensionhtml5+"0233.ttl" },
        	{ pathextensionsuit+pathextensionhtml5+"0246.html",pathextensionsuit+pathextensionhtml5+"0246.ttl" },
        	{ pathextensionsuit+pathextensionhtml5+"0247.html",pathextensionsuit+pathextensionhtml5+"0247.ttl" },
        	{ pathextensionsuit+pathextensionhtml5+"0248.html",pathextensionsuit+pathextensionhtml5+"0248.ttl" },
        	{ pathextensionsuit+pathextensionhtml5+"0249.html",pathextensionsuit+pathextensionhtml5+"0249.ttl" },
        	{ pathextensionsuit+pathextensionhtml5+"0250.html",pathextensionsuit+pathextensionhtml5+"0250.ttl" },
        	{ pathextensionsuit+pathextensionhtml5+"0251.html",pathextensionsuit+pathextensionhtml5+"0251.ttl" },
        	{ pathextensionsuit+pathextensionhtml5+"0252.html",pathextensionsuit+pathextensionhtml5+"0252.ttl" },
        	{ pathextensionsuit+pathextensionhtml5+"0253.html",pathextensionsuit+pathextensionhtml5+"0253.ttl" },
        	{ pathextensionsuit+pathextensionhtml5+"0254.html",pathextensionsuit+pathextensionhtml5+"0254.ttl" },
        	{ pathextensionsuit+pathextensionhtml5+"0255.html",pathextensionsuit+pathextensionhtml5+"0255.ttl" },
        	{ pathextensionsuit+pathextensionhtml5+"0257.html",pathextensionsuit+pathextensionhtml5+"0257.ttl" },
        	{ pathextensionsuit+pathextensionhtml5+"0259.html",pathextensionsuit+pathextensionhtml5+"0259.ttl" },
        	{ pathextensionsuit+pathextensionhtml5+"0261.html",pathextensionsuit+pathextensionhtml5+"0261.ttl" },
        	{ pathextensionsuit+pathextensionhtml5+"0262.html",pathextensionsuit+pathextensionhtml5+"0262.ttl" },
        	{ pathextensionsuit+pathextensionhtml5+"0263.html",pathextensionsuit+pathextensionhtml5+"0263.ttl" },
        	{ pathextensionsuit+pathextensionhtml5+"0264.html",pathextensionsuit+pathextensionhtml5+"0264.ttl" },
        	{ pathextensionsuit+pathextensionhtml5+"0265.html",pathextensionsuit+pathextensionhtml5+"0265.ttl" },
        	{ pathextensionsuit+pathextensionhtml5+"0266.html",pathextensionsuit+pathextensionhtml5+"0266.ttl" },
        	{ pathextensionsuit+pathextensionhtml5+"0267.html",pathextensionsuit+pathextensionhtml5+"0267.ttl" },
        	{ pathextensionsuit+pathextensionhtml5+"0268.html",pathextensionsuit+pathextensionhtml5+"0268.ttl" },
        	{ pathextensionsuit+pathextensionhtml5+"0269.html",pathextensionsuit+pathextensionhtml5+"0269.ttl" },
        	{ pathextensionsuit+pathextensionhtml5+"0271.html",pathextensionsuit+pathextensionhtml5+"0271.ttl" },
        	{ pathextensionsuit+pathextensionhtml5+"0272.html",pathextensionsuit+pathextensionhtml5+"0272.ttl" },
        	{ pathextensionsuit+pathextensionhtml5+"0273.html",pathextensionsuit+pathextensionhtml5+"0273.ttl" },
        	{ pathextensionsuit+pathextensionhtml5+"0274.html",pathextensionsuit+pathextensionhtml5+"0274.ttl" },
        	{ pathextensionsuit+pathextensionhtml5+"0275.html",pathextensionsuit+pathextensionhtml5+"0275.ttl" },
        	{ pathextensionsuit+pathextensionhtml5+"0276.html",pathextensionsuit+pathextensionhtml5+"0276.ttl" },
        	{ pathextensionsuit+pathextensionhtml5+"0277.html",pathextensionsuit+pathextensionhtml5+"0277.ttl" },
        	{ pathextensionsuit+pathextensionhtml5+"0278.html",pathextensionsuit+pathextensionhtml5+"0278.ttl" },
        	{ pathextensionsuit+pathextensionhtml5+"0279.html",pathextensionsuit+pathextensionhtml5+"0279.ttl" },
        	{ pathextensionsuit+pathextensionhtml5+"0281.html",pathextensionsuit+pathextensionhtml5+"0281.ttl" },
        	{ pathextensionsuit+pathextensionhtml5+"0282.html",pathextensionsuit+pathextensionhtml5+"0282.ttl" },
        	{ pathextensionsuit+pathextensionhtml5+"0283.html",pathextensionsuit+pathextensionhtml5+"0283.ttl" },
        	{ pathextensionsuit+pathextensionhtml5+"0284.html",pathextensionsuit+pathextensionhtml5+"0284.ttl" },
        	{ pathextensionsuit+pathextensionhtml5+"0287.html",pathextensionsuit+pathextensionhtml5+"0287.ttl" },
        	{ pathextensionsuit+pathextensionhtml5+"0289.html",pathextensionsuit+pathextensionhtml5+"0289.ttl" },
        	{ pathextensionsuit+pathextensionhtml5+"0290.html",pathextensionsuit+pathextensionhtml5+"0290.ttl" },
        	{ pathextensionsuit+pathextensionhtml5+"0291.html",pathextensionsuit+pathextensionhtml5+"0291.ttl" },
        	{ pathextensionsuit+pathextensionhtml5+"0292.html",pathextensionsuit+pathextensionhtml5+"0292.ttl" },
        	{ pathextensionsuit+pathextensionhtml5+"0293.html",pathextensionsuit+pathextensionhtml5+"0293.ttl" },
        	{ pathextensionsuit+pathextensionhtml5+"0296.html",pathextensionsuit+pathextensionhtml5+"0296.ttl" },
        	{ pathextensionsuit+pathextensionhtml5+"0297.html",pathextensionsuit+pathextensionhtml5+"0297.ttl" },
        	{ pathextensionsuit+pathextensionhtml5+"0298.html",pathextensionsuit+pathextensionhtml5+"0298.ttl" },
        	{ pathextensionsuit+pathextensionhtml5+"0299.html",pathextensionsuit+pathextensionhtml5+"0299.ttl" },
        	{ pathextensionsuit+pathextensionhtml5+"0300.html",pathextensionsuit+pathextensionhtml5+"0300.ttl" },
        	{ pathextensionsuit+pathextensionhtml5+"0301.html",pathextensionsuit+pathextensionhtml5+"0301.ttl" },
        	{ pathextensionsuit+pathextensionhtml5+"0302.html",pathextensionsuit+pathextensionhtml5+"0302.ttl" },
        	{ pathextensionsuit+pathextensionhtml5+"0311.html",pathextensionsuit+pathextensionhtml5+"0311.ttl" },
        	{ pathextensionsuit+pathextensionhtml5+"0312.html",pathextensionsuit+pathextensionhtml5+"0312.ttl" },
        	{ pathextensionsuit+pathextensionhtml5+"0315.html",pathextensionsuit+pathextensionhtml5+"0315.ttl" },
        	{ pathextensionsuit+pathextensionhtml5+"0316.html",pathextensionsuit+pathextensionhtml5+"0316.ttl" },
        	{ pathextensionsuit+pathextensionhtml5+"0317.html",pathextensionsuit+pathextensionhtml5+"0317.ttl" },
        	{ pathextensionsuit+pathextensionhtml5+"0318.html",pathextensionsuit+pathextensionhtml5+"0318.ttl" },
        	{ pathextensionsuit+pathextensionhtml5+"0321.html",pathextensionsuit+pathextensionhtml5+"0321.ttl" },
        	{ pathextensionsuit+pathextensionhtml5+"0322.html",pathextensionsuit+pathextensionhtml5+"0322.ttl" },
        	{ pathextensionsuit+pathextensionhtml5+"0323.html",pathextensionsuit+pathextensionhtml5+"0323.ttl" },
        	{ pathextensionsuit+pathextensionhtml5+"0324.html",pathextensionsuit+pathextensionhtml5+"0324.ttl" },
        	{ pathextensionsuit+pathextensionhtml5+"0325.html",pathextensionsuit+pathextensionhtml5+"0325.ttl" },
        	{ pathextensionsuit+pathextensionhtml5+"0326.html",pathextensionsuit+pathextensionhtml5+"0326.ttl" },
        	{ pathextensionsuit+pathextensionhtml5+"0327.html",pathextensionsuit+pathextensionhtml5+"0327.ttl" },
        	{ pathextensionsuit+pathextensionhtml5+"0328.html",pathextensionsuit+pathextensionhtml5+"0328.ttl" },
        	{ pathextensionsuit+pathextensionhtml5+"0329.html",pathextensionsuit+pathextensionhtml5+"0329.ttl" },
        	{ pathextensionsuit+pathextensionhtml5+"0330.html",pathextensionsuit+pathextensionhtml5+"0330.ttl" },
        	{ pathextensionsuit+pathextensionhtml5+"0331.html",pathextensionsuit+pathextensionhtml5+"0331.ttl" },
        	{ pathextensionsuit+pathextensionhtml5+"0332.html",pathextensionsuit+pathextensionhtml5+"0332.ttl" },
        	{ pathextensionsuit+pathextensionhtml5+"0333.html",pathextensionsuit+pathextensionhtml5+"0333.ttl" },
        	{ pathextensionsuit+pathextensionhtml5+"0334.html",pathextensionsuit+pathextensionhtml5+"0334.ttl" },
        	{ pathextensionsuit+pathextensionhtml5invalid+"0019.html",pathextensionsuit+pathextensionhtml5invalid+"0019.ttl" }, //HTML5-INVALID
        	{ pathextensionsuit+pathextensionhtml5invalid+"0035.html",pathextensionsuit+pathextensionhtml5invalid+"0035.ttl" },
        	{ pathextensionsuit+pathextensionhtml5invalid+"0037.html",pathextensionsuit+pathextensionhtml5invalid+"0037.ttl" },
        	{ pathextensionsuit+pathextensionhtml5invalid+"0039.html",pathextensionsuit+pathextensionhtml5invalid+"0039.ttl" },
        	{ pathextensionsuit+pathextensionhtml5invalid+"0041.html",pathextensionsuit+pathextensionhtml5invalid+"0041.ttl" },
        	{ pathextensionsuit+pathextensionhtml5invalid+"0079.html",pathextensionsuit+pathextensionhtml5invalid+"0079.ttl" },
        	{ pathextensionsuit+pathextensionhtml5invalid+"0085.html",pathextensionsuit+pathextensionhtml5invalid+"0085.ttl" },
        	{ pathextensionsuit+pathextensionhtml5invalid+"0087.html",pathextensionsuit+pathextensionhtml5invalid+"0087.ttl" },
        	{ pathextensionsuit+pathextensionhtml5invalid+"0109.html",pathextensionsuit+pathextensionhtml5invalid+"0109.ttl" },
        	{ pathextensionsuit+pathextensionhtml5invalid+"0114.html",pathextensionsuit+pathextensionhtml5invalid+"0114.ttl" },
        	{ pathextensionsuit+pathextensionhtml5invalid+"0131.html",pathextensionsuit+pathextensionhtml5invalid+"0131.ttl" },
        	{ pathextensionsuit+pathextensionhtml5invalid+"0147.html",pathextensionsuit+pathextensionhtml5invalid+"0147.ttl" },
        	{ pathextensionsuit+pathextensionhtml5invalid+"0179.html",pathextensionsuit+pathextensionhtml5invalid+"0179.ttl" },
        	{ pathextensionsuit+pathextensionhtml5invalid+"0180.html",pathextensionsuit+pathextensionhtml5invalid+"0180.ttl" },
        	{ pathextensionsuit+pathextensionhtml5invalid+"0183.html",pathextensionsuit+pathextensionhtml5invalid+"0183.ttl" },
        	{ pathextensionsuit+pathextensionhtml5invalid+"0222.html",pathextensionsuit+pathextensionhtml5invalid+"0222.ttl" },
        	{ pathextensionsuit+pathextensionhtml5invalid+"0223.html",pathextensionsuit+pathextensionhtml5invalid+"0223.ttl" },
        	{ pathextensionsuit+pathextensionhtml5invalid+"0226.html",pathextensionsuit+pathextensionhtml5invalid+"0226.ttl" },
        	{ pathextensionsuit+pathextensionhtml5invalid+"0227.html",pathextensionsuit+pathextensionhtml5invalid+"0227.ttl" },
        	{ pathextensionsuit+pathextensionhtml5invalid+"0230.html",pathextensionsuit+pathextensionhtml5invalid+"0230.ttl" },
        	{ pathextensionsuit+pathextensionhtml5invalid+"0234.html",pathextensionsuit+pathextensionhtml5invalid+"0234.ttl" },
        	{ pathextensionsuit+pathextensionhtml5invalid+"0258.html",pathextensionsuit+pathextensionhtml5invalid+"0258.ttl" },
        	{ pathextensionsuit+pathextensionhtml5invalid+"0280.html",pathextensionsuit+pathextensionhtml5invalid+"0280.ttl" },
        	{ pathextensionsuit+pathextensionhtml5invalid+"0285.html",pathextensionsuit+pathextensionhtml5invalid+"0285.ttl" },
        	{ pathextensionsuit+pathextensionhtml5invalid+"0295.html",pathextensionsuit+pathextensionhtml5invalid+"0295.ttl" },
        	{ pathextensionsuit+pathextensionhtml5invalid+"0303.html",pathextensionsuit+pathextensionhtml5invalid+"0303.ttl" },
        	{ pathextensionsuit+pathextensionhtml5invalid+"0319.html",pathextensionsuit+pathextensionhtml5invalid+"0319.ttl" },
        	{ pathextensionsuit+pathextensionhtml5invalid+"0320.html",pathextensionsuit+pathextensionhtml5invalid+"0320.ttl" },       	
        	{ pathextensionsuit+pathextensionsvg+"0201.svg",pathextensionsuit+pathextensionsvg+"0201.ttl" },	//SVG
        	{ pathextensionsuit+pathextensionsvg+"0202.svg",pathextensionsuit+pathextensionsvg+"0202.ttl" },
        	{ pathextensionsuit+pathextensionsvg+"0203.svg",pathextensionsuit+pathextensionsvg+"0203.ttl" },
        	{ pathextensionsuit+pathextensionsvg+"0214.svg",pathextensionsuit+pathextensionsvg+"0214.ttl" },
        	{ pathextensionsuit+pathextensionsvg+"0218.svg",pathextensionsuit+pathextensionsvg+"0218.ttl" },
        	{ pathextensionsuit+pathextensionsvg+"0219.svg",pathextensionsuit+pathextensionsvg+"0219.ttl" },
        	{ pathextensionsuit+pathextensionsvg+"0220.svg",pathextensionsuit+pathextensionsvg+"0220.ttl" },
        	{ pathextensionsuit+pathextensionsvg+"0221.svg",pathextensionsuit+pathextensionsvg+"0221.ttl" },
        	{ pathextensionsuit+pathextensionsvg+"0222.svg",pathextensionsuit+pathextensionsvg+"0222.ttl" },
        	{ pathextensionsuit+pathextensionsvg+"0223.svg",pathextensionsuit+pathextensionsvg+"0223.ttl" },
        	{ pathextensionsuit+pathextensionsvg+"0224.svg",pathextensionsuit+pathextensionsvg+"0224.ttl" },
        	{ pathextensionsuit+pathextensionsvg+"0225.svg",pathextensionsuit+pathextensionsvg+"0225.ttl" },
        	{ pathextensionsuit+pathextensionsvg+"0226.svg",pathextensionsuit+pathextensionsvg+"0226.ttl" },
        	{ pathextensionsuit+pathextensionsvg+"0227.svg",pathextensionsuit+pathextensionsvg+"0227.ttl" },
        	{ pathextensionsuit+pathextensionsvg+"0234.svg",pathextensionsuit+pathextensionsvg+"0234.ttl" },
        	{ pathextensionsuit+pathextensionsvg+"0259.svg",pathextensionsuit+pathextensionsvg+"0259.ttl" },
        	{ pathextensionsuit+pathextensionsvg+"0289.svg",pathextensionsuit+pathextensionsvg+"0289.ttl" },
        	{ pathextensionsuit+pathextensionsvg+"0290.svg",pathextensionsuit+pathextensionsvg+"0290.ttl" },
        	{ pathextensionsuit+pathextensionsvg+"0291.svg",pathextensionsuit+pathextensionsvg+"0291.ttl" },
        	{ pathextensionsuit+pathextensionsvg+"0292.svg",pathextensionsuit+pathextensionsvg+"0292.ttl" },
        	{ pathextensionsuit+pathextensionsvg+"0293.svg",pathextensionsuit+pathextensionsvg+"0293.ttl" },
        	{ pathextensionsuit+pathextensionsvg+"0295.svg",pathextensionsuit+pathextensionsvg+"0295.ttl" },
        	{ pathextensionsuit+pathextensionsvg+"0296.svg",pathextensionsuit+pathextensionsvg+"0296.ttl" },
        	{ pathextensionsuit+pathextensionsvg+"0297.svg",pathextensionsuit+pathextensionsvg+"0297.ttl" },
        	{ pathextensionsuit+pathextensionsvg+"0298.svg",pathextensionsuit+pathextensionsvg+"0298.ttl" },
        	{ pathextensionsuit+pathextensionsvg+"0299.svg",pathextensionsuit+pathextensionsvg+"0299.ttl" },
        	{ pathextensionsuit+pathextensionsvg+"0300.svg",pathextensionsuit+pathextensionsvg+"0300.ttl" },
        	{ pathextensionsuit+pathextensionsvg+"0301.svg",pathextensionsuit+pathextensionsvg+"0301.ttl" },
        	{ pathextensionsuit+pathextensionsvg+"0302.svg",pathextensionsuit+pathextensionsvg+"0302.ttl" },
        	{ pathextensionsuit+pathextensionsvg+"0304.svg",pathextensionsuit+pathextensionsvg+"0304.ttl" },
        	{ pathextensionsuit+pathextensionsvg+"0311.svg",pathextensionsuit+pathextensionsvg+"0311.ttl" },    	
        	{ pathextensionsuit+pathextensionxhtml1+"0001.xhtml",pathextensionsuit+pathextensionxhtml1+"0001.ttl" },	//XHMTL1
        	{ pathextensionsuit+pathextensionxhtml1+"0006.xhtml",pathextensionsuit+pathextensionxhtml1+"0006.ttl" },
        	{ pathextensionsuit+pathextensionxhtml1+"0007.xhtml",pathextensionsuit+pathextensionxhtml1+"0007.ttl" },
        	{ pathextensionsuit+pathextensionxhtml1+"0008.xhtml",pathextensionsuit+pathextensionxhtml1+"0008.ttl" },
        	{ pathextensionsuit+pathextensionxhtml1+"0009.xhtml",pathextensionsuit+pathextensionxhtml1+"0009.ttl" },
        	{ pathextensionsuit+pathextensionxhtml1+"0010.xhtml",pathextensionsuit+pathextensionxhtml1+"0010.ttl" },
        	{ pathextensionsuit+pathextensionxhtml1+"0012.xhtml",pathextensionsuit+pathextensionxhtml1+"0012.ttl" },
        	{ pathextensionsuit+pathextensionxhtml1+"0013.xhtml",pathextensionsuit+pathextensionxhtml1+"0013.ttl" },
        	{ pathextensionsuit+pathextensionxhtml1+"0014.xhtml",pathextensionsuit+pathextensionxhtml1+"0014.ttl" },
        	{ pathextensionsuit+pathextensionxhtml1+"0015.xhtml",pathextensionsuit+pathextensionxhtml1+"0015.ttl" },
        	{ pathextensionsuit+pathextensionxhtml1+"0017.xhtml",pathextensionsuit+pathextensionxhtml1+"0017.ttl" },
        	{ pathextensionsuit+pathextensionxhtml1+"0018.xhtml",pathextensionsuit+pathextensionxhtml1+"0018.ttl" },
        	{ pathextensionsuit+pathextensionxhtml1+"0019.xhtml",pathextensionsuit+pathextensionxhtml1+"0019.ttl" },
        	{ pathextensionsuit+pathextensionxhtml1+"0020.xhtml",pathextensionsuit+pathextensionxhtml1+"0020.ttl" },
        	{ pathextensionsuit+pathextensionxhtml1+"0021.xhtml",pathextensionsuit+pathextensionxhtml1+"0021.ttl" },
        	{ pathextensionsuit+pathextensionxhtml1+"0023.xhtml",pathextensionsuit+pathextensionxhtml1+"0023.ttl" },
        	{ pathextensionsuit+pathextensionxhtml1+"0025.xhtml",pathextensionsuit+pathextensionxhtml1+"0025.ttl" },
        	{ pathextensionsuit+pathextensionxhtml1+"0026.xhtml",pathextensionsuit+pathextensionxhtml1+"0026.ttl" },
        	{ pathextensionsuit+pathextensionxhtml1+"0027.xhtml",pathextensionsuit+pathextensionxhtml1+"0027.ttl" },
        	{ pathextensionsuit+pathextensionxhtml1+"0029.xhtml",pathextensionsuit+pathextensionxhtml1+"0029.ttl" },
        	{ pathextensionsuit+pathextensionxhtml1+"0030.xhtml",pathextensionsuit+pathextensionxhtml1+"0030.ttl" },
        	{ pathextensionsuit+pathextensionxhtml1+"0031.xhtml",pathextensionsuit+pathextensionxhtml1+"0031.ttl" },
        	{ pathextensionsuit+pathextensionxhtml1+"0032.xhtml",pathextensionsuit+pathextensionxhtml1+"0032.ttl" },
        	{ pathextensionsuit+pathextensionxhtml1+"0033.xhtml",pathextensionsuit+pathextensionxhtml1+"0033.ttl" },
        	{ pathextensionsuit+pathextensionxhtml1+"0034.xhtml",pathextensionsuit+pathextensionxhtml1+"0034.ttl" },
        	{ pathextensionsuit+pathextensionxhtml1+"0035.xhtml",pathextensionsuit+pathextensionxhtml1+"0035.ttl" },
        	{ pathextensionsuit+pathextensionxhtml1+"0036.xhtml",pathextensionsuit+pathextensionxhtml1+"0036.ttl" },
        	{ pathextensionsuit+pathextensionxhtml1+"0037.xhtml",pathextensionsuit+pathextensionxhtml1+"0037.ttl" },
        	{ pathextensionsuit+pathextensionxhtml1+"0038.xhtml",pathextensionsuit+pathextensionxhtml1+"0038.ttl" },
        	{ pathextensionsuit+pathextensionxhtml1+"0039.xhtml",pathextensionsuit+pathextensionxhtml1+"0039.ttl" },
        	{ pathextensionsuit+pathextensionxhtml1+"0041.xhtml",pathextensionsuit+pathextensionxhtml1+"0041.ttl" },
        	{ pathextensionsuit+pathextensionxhtml1+"0048.xhtml",pathextensionsuit+pathextensionxhtml1+"0048.ttl" },
        	{ pathextensionsuit+pathextensionxhtml1+"0049.xhtml",pathextensionsuit+pathextensionxhtml1+"0049.ttl" },
        	{ pathextensionsuit+pathextensionxhtml1+"0050.xhtml",pathextensionsuit+pathextensionxhtml1+"0050.ttl" },
        	{ pathextensionsuit+pathextensionxhtml1+"0051.xhtml",pathextensionsuit+pathextensionxhtml1+"0051.ttl" },
        	{ pathextensionsuit+pathextensionxhtml1+"0052.xhtml",pathextensionsuit+pathextensionxhtml1+"0052.ttl" },
        	{ pathextensionsuit+pathextensionxhtml1+"0053.xhtml",pathextensionsuit+pathextensionxhtml1+"0053.ttl" },
        	{ pathextensionsuit+pathextensionxhtml1+"0054.xhtml",pathextensionsuit+pathextensionxhtml1+"0054.ttl" },
        	{ pathextensionsuit+pathextensionxhtml1+"0055.xhtml",pathextensionsuit+pathextensionxhtml1+"0055.ttl" },
        	{ pathextensionsuit+pathextensionxhtml1+"0056.xhtml",pathextensionsuit+pathextensionxhtml1+"0056.ttl" },
        	{ pathextensionsuit+pathextensionxhtml1+"0057.xhtml",pathextensionsuit+pathextensionxhtml1+"0057.ttl" },
        	{ pathextensionsuit+pathextensionxhtml1+"0059.xhtml",pathextensionsuit+pathextensionxhtml1+"0059.ttl" },
        	{ pathextensionsuit+pathextensionxhtml1+"0060.xhtml",pathextensionsuit+pathextensionxhtml1+"0060.ttl" },
        	{ pathextensionsuit+pathextensionxhtml1+"0061.xhtml",pathextensionsuit+pathextensionxhtml1+"0061.ttl" },
        	{ pathextensionsuit+pathextensionxhtml1+"0062.xhtml",pathextensionsuit+pathextensionxhtml1+"0062.ttl" },
        	{ pathextensionsuit+pathextensionxhtml1+"0063.xhtml",pathextensionsuit+pathextensionxhtml1+"0063.ttl" },
        	{ pathextensionsuit+pathextensionxhtml1+"0064.xhtml",pathextensionsuit+pathextensionxhtml1+"0064.ttl" },
        	{ pathextensionsuit+pathextensionxhtml1+"0065.xhtml",pathextensionsuit+pathextensionxhtml1+"0065.ttl" },
        	{ pathextensionsuit+pathextensionxhtml1+"0066.xhtml",pathextensionsuit+pathextensionxhtml1+"0066.ttl" },
        	{ pathextensionsuit+pathextensionxhtml1+"0067.xhtml",pathextensionsuit+pathextensionxhtml1+"0067.ttl" },
        	{ pathextensionsuit+pathextensionxhtml1+"0068.xhtml",pathextensionsuit+pathextensionxhtml1+"0068.ttl" },
        	{ pathextensionsuit+pathextensionxhtml1+"0069.xhtml",pathextensionsuit+pathextensionxhtml1+"0069.ttl" },
        	{ pathextensionsuit+pathextensionxhtml1+"0070.xhtml",pathextensionsuit+pathextensionxhtml1+"0070.ttl" },
        	{ pathextensionsuit+pathextensionxhtml1+"0071.xhtml",pathextensionsuit+pathextensionxhtml1+"0071.ttl" },
        	{ pathextensionsuit+pathextensionxhtml1+"0072.xhtml",pathextensionsuit+pathextensionxhtml1+"0072.ttl" },
        	{ pathextensionsuit+pathextensionxhtml1+"0073.xhtml",pathextensionsuit+pathextensionxhtml1+"0073.ttl" },
        	{ pathextensionsuit+pathextensionxhtml1+"0074.xhtml",pathextensionsuit+pathextensionxhtml1+"0074.ttl" },
        	{ pathextensionsuit+pathextensionxhtml1+"0075.xhtml",pathextensionsuit+pathextensionxhtml1+"0075.ttl" },
        	{ pathextensionsuit+pathextensionxhtml1+"0076.xhtml",pathextensionsuit+pathextensionxhtml1+"0076.ttl" },
        	{ pathextensionsuit+pathextensionxhtml1+"0077.xhtml",pathextensionsuit+pathextensionxhtml1+"0077.ttl" },
        	{ pathextensionsuit+pathextensionxhtml1+"0079.xhtml",pathextensionsuit+pathextensionxhtml1+"0079.ttl" },
        	{ pathextensionsuit+pathextensionxhtml1+"0080.xhtml",pathextensionsuit+pathextensionxhtml1+"0080.ttl" },
        	{ pathextensionsuit+pathextensionxhtml1+"0083.xhtml",pathextensionsuit+pathextensionxhtml1+"0083.ttl" },
        	{ pathextensionsuit+pathextensionxhtml1+"0084.xhtml",pathextensionsuit+pathextensionxhtml1+"0084.ttl" },
        	{ pathextensionsuit+pathextensionxhtml1+"0085.xhtml",pathextensionsuit+pathextensionxhtml1+"0085.ttl" },
        	{ pathextensionsuit+pathextensionxhtml1+"0087.xhtml",pathextensionsuit+pathextensionxhtml1+"0087.ttl" },
        	{ pathextensionsuit+pathextensionxhtml1+"0088.xhtml",pathextensionsuit+pathextensionxhtml1+"0088.ttl" },
        	{ pathextensionsuit+pathextensionxhtml1+"0089.xhtml",pathextensionsuit+pathextensionxhtml1+"0089.ttl" },
        	{ pathextensionsuit+pathextensionxhtml1+"0091.xhtml",pathextensionsuit+pathextensionxhtml1+"0091.ttl" },
        	{ pathextensionsuit+pathextensionxhtml1+"0093.xhtml",pathextensionsuit+pathextensionxhtml1+"0093.ttl" },
        	{ pathextensionsuit+pathextensionxhtml1+"0099.xhtml",pathextensionsuit+pathextensionxhtml1+"0099.ttl" },
        	{ pathextensionsuit+pathextensionxhtml1+"0104.xhtml",pathextensionsuit+pathextensionxhtml1+"0104.ttl" },
        	{ pathextensionsuit+pathextensionxhtml1+"0106.xhtml",pathextensionsuit+pathextensionxhtml1+"0106.ttl" },
        	{ pathextensionsuit+pathextensionxhtml1+"0107.xhtml",pathextensionsuit+pathextensionxhtml1+"0107.ttl" },
        	{ pathextensionsuit+pathextensionxhtml1+"0108.xhtml",pathextensionsuit+pathextensionxhtml1+"0108.ttl" },
        	{ pathextensionsuit+pathextensionxhtml1+"0110.xhtml",pathextensionsuit+pathextensionxhtml1+"0110.ttl" },
        	{ pathextensionsuit+pathextensionxhtml1+"0111.xhtml",pathextensionsuit+pathextensionxhtml1+"0111.ttl" },
        	{ pathextensionsuit+pathextensionxhtml1+"0112.xhtml",pathextensionsuit+pathextensionxhtml1+"0112.ttl" },
        	{ pathextensionsuit+pathextensionxhtml1+"0113.xhtml",pathextensionsuit+pathextensionxhtml1+"0113.ttl" },
        	{ pathextensionsuit+pathextensionxhtml1+"0114.xhtml",pathextensionsuit+pathextensionxhtml1+"0114.ttl" },
        	{ pathextensionsuit+pathextensionxhtml1+"0115.xhtml",pathextensionsuit+pathextensionxhtml1+"0115.ttl" },
        	{ pathextensionsuit+pathextensionxhtml1+"0117.xhtml",pathextensionsuit+pathextensionxhtml1+"0117.ttl" },
        	{ pathextensionsuit+pathextensionxhtml1+"0118.xhtml",pathextensionsuit+pathextensionxhtml1+"0118.ttl" },
        	{ pathextensionsuit+pathextensionxhtml1+"0119.xhtml",pathextensionsuit+pathextensionxhtml1+"0119.ttl" },
        	{ pathextensionsuit+pathextensionxhtml1+"0120.xhtml",pathextensionsuit+pathextensionxhtml1+"0120.ttl" },
        	{ pathextensionsuit+pathextensionxhtml1+"0121.xhtml",pathextensionsuit+pathextensionxhtml1+"0121.ttl" },
        	{ pathextensionsuit+pathextensionxhtml1+"0122.xhtml",pathextensionsuit+pathextensionxhtml1+"0122.ttl" },
        	{ pathextensionsuit+pathextensionxhtml1+"0126.xhtml",pathextensionsuit+pathextensionxhtml1+"0126.ttl" },
        	{ pathextensionsuit+pathextensionxhtml1+"0131.xhtml",pathextensionsuit+pathextensionxhtml1+"0131.ttl" },
        	{ pathextensionsuit+pathextensionxhtml1+"0134.xhtml",pathextensionsuit+pathextensionxhtml1+"0134.ttl" },
        	{ pathextensionsuit+pathextensionxhtml1+"0140.xhtml",pathextensionsuit+pathextensionxhtml1+"0140.ttl" },
        	{ pathextensionsuit+pathextensionxhtml1+"0147.xhtml",pathextensionsuit+pathextensionxhtml1+"0147.ttl" },
        	{ pathextensionsuit+pathextensionxhtml1+"0172.xhtml",pathextensionsuit+pathextensionxhtml1+"0172.ttl" },
        	{ pathextensionsuit+pathextensionxhtml1+"0174.xhtml",pathextensionsuit+pathextensionxhtml1+"0174.ttl" },
        	{ pathextensionsuit+pathextensionxhtml1+"0175.xhtml",pathextensionsuit+pathextensionxhtml1+"0175.ttl" },
        	{ pathextensionsuit+pathextensionxhtml1+"0176.xhtml",pathextensionsuit+pathextensionxhtml1+"0176.ttl" },
        	{ pathextensionsuit+pathextensionxhtml1+"0177.xhtml",pathextensionsuit+pathextensionxhtml1+"0177.ttl" },
        	{ pathextensionsuit+pathextensionxhtml1+"0178.xhtml",pathextensionsuit+pathextensionxhtml1+"0178.ttl" },
        	{ pathextensionsuit+pathextensionxhtml1+"0179.xhtml",pathextensionsuit+pathextensionxhtml1+"0179.ttl" },
        	{ pathextensionsuit+pathextensionxhtml1+"0180.xhtml",pathextensionsuit+pathextensionxhtml1+"0180.ttl" },
        	{ pathextensionsuit+pathextensionxhtml1+"0181.xhtml",pathextensionsuit+pathextensionxhtml1+"0181.ttl" },
        	{ pathextensionsuit+pathextensionxhtml1+"0182.xhtml",pathextensionsuit+pathextensionxhtml1+"0182.ttl" },
        	{ pathextensionsuit+pathextensionxhtml1+"0183.xhtml",pathextensionsuit+pathextensionxhtml1+"0183.ttl" },
        	{ pathextensionsuit+pathextensionxhtml1+"0186.xhtml",pathextensionsuit+pathextensionxhtml1+"0186.ttl" },
        	{ pathextensionsuit+pathextensionxhtml1+"0187.xhtml",pathextensionsuit+pathextensionxhtml1+"0187.ttl" },
        	{ pathextensionsuit+pathextensionxhtml1+"0188.xhtml",pathextensionsuit+pathextensionxhtml1+"0188.ttl" },
        	{ pathextensionsuit+pathextensionxhtml1+"0189.xhtml",pathextensionsuit+pathextensionxhtml1+"0189.ttl" },
        	{ pathextensionsuit+pathextensionxhtml1+"0190.xhtml",pathextensionsuit+pathextensionxhtml1+"0190.ttl" },
        	{ pathextensionsuit+pathextensionxhtml1+"0196.xhtml",pathextensionsuit+pathextensionxhtml1+"0196.ttl" },
        	{ pathextensionsuit+pathextensionxhtml1+"0197.xhtml",pathextensionsuit+pathextensionxhtml1+"0197.ttl" },
        	{ pathextensionsuit+pathextensionxhtml1+"0198.xhtml",pathextensionsuit+pathextensionxhtml1+"0198.ttl" },
        	{ pathextensionsuit+pathextensionxhtml1+"0206.xhtml",pathextensionsuit+pathextensionxhtml1+"0206.ttl" },
        	{ pathextensionsuit+pathextensionxhtml1+"0207.xhtml",pathextensionsuit+pathextensionxhtml1+"0207.ttl" },
        	{ pathextensionsuit+pathextensionxhtml1+"0213.xhtml",pathextensionsuit+pathextensionxhtml1+"0213.ttl" },
        	{ pathextensionsuit+pathextensionxhtml1+"0214.xhtml",pathextensionsuit+pathextensionxhtml1+"0214.ttl" },
        	{ pathextensionsuit+pathextensionxhtml1+"0216.xhtml",pathextensionsuit+pathextensionxhtml1+"0216.ttl" },
        	{ pathextensionsuit+pathextensionxhtml1+"0217.xhtml",pathextensionsuit+pathextensionxhtml1+"0217.ttl" },
        	{ pathextensionsuit+pathextensionxhtml1+"0218.xhtml",pathextensionsuit+pathextensionxhtml1+"0218.ttl" },
        	{ pathextensionsuit+pathextensionxhtml1+"0219.xhtml",pathextensionsuit+pathextensionxhtml1+"0219.ttl" },
        	{ pathextensionsuit+pathextensionxhtml1+"0220.xhtml",pathextensionsuit+pathextensionxhtml1+"0220.ttl" },
        	{ pathextensionsuit+pathextensionxhtml1+"0221.xhtml",pathextensionsuit+pathextensionxhtml1+"0221.ttl" },
        	{ pathextensionsuit+pathextensionxhtml1+"0222.xhtml",pathextensionsuit+pathextensionxhtml1+"0222.ttl" },
        	{ pathextensionsuit+pathextensionxhtml1+"0223.xhtml",pathextensionsuit+pathextensionxhtml1+"0223.ttl" },
        	{ pathextensionsuit+pathextensionxhtml1+"0224.xhtml",pathextensionsuit+pathextensionxhtml1+"0224.ttl" },
        	{ pathextensionsuit+pathextensionxhtml1+"0225.xhtml",pathextensionsuit+pathextensionxhtml1+"0225.ttl" },
        	{ pathextensionsuit+pathextensionxhtml1+"0226.xhtml",pathextensionsuit+pathextensionxhtml1+"0226.ttl" },
        	{ pathextensionsuit+pathextensionxhtml1+"0227.xhtml",pathextensionsuit+pathextensionxhtml1+"0227.ttl" },
        	{ pathextensionsuit+pathextensionxhtml1+"0228.xhtml",pathextensionsuit+pathextensionxhtml1+"0228.ttl" },
        	{ pathextensionsuit+pathextensionxhtml1+"0229.xhtml",pathextensionsuit+pathextensionxhtml1+"0229.ttl" },
        	{ pathextensionsuit+pathextensionxhtml1+"0230.xhtml",pathextensionsuit+pathextensionxhtml1+"0230.ttl" },
        	{ pathextensionsuit+pathextensionxhtml1+"0231.xhtml",pathextensionsuit+pathextensionxhtml1+"0231.ttl" },
        	{ pathextensionsuit+pathextensionxhtml1+"0232.xhtml",pathextensionsuit+pathextensionxhtml1+"0232.ttl" },
        	{ pathextensionsuit+pathextensionxhtml1+"0233.xhtml",pathextensionsuit+pathextensionxhtml1+"0233.ttl" },
        	{ pathextensionsuit+pathextensionxhtml1+"0234.xhtml",pathextensionsuit+pathextensionxhtml1+"0234.ttl" },
        	{ pathextensionsuit+pathextensionxhtml1+"0246.xhtml",pathextensionsuit+pathextensionxhtml1+"0246.ttl" },
        	{ pathextensionsuit+pathextensionxhtml1+"0247.xhtml",pathextensionsuit+pathextensionxhtml1+"0247.ttl" },
        	{ pathextensionsuit+pathextensionxhtml1+"0248.xhtml",pathextensionsuit+pathextensionxhtml1+"0248.ttl" },
        	{ pathextensionsuit+pathextensionxhtml1+"0249.xhtml",pathextensionsuit+pathextensionxhtml1+"0249.ttl" },
        	{ pathextensionsuit+pathextensionxhtml1+"0250.xhtml",pathextensionsuit+pathextensionxhtml1+"0250.ttl" },
        	{ pathextensionsuit+pathextensionxhtml1+"0251.xhtml",pathextensionsuit+pathextensionxhtml1+"0251.ttl" },
        	{ pathextensionsuit+pathextensionxhtml1+"0252.xhtml",pathextensionsuit+pathextensionxhtml1+"0252.ttl" },
        	{ pathextensionsuit+pathextensionxhtml1+"0253.xhtml",pathextensionsuit+pathextensionxhtml1+"0253.ttl" },
        	{ pathextensionsuit+pathextensionxhtml1+"0254.xhtml",pathextensionsuit+pathextensionxhtml1+"0254.ttl" },
        	{ pathextensionsuit+pathextensionxhtml1+"0255.xhtml",pathextensionsuit+pathextensionxhtml1+"0255.ttl" },
        	{ pathextensionsuit+pathextensionxhtml1+"0256.xhtml",pathextensionsuit+pathextensionxhtml1+"0256.ttl" },
        	{ pathextensionsuit+pathextensionxhtml1+"0257.xhtml",pathextensionsuit+pathextensionxhtml1+"0257.ttl" },
        	{ pathextensionsuit+pathextensionxhtml1+"0258.xhtml",pathextensionsuit+pathextensionxhtml1+"0258.ttl" },
        	{ pathextensionsuit+pathextensionxhtml1+"0259.xhtml",pathextensionsuit+pathextensionxhtml1+"0259.ttl" },
        	{ pathextensionsuit+pathextensionxhtml1+"0260.xhtml",pathextensionsuit+pathextensionxhtml1+"0260.ttl" },
        	{ pathextensionsuit+pathextensionxhtml1+"0261.xhtml",pathextensionsuit+pathextensionxhtml1+"0261.ttl" },
        	{ pathextensionsuit+pathextensionxhtml1+"0262.xhtml",pathextensionsuit+pathextensionxhtml1+"0262.ttl" },
        	{ pathextensionsuit+pathextensionxhtml1+"0263.xhtml",pathextensionsuit+pathextensionxhtml1+"0263.ttl" },
        	{ pathextensionsuit+pathextensionxhtml1+"0264.xhtml",pathextensionsuit+pathextensionxhtml1+"0264.ttl" },
        	{ pathextensionsuit+pathextensionxhtml1+"0265.xhtml",pathextensionsuit+pathextensionxhtml1+"0265.ttl" },
        	{ pathextensionsuit+pathextensionxhtml1+"0266.xhtml",pathextensionsuit+pathextensionxhtml1+"0266.ttl" },
        	{ pathextensionsuit+pathextensionxhtml1+"0267.xhtml",pathextensionsuit+pathextensionxhtml1+"0267.ttl" },
        	{ pathextensionsuit+pathextensionxhtml1+"0268.xhtml",pathextensionsuit+pathextensionxhtml1+"0268.ttl" },
        	{ pathextensionsuit+pathextensionxhtml1+"0269.xhtml",pathextensionsuit+pathextensionxhtml1+"0269.ttl" },
        	{ pathextensionsuit+pathextensionxhtml1+"0271.xhtml",pathextensionsuit+pathextensionxhtml1+"0271.ttl" },
        	{ pathextensionsuit+pathextensionxhtml1+"0289.xhtml",pathextensionsuit+pathextensionxhtml1+"0289.ttl" },
        	{ pathextensionsuit+pathextensionxhtml1+"0290.xhtml",pathextensionsuit+pathextensionxhtml1+"0290.ttl" },
        	{ pathextensionsuit+pathextensionxhtml1+"0291.xhtml",pathextensionsuit+pathextensionxhtml1+"0291.ttl" },
        	{ pathextensionsuit+pathextensionxhtml1+"0292.xhtml",pathextensionsuit+pathextensionxhtml1+"0292.ttl" },
        	{ pathextensionsuit+pathextensionxhtml1+"0293.xhtml",pathextensionsuit+pathextensionxhtml1+"0293.ttl" },
        	{ pathextensionsuit+pathextensionxhtml1+"0295.xhtml",pathextensionsuit+pathextensionxhtml1+"0295.ttl" },
        	{ pathextensionsuit+pathextensionxhtml1+"0296.xhtml",pathextensionsuit+pathextensionxhtml1+"0296.ttl" },
        	{ pathextensionsuit+pathextensionxhtml1+"0297.xhtml",pathextensionsuit+pathextensionxhtml1+"0297.ttl" },
        	{ pathextensionsuit+pathextensionxhtml1+"0298.xhtml",pathextensionsuit+pathextensionxhtml1+"0298.ttl" },
        	{ pathextensionsuit+pathextensionxhtml1+"0299.xhtml",pathextensionsuit+pathextensionxhtml1+"0299.ttl" },
        	{ pathextensionsuit+pathextensionxhtml1+"0300.xhtml",pathextensionsuit+pathextensionxhtml1+"0300.ttl" },
        	{ pathextensionsuit+pathextensionxhtml1+"0301.xhtml",pathextensionsuit+pathextensionxhtml1+"0301.ttl" },
        	{ pathextensionsuit+pathextensionxhtml1+"0302.xhtml",pathextensionsuit+pathextensionxhtml1+"0302.ttl" },
        	{ pathextensionsuit+pathextensionxhtml1+"0311.xhtml",pathextensionsuit+pathextensionxhtml1+"0311.ttl" },
        	{ pathextensionsuit+pathextensionxhtml1+"0318.xhtml",pathextensionsuit+pathextensionxhtml1+"0318.ttl" },
        	{ pathextensionsuit+pathextensionxhtml1+"0319.xhtml",pathextensionsuit+pathextensionxhtml1+"0319.ttl" },
        	{ pathextensionsuit+pathextensionxhtml1+"0320.xhtml",pathextensionsuit+pathextensionxhtml1+"0320.ttl" },
        	{ pathextensionsuit+pathextensionxhtml1+"0329.xhtml",pathextensionsuit+pathextensionxhtml1+"0329.ttl" },
        	{ pathextensionsuit+pathextensionxhtml1+"0330.xhtml",pathextensionsuit+pathextensionxhtml1+"0330.ttl" },
        	{ pathextensionsuit+pathextensionxhtml1+"0331.xhtml",pathextensionsuit+pathextensionxhtml1+"0331.ttl" },
        	{ pathextensionsuit+pathextensionxhtml1+"0332.xhtml",pathextensionsuit+pathextensionxhtml1+"0332.ttl" },
        	{ pathextensionsuit+pathextensionxhtml5+"0001.xhtml",pathextensionsuit+pathextensionxhtml5+"0001.ttl" },	//XHTML5
        	{ pathextensionsuit+pathextensionxhtml5+"0006.xhtml",pathextensionsuit+pathextensionxhtml5+"0006.ttl" },
        	{ pathextensionsuit+pathextensionxhtml5+"0007.xhtml",pathextensionsuit+pathextensionxhtml5+"0007.ttl" },
        	{ pathextensionsuit+pathextensionxhtml5+"0008.xhtml",pathextensionsuit+pathextensionxhtml5+"0008.ttl" },
        	{ pathextensionsuit+pathextensionxhtml5+"0009.xhtml",pathextensionsuit+pathextensionxhtml5+"0009.ttl" },
        	{ pathextensionsuit+pathextensionxhtml5+"0010.xhtml",pathextensionsuit+pathextensionxhtml5+"0010.ttl" },
        	{ pathextensionsuit+pathextensionxhtml5+"0014.xhtml",pathextensionsuit+pathextensionxhtml5+"0014.ttl" },
        	{ pathextensionsuit+pathextensionxhtml5+"0015.xhtml",pathextensionsuit+pathextensionxhtml5+"0015.ttl" },
        	{ pathextensionsuit+pathextensionxhtml5+"0017.xhtml",pathextensionsuit+pathextensionxhtml5+"0017.ttl" },
        	{ pathextensionsuit+pathextensionxhtml5+"0018.xhtml",pathextensionsuit+pathextensionxhtml5+"0018.ttl" },
        	{ pathextensionsuit+pathextensionxhtml5+"0020.xhtml",pathextensionsuit+pathextensionxhtml5+"0020.ttl" },
        	{ pathextensionsuit+pathextensionxhtml5+"0021.xhtml",pathextensionsuit+pathextensionxhtml5+"0021.ttl" },
        	{ pathextensionsuit+pathextensionxhtml5+"0023.xhtml",pathextensionsuit+pathextensionxhtml5+"0023.ttl" },
        	{ pathextensionsuit+pathextensionxhtml5+"0025.xhtml",pathextensionsuit+pathextensionxhtml5+"0025.ttl" },
        	{ pathextensionsuit+pathextensionxhtml5+"0026.xhtml",pathextensionsuit+pathextensionxhtml5+"0026.ttl" },
        	{ pathextensionsuit+pathextensionxhtml5+"0027.xhtml",pathextensionsuit+pathextensionxhtml5+"0027.ttl" },
        	{ pathextensionsuit+pathextensionxhtml5+"0029.xhtml",pathextensionsuit+pathextensionxhtml5+"0029.ttl" },
        	{ pathextensionsuit+pathextensionxhtml5+"0030.xhtml",pathextensionsuit+pathextensionxhtml5+"0030.ttl" },
        	{ pathextensionsuit+pathextensionxhtml5+"0031.xhtml",pathextensionsuit+pathextensionxhtml5+"0031.ttl" },
        	{ pathextensionsuit+pathextensionxhtml5+"0032.xhtml",pathextensionsuit+pathextensionxhtml5+"0032.ttl" },
        	{ pathextensionsuit+pathextensionxhtml5+"0033.xhtml",pathextensionsuit+pathextensionxhtml5+"0033.ttl" },
        	{ pathextensionsuit+pathextensionxhtml5+"0034.xhtml",pathextensionsuit+pathextensionxhtml5+"0034.ttl" },
        	{ pathextensionsuit+pathextensionxhtml5+"0036.xhtml",pathextensionsuit+pathextensionxhtml5+"0036.ttl" },
        	{ pathextensionsuit+pathextensionxhtml5+"0038.xhtml",pathextensionsuit+pathextensionxhtml5+"0038.ttl" },
        	{ pathextensionsuit+pathextensionxhtml5+"0048.xhtml",pathextensionsuit+pathextensionxhtml5+"0048.ttl" },
        	{ pathextensionsuit+pathextensionxhtml5+"0049.xhtml",pathextensionsuit+pathextensionxhtml5+"0049.ttl" },
        	{ pathextensionsuit+pathextensionxhtml5+"0050.xhtml",pathextensionsuit+pathextensionxhtml5+"0050.ttl" },
        	{ pathextensionsuit+pathextensionxhtml5+"0051.xhtml",pathextensionsuit+pathextensionxhtml5+"0051.ttl" },
        	{ pathextensionsuit+pathextensionxhtml5+"0052.xhtml",pathextensionsuit+pathextensionxhtml5+"0052.ttl" },
        	{ pathextensionsuit+pathextensionxhtml5+"0053.xhtml",pathextensionsuit+pathextensionxhtml5+"0053.ttl" },
        	{ pathextensionsuit+pathextensionxhtml5+"0054.xhtml",pathextensionsuit+pathextensionxhtml5+"0054.ttl" },
        	{ pathextensionsuit+pathextensionxhtml5+"0055.xhtml",pathextensionsuit+pathextensionxhtml5+"0055.ttl" },
        	{ pathextensionsuit+pathextensionxhtml5+"0056.xhtml",pathextensionsuit+pathextensionxhtml5+"0056.ttl" },
        	{ pathextensionsuit+pathextensionxhtml5+"0057.xhtml",pathextensionsuit+pathextensionxhtml5+"0057.ttl" },
        	{ pathextensionsuit+pathextensionxhtml5+"0059.xhtml",pathextensionsuit+pathextensionxhtml5+"0059.ttl" },
        	{ pathextensionsuit+pathextensionxhtml5+"0060.xhtml",pathextensionsuit+pathextensionxhtml5+"0060.ttl" },
        	{ pathextensionsuit+pathextensionxhtml5+"0063.xhtml",pathextensionsuit+pathextensionxhtml5+"0063.ttl" },
        	{ pathextensionsuit+pathextensionxhtml5+"0064.xhtml",pathextensionsuit+pathextensionxhtml5+"0064.ttl" },
        	{ pathextensionsuit+pathextensionxhtml5+"0065.xhtml",pathextensionsuit+pathextensionxhtml5+"0065.ttl" },
        	{ pathextensionsuit+pathextensionxhtml5+"0066.xhtml",pathextensionsuit+pathextensionxhtml5+"0066.ttl" },
        	{ pathextensionsuit+pathextensionxhtml5+"0067.xhtml",pathextensionsuit+pathextensionxhtml5+"0067.ttl" },
        	{ pathextensionsuit+pathextensionxhtml5+"0068.xhtml",pathextensionsuit+pathextensionxhtml5+"0068.ttl" },
        	{ pathextensionsuit+pathextensionxhtml5+"0069.xhtml",pathextensionsuit+pathextensionxhtml5+"0069.ttl" },
        	{ pathextensionsuit+pathextensionxhtml5+"0070.xhtml",pathextensionsuit+pathextensionxhtml5+"0070.ttl" },
        	{ pathextensionsuit+pathextensionxhtml5+"0071.xhtml",pathextensionsuit+pathextensionxhtml5+"0071.ttl" },
        	{ pathextensionsuit+pathextensionxhtml5+"0072.xhtml",pathextensionsuit+pathextensionxhtml5+"0072.ttl" },
        	{ pathextensionsuit+pathextensionxhtml5+"0073.xhtml",pathextensionsuit+pathextensionxhtml5+"0073.ttl" },
        	{ pathextensionsuit+pathextensionxhtml5+"0074.xhtml",pathextensionsuit+pathextensionxhtml5+"0074.ttl" },
        	{ pathextensionsuit+pathextensionxhtml5+"0075.xhtml",pathextensionsuit+pathextensionxhtml5+"0075.ttl" },
        	{ pathextensionsuit+pathextensionxhtml5+"0080.xhtml",pathextensionsuit+pathextensionxhtml5+"0080.ttl" },
        	{ pathextensionsuit+pathextensionxhtml5+"0083.xhtml",pathextensionsuit+pathextensionxhtml5+"0083.ttl" },
        	{ pathextensionsuit+pathextensionxhtml5+"0084.xhtml",pathextensionsuit+pathextensionxhtml5+"0084.ttl" },
        	{ pathextensionsuit+pathextensionxhtml5+"0088.xhtml",pathextensionsuit+pathextensionxhtml5+"0088.ttl" },
        	{ pathextensionsuit+pathextensionxhtml5+"0089.xhtml",pathextensionsuit+pathextensionxhtml5+"0089.ttl" },
        	{ pathextensionsuit+pathextensionxhtml5+"0091.xhtml",pathextensionsuit+pathextensionxhtml5+"0091.ttl" },
        	{ pathextensionsuit+pathextensionxhtml5+"0093.xhtml",pathextensionsuit+pathextensionxhtml5+"0093.ttl" },
        	{ pathextensionsuit+pathextensionxhtml5+"0099.xhtml",pathextensionsuit+pathextensionxhtml5+"0099.ttl" },
        	{ pathextensionsuit+pathextensionxhtml5+"0104.xhtml",pathextensionsuit+pathextensionxhtml5+"0104.ttl" },
        	{ pathextensionsuit+pathextensionxhtml5+"0106.xhtml",pathextensionsuit+pathextensionxhtml5+"0106.ttl" },
        	{ pathextensionsuit+pathextensionxhtml5+"0107.xhtml",pathextensionsuit+pathextensionxhtml5+"0107.ttl" },
        	{ pathextensionsuit+pathextensionxhtml5+"0110.xhtml",pathextensionsuit+pathextensionxhtml5+"0110.ttl" },
        	{ pathextensionsuit+pathextensionxhtml5+"0111.xhtml",pathextensionsuit+pathextensionxhtml5+"0111.ttl" },
        	{ pathextensionsuit+pathextensionxhtml5+"0112.xhtml",pathextensionsuit+pathextensionxhtml5+"0112.ttl" },
        	{ pathextensionsuit+pathextensionxhtml5+"0115.xhtml",pathextensionsuit+pathextensionxhtml5+"0115.ttl" },
        	{ pathextensionsuit+pathextensionxhtml5+"0117.xhtml",pathextensionsuit+pathextensionxhtml5+"0117.ttl" },
        	{ pathextensionsuit+pathextensionxhtml5+"0118.xhtml",pathextensionsuit+pathextensionxhtml5+"0118.ttl" },
        	{ pathextensionsuit+pathextensionxhtml5+"0119.xhtml",pathextensionsuit+pathextensionxhtml5+"0119.ttl" },
        	{ pathextensionsuit+pathextensionxhtml5+"0120.xhtml",pathextensionsuit+pathextensionxhtml5+"0120.ttl" },
        	{ pathextensionsuit+pathextensionxhtml5+"0121.xhtml",pathextensionsuit+pathextensionxhtml5+"0121.ttl" },
        	{ pathextensionsuit+pathextensionxhtml5+"0122.xhtml",pathextensionsuit+pathextensionxhtml5+"0122.ttl" },
        	{ pathextensionsuit+pathextensionxhtml5+"0126.xhtml",pathextensionsuit+pathextensionxhtml5+"0126.ttl" },
        	{ pathextensionsuit+pathextensionxhtml5+"0134.xhtml",pathextensionsuit+pathextensionxhtml5+"0134.ttl" },
        	{ pathextensionsuit+pathextensionxhtml5+"0140.xhtml",pathextensionsuit+pathextensionxhtml5+"0140.ttl" },
        	{ pathextensionsuit+pathextensionxhtml5+"0147.xhtml",pathextensionsuit+pathextensionxhtml5+"0147.ttl" },
        	{ pathextensionsuit+pathextensionxhtml5+"0174.xhtml",pathextensionsuit+pathextensionxhtml5+"0174.ttl" },
        	{ pathextensionsuit+pathextensionxhtml5+"0175.xhtml",pathextensionsuit+pathextensionxhtml5+"0175.ttl" },
        	{ pathextensionsuit+pathextensionxhtml5+"0176.xhtml",pathextensionsuit+pathextensionxhtml5+"0176.ttl" },
        	{ pathextensionsuit+pathextensionxhtml5+"0177.xhtml",pathextensionsuit+pathextensionxhtml5+"0177.ttl" },
        	{ pathextensionsuit+pathextensionxhtml5+"0178.xhtml",pathextensionsuit+pathextensionxhtml5+"0178.ttl" },
        	{ pathextensionsuit+pathextensionxhtml5+"0179.xhtml",pathextensionsuit+pathextensionxhtml5+"0179.ttl" },
        	{ pathextensionsuit+pathextensionxhtml5+"0181.xhtml",pathextensionsuit+pathextensionxhtml5+"0181.ttl" },
        	{ pathextensionsuit+pathextensionxhtml5+"0182.xhtml",pathextensionsuit+pathextensionxhtml5+"0182.ttl" },
        	{ pathextensionsuit+pathextensionxhtml5+"0183.xhtml",pathextensionsuit+pathextensionxhtml5+"0183.ttl" },
        	{ pathextensionsuit+pathextensionxhtml5+"0186.xhtml",pathextensionsuit+pathextensionxhtml5+"0186.ttl" },
        	{ pathextensionsuit+pathextensionxhtml5+"0187.xhtml",pathextensionsuit+pathextensionxhtml5+"0187.ttl" },
        	{ pathextensionsuit+pathextensionxhtml5+"0188.xhtml",pathextensionsuit+pathextensionxhtml5+"0188.ttl" },
        	{ pathextensionsuit+pathextensionxhtml5+"0189.xhtml",pathextensionsuit+pathextensionxhtml5+"0189.ttl" },
        	{ pathextensionsuit+pathextensionxhtml5+"0190.xhtml",pathextensionsuit+pathextensionxhtml5+"0190.ttl" },
        	{ pathextensionsuit+pathextensionxhtml5+"0196.xhtml",pathextensionsuit+pathextensionxhtml5+"0196.ttl" },
        	{ pathextensionsuit+pathextensionxhtml5+"0197.xhtml",pathextensionsuit+pathextensionxhtml5+"0197.ttl" },
        	{ pathextensionsuit+pathextensionxhtml5+"0198.xhtml",pathextensionsuit+pathextensionxhtml5+"0198.ttl" },
        	{ pathextensionsuit+pathextensionxhtml5+"0206.xhtml",pathextensionsuit+pathextensionxhtml5+"0206.ttl" },
        	{ pathextensionsuit+pathextensionxhtml5+"0207.xhtml",pathextensionsuit+pathextensionxhtml5+"0207.ttl" },
        	{ pathextensionsuit+pathextensionxhtml5+"0213.xhtml",pathextensionsuit+pathextensionxhtml5+"0213.ttl" },
        	{ pathextensionsuit+pathextensionxhtml5+"0214.xhtml",pathextensionsuit+pathextensionxhtml5+"0214.ttl" },
        	{ pathextensionsuit+pathextensionxhtml5+"0216.xhtml",pathextensionsuit+pathextensionxhtml5+"0216.ttl" },
        	{ pathextensionsuit+pathextensionxhtml5+"0217.xhtml",pathextensionsuit+pathextensionxhtml5+"0217.ttl" },
        	{ pathextensionsuit+pathextensionxhtml5+"0218.xhtml",pathextensionsuit+pathextensionxhtml5+"0218.ttl" },
        	{ pathextensionsuit+pathextensionxhtml5+"0219.xhtml",pathextensionsuit+pathextensionxhtml5+"0219.ttl" },
        	{ pathextensionsuit+pathextensionxhtml5+"0220.xhtml",pathextensionsuit+pathextensionxhtml5+"0220.ttl" },
        	{ pathextensionsuit+pathextensionxhtml5+"0221.xhtml",pathextensionsuit+pathextensionxhtml5+"0221.ttl" },
        	{ pathextensionsuit+pathextensionxhtml5+"0224.xhtml",pathextensionsuit+pathextensionxhtml5+"0224.ttl" },
        	{ pathextensionsuit+pathextensionxhtml5+"0225.xhtml",pathextensionsuit+pathextensionxhtml5+"0225.ttl" },
        	{ pathextensionsuit+pathextensionxhtml5+"0228.xhtml",pathextensionsuit+pathextensionxhtml5+"0228.ttl" },
        	{ pathextensionsuit+pathextensionxhtml5+"0229.xhtml",pathextensionsuit+pathextensionxhtml5+"0229.ttl" },
        	{ pathextensionsuit+pathextensionxhtml5+"0231.xhtml",pathextensionsuit+pathextensionxhtml5+"0231.ttl" },
        	{ pathextensionsuit+pathextensionxhtml5+"0232.xhtml",pathextensionsuit+pathextensionxhtml5+"0232.ttl" },
        	{ pathextensionsuit+pathextensionxhtml5+"0233.xhtml",pathextensionsuit+pathextensionxhtml5+"0233.ttl" },
        	{ pathextensionsuit+pathextensionxhtml5+"0246.xhtml",pathextensionsuit+pathextensionxhtml5+"0246.ttl" },
        	{ pathextensionsuit+pathextensionxhtml5+"0247.xhtml",pathextensionsuit+pathextensionxhtml5+"0247.ttl" },
        	{ pathextensionsuit+pathextensionxhtml5+"0248.xhtml",pathextensionsuit+pathextensionxhtml5+"0248.ttl" },
        	{ pathextensionsuit+pathextensionxhtml5+"0249.xhtml",pathextensionsuit+pathextensionxhtml5+"0249.ttl" },
        	{ pathextensionsuit+pathextensionxhtml5+"0250.xhtml",pathextensionsuit+pathextensionxhtml5+"0250.ttl" },
        	{ pathextensionsuit+pathextensionxhtml5+"0251.xhtml",pathextensionsuit+pathextensionxhtml5+"0251.ttl" },
        	{ pathextensionsuit+pathextensionxhtml5+"0252.xhtml",pathextensionsuit+pathextensionxhtml5+"0252.ttl" },
        	{ pathextensionsuit+pathextensionxhtml5+"0253.xhtml",pathextensionsuit+pathextensionxhtml5+"0253.ttl" },
        	{ pathextensionsuit+pathextensionxhtml5+"0254.xhtml",pathextensionsuit+pathextensionxhtml5+"0254.ttl" },
        	{ pathextensionsuit+pathextensionxhtml5+"0255.xhtml",pathextensionsuit+pathextensionxhtml5+"0255.ttl" },
        	{ pathextensionsuit+pathextensionxhtml5+"0256.xhtml",pathextensionsuit+pathextensionxhtml5+"0256.ttl" },
        	{ pathextensionsuit+pathextensionxhtml5+"0257.xhtml",pathextensionsuit+pathextensionxhtml5+"0257.ttl" },
        	{ pathextensionsuit+pathextensionxhtml5+"0259.xhtml",pathextensionsuit+pathextensionxhtml5+"0259.ttl" },
        	{ pathextensionsuit+pathextensionxhtml5+"0261.xhtml",pathextensionsuit+pathextensionxhtml5+"0261.ttl" },
        	{ pathextensionsuit+pathextensionxhtml5+"0262.xhtml",pathextensionsuit+pathextensionxhtml5+"0262.ttl" },
        	{ pathextensionsuit+pathextensionxhtml5+"0263.xhtml",pathextensionsuit+pathextensionxhtml5+"0263.ttl" },
        	{ pathextensionsuit+pathextensionxhtml5+"0264.xhtml",pathextensionsuit+pathextensionxhtml5+"0264.ttl" },
        	{ pathextensionsuit+pathextensionxhtml5+"0265.xhtml",pathextensionsuit+pathextensionxhtml5+"0265.ttl" },
        	{ pathextensionsuit+pathextensionxhtml5+"0266.xhtml",pathextensionsuit+pathextensionxhtml5+"0266.ttl" },
        	{ pathextensionsuit+pathextensionxhtml5+"0267.xhtml",pathextensionsuit+pathextensionxhtml5+"0267.ttl" },
        	{ pathextensionsuit+pathextensionxhtml5+"0268.xhtml",pathextensionsuit+pathextensionxhtml5+"0268.ttl" },
        	{ pathextensionsuit+pathextensionxhtml5+"0269.xhtml",pathextensionsuit+pathextensionxhtml5+"0269.ttl" },
        	{ pathextensionsuit+pathextensionxhtml5+"0271.xhtml",pathextensionsuit+pathextensionxhtml5+"0271.ttl" },
        	{ pathextensionsuit+pathextensionxhtml5+"0272.xhtml",pathextensionsuit+pathextensionxhtml5+"0272.ttl" },
        	{ pathextensionsuit+pathextensionxhtml5+"0273.xhtml",pathextensionsuit+pathextensionxhtml5+"0273.ttl" },
        	{ pathextensionsuit+pathextensionxhtml5+"0274.xhtml",pathextensionsuit+pathextensionxhtml5+"0274.ttl" },
        	{ pathextensionsuit+pathextensionxhtml5+"0275.xhtml",pathextensionsuit+pathextensionxhtml5+"0275.ttl" },
        	{ pathextensionsuit+pathextensionxhtml5+"0276.xhtml",pathextensionsuit+pathextensionxhtml5+"0276.ttl" },
        	{ pathextensionsuit+pathextensionxhtml5+"0277.xhtml",pathextensionsuit+pathextensionxhtml5+"0277.ttl" },
        	{ pathextensionsuit+pathextensionxhtml5+"0278.xhtml",pathextensionsuit+pathextensionxhtml5+"0278.ttl" },
        	{ pathextensionsuit+pathextensionxhtml5+"0279.xhtml",pathextensionsuit+pathextensionxhtml5+"0279.ttl" },
        	{ pathextensionsuit+pathextensionxhtml5+"0281.xhtml",pathextensionsuit+pathextensionxhtml5+"0281.ttl" },
        	{ pathextensionsuit+pathextensionxhtml5+"0282.xhtml",pathextensionsuit+pathextensionxhtml5+"0282.ttl" },
        	{ pathextensionsuit+pathextensionxhtml5+"0283.xhtml",pathextensionsuit+pathextensionxhtml5+"0283.ttl" },
        	{ pathextensionsuit+pathextensionxhtml5+"0284.xhtml",pathextensionsuit+pathextensionxhtml5+"0284.ttl" },
        	{ pathextensionsuit+pathextensionxhtml5+"0287.xhtml",pathextensionsuit+pathextensionxhtml5+"0287.ttl" },
        	{ pathextensionsuit+pathextensionxhtml5+"0289.xhtml",pathextensionsuit+pathextensionxhtml5+"0289.ttl" },
        	{ pathextensionsuit+pathextensionxhtml5+"0290.xhtml",pathextensionsuit+pathextensionxhtml5+"0290.ttl" },
        	{ pathextensionsuit+pathextensionxhtml5+"0291.xhtml",pathextensionsuit+pathextensionxhtml5+"0291.ttl" },
        	{ pathextensionsuit+pathextensionxhtml5+"0292.xhtml",pathextensionsuit+pathextensionxhtml5+"0292.ttl" },
        	{ pathextensionsuit+pathextensionxhtml5+"0293.xhtml",pathextensionsuit+pathextensionxhtml5+"0293.ttl" },
        	{ pathextensionsuit+pathextensionxhtml5+"0296.xhtml",pathextensionsuit+pathextensionxhtml5+"0296.ttl" },
        	{ pathextensionsuit+pathextensionxhtml5+"0297.xhtml",pathextensionsuit+pathextensionxhtml5+"0297.ttl" },
        	{ pathextensionsuit+pathextensionxhtml5+"0298.xhtml",pathextensionsuit+pathextensionxhtml5+"0298.ttl" },
        	{ pathextensionsuit+pathextensionxhtml5+"0299.xhtml",pathextensionsuit+pathextensionxhtml5+"0299.ttl" },
        	{ pathextensionsuit+pathextensionxhtml5+"0300.xhtml",pathextensionsuit+pathextensionxhtml5+"0300.ttl" },
        	{ pathextensionsuit+pathextensionxhtml5+"0301.xhtml",pathextensionsuit+pathextensionxhtml5+"0301.ttl" },
        	{ pathextensionsuit+pathextensionxhtml5+"0302.xhtml",pathextensionsuit+pathextensionxhtml5+"0302.ttl" },
        	{ pathextensionsuit+pathextensionxhtml5+"0311.xhtml",pathextensionsuit+pathextensionxhtml5+"0311.ttl" },
        	{ pathextensionsuit+pathextensionxhtml5+"0312.xhtml",pathextensionsuit+pathextensionxhtml5+"0312.ttl" },
        	{ pathextensionsuit+pathextensionxhtml5+"0315.xhtml",pathextensionsuit+pathextensionxhtml5+"0315.ttl" },
        	{ pathextensionsuit+pathextensionxhtml5+"0316.xhtml",pathextensionsuit+pathextensionxhtml5+"0316.ttl" },
        	{ pathextensionsuit+pathextensionxhtml5+"0317.xhtml",pathextensionsuit+pathextensionxhtml5+"0317.ttl" },
        	{ pathextensionsuit+pathextensionxhtml5+"0318.xhtml",pathextensionsuit+pathextensionxhtml5+"0318.ttl" },
        	{ pathextensionsuit+pathextensionxhtml5+"0319.xhtml",pathextensionsuit+pathextensionxhtml5+"0319.ttl" },
        	{ pathextensionsuit+pathextensionxhtml5+"0321.xhtml",pathextensionsuit+pathextensionxhtml5+"0321.ttl" },
        	{ pathextensionsuit+pathextensionxhtml5+"0322.xhtml",pathextensionsuit+pathextensionxhtml5+"0322.ttl" },
        	{ pathextensionsuit+pathextensionxhtml5+"0323.xhtml",pathextensionsuit+pathextensionxhtml5+"0323.ttl" },
        	{ pathextensionsuit+pathextensionxhtml5+"0324.xhtml",pathextensionsuit+pathextensionxhtml5+"0324.ttl" },
        	{ pathextensionsuit+pathextensionxhtml5+"0325.xhtml",pathextensionsuit+pathextensionxhtml5+"0325.ttl" },
        	{ pathextensionsuit+pathextensionxhtml5+"0326.xhtml",pathextensionsuit+pathextensionxhtml5+"0326.ttl" },
        	{ pathextensionsuit+pathextensionxhtml5+"0327.xhtml",pathextensionsuit+pathextensionxhtml5+"0327.ttl" },
        	{ pathextensionsuit+pathextensionxhtml5+"0328.xhtml",pathextensionsuit+pathextensionxhtml5+"0328.ttl" },
        	{ pathextensionsuit+pathextensionxhtml5+"0329.xhtml",pathextensionsuit+pathextensionxhtml5+"0329.ttl" },
        	{ pathextensionsuit+pathextensionxhtml5+"0330.xhtml",pathextensionsuit+pathextensionxhtml5+"0330.ttl" },
        	{ pathextensionsuit+pathextensionxhtml5+"0331.xhtml",pathextensionsuit+pathextensionxhtml5+"0331.ttl" },
        	{ pathextensionsuit+pathextensionxhtml5+"0332.xhtml",pathextensionsuit+pathextensionxhtml5+"0332.ttl" },
        	{ pathextensionsuit+pathextensionxhtml5+"0333.xhtml",pathextensionsuit+pathextensionxhtml5+"0333.ttl" },
        	{ pathextensionsuit+pathextensionxhtml5+"0334.xhtml",pathextensionsuit+pathextensionxhtml5+"0334.ttl" },    	
        	{ pathextensionsuit+pathextensionxhtml5invalid+"0019.xhtml",pathextensionsuit+pathextensionxhtml5invalid+"0019.ttl" },	//XHTML5-INVALID
        	{ pathextensionsuit+pathextensionxhtml5invalid+"0035.xhtml",pathextensionsuit+pathextensionxhtml5invalid+"0035.ttl" },
        	{ pathextensionsuit+pathextensionxhtml5invalid+"0037.xhtml",pathextensionsuit+pathextensionxhtml5invalid+"0037.ttl" },
        	{ pathextensionsuit+pathextensionxhtml5invalid+"0039.xhtml",pathextensionsuit+pathextensionxhtml5invalid+"0039.ttl" },
        	{ pathextensionsuit+pathextensionxhtml5invalid+"0041.xhtml",pathextensionsuit+pathextensionxhtml5invalid+"0041.ttl" },
        	{ pathextensionsuit+pathextensionxhtml5invalid+"0079.xhtml",pathextensionsuit+pathextensionxhtml5invalid+"0079.ttl" },
        	{ pathextensionsuit+pathextensionxhtml5invalid+"0085.xhtml",pathextensionsuit+pathextensionxhtml5invalid+"0085.ttl" },
        	{ pathextensionsuit+pathextensionxhtml5invalid+"0087.xhtml",pathextensionsuit+pathextensionxhtml5invalid+"0087.ttl" },
        	{ pathextensionsuit+pathextensionxhtml5invalid+"0114.xhtml",pathextensionsuit+pathextensionxhtml5invalid+"0114.ttl" },
        	{ pathextensionsuit+pathextensionxhtml5invalid+"0131.xhtml",pathextensionsuit+pathextensionxhtml5invalid+"0131.ttl" },
        	{ pathextensionsuit+pathextensionxhtml5invalid+"0180.xhtml",pathextensionsuit+pathextensionxhtml5invalid+"0180.ttl" },
        	{ pathextensionsuit+pathextensionxhtml5invalid+"0222.xhtml",pathextensionsuit+pathextensionxhtml5invalid+"0222.ttl" },
        	{ pathextensionsuit+pathextensionxhtml5invalid+"0223.xhtml",pathextensionsuit+pathextensionxhtml5invalid+"0223.ttl" },
        	{ pathextensionsuit+pathextensionxhtml5invalid+"0226.xhtml",pathextensionsuit+pathextensionxhtml5invalid+"0226.ttl" },
        	{ pathextensionsuit+pathextensionxhtml5invalid+"0227.xhtml",pathextensionsuit+pathextensionxhtml5invalid+"0227.ttl" },
        	{ pathextensionsuit+pathextensionxhtml5invalid+"0230.xhtml",pathextensionsuit+pathextensionxhtml5invalid+"0230.ttl" },
        	{ pathextensionsuit+pathextensionxhtml5invalid+"0234.xhtml",pathextensionsuit+pathextensionxhtml5invalid+"0234.ttl" },
        	{ pathextensionsuit+pathextensionxhtml5invalid+"0258.xhtml",pathextensionsuit+pathextensionxhtml5invalid+"0258.ttl" },
        	{ pathextensionsuit+pathextensionxhtml5invalid+"0280.xhtml",pathextensionsuit+pathextensionxhtml5invalid+"0280.ttl" },
        	{ pathextensionsuit+pathextensionxhtml5invalid+"0285.xhtml",pathextensionsuit+pathextensionxhtml5invalid+"0285.ttl" },
        	{ pathextensionsuit+pathextensionxhtml5invalid+"0295.xhtml",pathextensionsuit+pathextensionxhtml5invalid+"0295.ttl" },
        	{ pathextensionsuit+pathextensionxhtml5invalid+"0303.xhtml",pathextensionsuit+pathextensionxhtml5invalid+"0303.ttl" },
        	{ pathextensionsuit+pathextensionxhtml5invalid+"0320.xhtml",pathextensionsuit+pathextensionxhtml5invalid+"0320.ttl" },        	
        	{ pathextensionsuit+pathextensionxml+"0001.xml",pathextensionsuit+pathextensionxml+"0001.ttl" },	//XML
        	{ pathextensionsuit+pathextensionxml+"0006.xml",pathextensionsuit+pathextensionxml+"0006.ttl" },
        	{ pathextensionsuit+pathextensionxml+"0007.xml",pathextensionsuit+pathextensionxml+"0007.ttl" },
        	{ pathextensionsuit+pathextensionxml+"0008.xml",pathextensionsuit+pathextensionxml+"0008.ttl" },
        	{ pathextensionsuit+pathextensionxml+"0009.xml",pathextensionsuit+pathextensionxml+"0009.ttl" },
        	{ pathextensionsuit+pathextensionxml+"0010.xml",pathextensionsuit+pathextensionxml+"0010.ttl" },
        	{ pathextensionsuit+pathextensionxml+"0012.xml",pathextensionsuit+pathextensionxml+"0012.ttl" },
        	{ pathextensionsuit+pathextensionxml+"0013.xml",pathextensionsuit+pathextensionxml+"0013.ttl" },
        	{ pathextensionsuit+pathextensionxml+"0014.xml",pathextensionsuit+pathextensionxml+"0014.ttl" },
        	{ pathextensionsuit+pathextensionxml+"0015.xml",pathextensionsuit+pathextensionxml+"0015.ttl" },
        	{ pathextensionsuit+pathextensionxml+"0017.xml",pathextensionsuit+pathextensionxml+"0017.ttl" },
        	{ pathextensionsuit+pathextensionxml+"0018.xml",pathextensionsuit+pathextensionxml+"0018.ttl" },
        	{ pathextensionsuit+pathextensionxml+"0019.xml",pathextensionsuit+pathextensionxml+"0019.ttl" },
        	{ pathextensionsuit+pathextensionxml+"0020.xml",pathextensionsuit+pathextensionxml+"0020.ttl" },
        	{ pathextensionsuit+pathextensionxml+"0021.xml",pathextensionsuit+pathextensionxml+"0021.ttl" },
        	{ pathextensionsuit+pathextensionxml+"0023.xml",pathextensionsuit+pathextensionxml+"0023.ttl" },
        	{ pathextensionsuit+pathextensionxml+"0025.xml",pathextensionsuit+pathextensionxml+"0025.ttl" },
        	{ pathextensionsuit+pathextensionxml+"0026.xml",pathextensionsuit+pathextensionxml+"0026.ttl" },
        	{ pathextensionsuit+pathextensionxml+"0027.xml",pathextensionsuit+pathextensionxml+"0027.ttl" },
        	{ pathextensionsuit+pathextensionxml+"0029.xml",pathextensionsuit+pathextensionxml+"0029.ttl" },
        	{ pathextensionsuit+pathextensionxml+"0030.xml",pathextensionsuit+pathextensionxml+"0030.ttl" },
        	{ pathextensionsuit+pathextensionxml+"0031.xml",pathextensionsuit+pathextensionxml+"0031.ttl" },
        	{ pathextensionsuit+pathextensionxml+"0032.xml",pathextensionsuit+pathextensionxml+"0032.ttl" },
        	{ pathextensionsuit+pathextensionxml+"0033.xml",pathextensionsuit+pathextensionxml+"0033.ttl" },
        	{ pathextensionsuit+pathextensionxml+"0034.xml",pathextensionsuit+pathextensionxml+"0034.ttl" },
        	{ pathextensionsuit+pathextensionxml+"0035.xml",pathextensionsuit+pathextensionxml+"0035.ttl" },
        	{ pathextensionsuit+pathextensionxml+"0036.xml",pathextensionsuit+pathextensionxml+"0036.ttl" },
        	{ pathextensionsuit+pathextensionxml+"0037.xml",pathextensionsuit+pathextensionxml+"0037.ttl" },
        	{ pathextensionsuit+pathextensionxml+"0038.xml",pathextensionsuit+pathextensionxml+"0038.ttl" },
        	{ pathextensionsuit+pathextensionxml+"0039.xml",pathextensionsuit+pathextensionxml+"0039.ttl" },
        	{ pathextensionsuit+pathextensionxml+"0041.xml",pathextensionsuit+pathextensionxml+"0041.ttl" },
        	{ pathextensionsuit+pathextensionxml+"0048.xml",pathextensionsuit+pathextensionxml+"0048.ttl" },
        	{ pathextensionsuit+pathextensionxml+"0049.xml",pathextensionsuit+pathextensionxml+"0049.ttl" },
        	{ pathextensionsuit+pathextensionxml+"0050.xml",pathextensionsuit+pathextensionxml+"0050.ttl" },
        	{ pathextensionsuit+pathextensionxml+"0051.xml",pathextensionsuit+pathextensionxml+"0051.ttl" },
        	{ pathextensionsuit+pathextensionxml+"0052.xml",pathextensionsuit+pathextensionxml+"0052.ttl" },
        	{ pathextensionsuit+pathextensionxml+"0053.xml",pathextensionsuit+pathextensionxml+"0053.ttl" },
        	{ pathextensionsuit+pathextensionxml+"0054.xml",pathextensionsuit+pathextensionxml+"0054.ttl" },
        	{ pathextensionsuit+pathextensionxml+"0055.xml",pathextensionsuit+pathextensionxml+"0055.ttl" },
        	{ pathextensionsuit+pathextensionxml+"0056.xml",pathextensionsuit+pathextensionxml+"0056.ttl" },
        	{ pathextensionsuit+pathextensionxml+"0057.xml",pathextensionsuit+pathextensionxml+"0057.ttl" },
        	{ pathextensionsuit+pathextensionxml+"0059.xml",pathextensionsuit+pathextensionxml+"0059.ttl" },
        	{ pathextensionsuit+pathextensionxml+"0060.xml",pathextensionsuit+pathextensionxml+"0060.ttl" },
        	{ pathextensionsuit+pathextensionxml+"0063.xml",pathextensionsuit+pathextensionxml+"0063.ttl" },
        	{ pathextensionsuit+pathextensionxml+"0064.xml",pathextensionsuit+pathextensionxml+"0064.ttl" },
        	{ pathextensionsuit+pathextensionxml+"0065.xml",pathextensionsuit+pathextensionxml+"0065.ttl" },
        	{ pathextensionsuit+pathextensionxml+"0067.xml",pathextensionsuit+pathextensionxml+"0067.ttl" },
        	{ pathextensionsuit+pathextensionxml+"0068.xml",pathextensionsuit+pathextensionxml+"0068.ttl" },
        	{ pathextensionsuit+pathextensionxml+"0071.xml",pathextensionsuit+pathextensionxml+"0071.ttl" },
        	{ pathextensionsuit+pathextensionxml+"0079.xml",pathextensionsuit+pathextensionxml+"0079.ttl" },
        	{ pathextensionsuit+pathextensionxml+"0080.xml",pathextensionsuit+pathextensionxml+"0080.ttl" },
        	{ pathextensionsuit+pathextensionxml+"0083.xml",pathextensionsuit+pathextensionxml+"0083.ttl" },
        	{ pathextensionsuit+pathextensionxml+"0084.xml",pathextensionsuit+pathextensionxml+"0084.ttl" },
        	{ pathextensionsuit+pathextensionxml+"0085.xml",pathextensionsuit+pathextensionxml+"0085.ttl" },
        	{ pathextensionsuit+pathextensionxml+"0087.xml",pathextensionsuit+pathextensionxml+"0087.ttl" },
        	{ pathextensionsuit+pathextensionxml+"0088.xml",pathextensionsuit+pathextensionxml+"0088.ttl" },
        	{ pathextensionsuit+pathextensionxml+"0089.xml",pathextensionsuit+pathextensionxml+"0089.ttl" },
        	{ pathextensionsuit+pathextensionxml+"0091.xml",pathextensionsuit+pathextensionxml+"0091.ttl" },
        	{ pathextensionsuit+pathextensionxml+"0093.xml",pathextensionsuit+pathextensionxml+"0093.ttl" },
        	{ pathextensionsuit+pathextensionxml+"0099.xml",pathextensionsuit+pathextensionxml+"0099.ttl" },
        	{ pathextensionsuit+pathextensionxml+"0104.xml",pathextensionsuit+pathextensionxml+"0104.ttl" },
        	{ pathextensionsuit+pathextensionxml+"0106.xml",pathextensionsuit+pathextensionxml+"0106.ttl" },
        	{ pathextensionsuit+pathextensionxml+"0107.xml",pathextensionsuit+pathextensionxml+"0107.ttl" },
        	{ pathextensionsuit+pathextensionxml+"0108.xml",pathextensionsuit+pathextensionxml+"0108.ttl" },
        	{ pathextensionsuit+pathextensionxml+"0112.xml",pathextensionsuit+pathextensionxml+"0112.ttl" },
        	{ pathextensionsuit+pathextensionxml+"0113.xml",pathextensionsuit+pathextensionxml+"0113.ttl" },
        	{ pathextensionsuit+pathextensionxml+"0115.xml",pathextensionsuit+pathextensionxml+"0115.ttl" },
        	{ pathextensionsuit+pathextensionxml+"0118.xml",pathextensionsuit+pathextensionxml+"0118.ttl" },
        	{ pathextensionsuit+pathextensionxml+"0119.xml",pathextensionsuit+pathextensionxml+"0119.ttl" },
        	{ pathextensionsuit+pathextensionxml+"0120.xml",pathextensionsuit+pathextensionxml+"0120.ttl" },
        	{ pathextensionsuit+pathextensionxml+"0121.xml",pathextensionsuit+pathextensionxml+"0121.ttl" },
        	{ pathextensionsuit+pathextensionxml+"0122.xml",pathextensionsuit+pathextensionxml+"0122.ttl" },
        	{ pathextensionsuit+pathextensionxml+"0126.xml",pathextensionsuit+pathextensionxml+"0126.ttl" },
        	{ pathextensionsuit+pathextensionxml+"0140.xml",pathextensionsuit+pathextensionxml+"0140.ttl" },
        	{ pathextensionsuit+pathextensionxml+"0147.xml",pathextensionsuit+pathextensionxml+"0147.ttl" },
        	{ pathextensionsuit+pathextensionxml+"0174.xml",pathextensionsuit+pathextensionxml+"0174.ttl" },
        	{ pathextensionsuit+pathextensionxml+"0175.xml",pathextensionsuit+pathextensionxml+"0175.ttl" },
        	{ pathextensionsuit+pathextensionxml+"0176.xml",pathextensionsuit+pathextensionxml+"0176.ttl" },
        	{ pathextensionsuit+pathextensionxml+"0180.xml",pathextensionsuit+pathextensionxml+"0180.ttl" },
        	{ pathextensionsuit+pathextensionxml+"0181.xml",pathextensionsuit+pathextensionxml+"0181.ttl" },
        	{ pathextensionsuit+pathextensionxml+"0196.xml",pathextensionsuit+pathextensionxml+"0196.ttl" },
        	{ pathextensionsuit+pathextensionxml+"0202.xml",pathextensionsuit+pathextensionxml+"0202.ttl" },
        	{ pathextensionsuit+pathextensionxml+"0203.xml",pathextensionsuit+pathextensionxml+"0203.ttl" },
        	{ pathextensionsuit+pathextensionxml+"0206.xml",pathextensionsuit+pathextensionxml+"0206.ttl" },
        	{ pathextensionsuit+pathextensionxml+"0207.xml",pathextensionsuit+pathextensionxml+"0207.ttl" },
        	{ pathextensionsuit+pathextensionxml+"0213.xml",pathextensionsuit+pathextensionxml+"0213.ttl" },
        	{ pathextensionsuit+pathextensionxml+"0214.xml",pathextensionsuit+pathextensionxml+"0214.ttl" },
        	{ pathextensionsuit+pathextensionxml+"0229.xml",pathextensionsuit+pathextensionxml+"0229.ttl" },
        	{ pathextensionsuit+pathextensionxml+"0246.xml",pathextensionsuit+pathextensionxml+"0246.ttl" },
        	{ pathextensionsuit+pathextensionxml+"0247.xml",pathextensionsuit+pathextensionxml+"0247.ttl" },
        	{ pathextensionsuit+pathextensionxml+"0248.xml",pathextensionsuit+pathextensionxml+"0248.ttl" },
        	{ pathextensionsuit+pathextensionxml+"0249.xml",pathextensionsuit+pathextensionxml+"0249.ttl" },
        	{ pathextensionsuit+pathextensionxml+"0250.xml",pathextensionsuit+pathextensionxml+"0250.ttl" },
        	{ pathextensionsuit+pathextensionxml+"0258.xml",pathextensionsuit+pathextensionxml+"0258.ttl" },
        	{ pathextensionsuit+pathextensionxml+"0259.xml",pathextensionsuit+pathextensionxml+"0259.ttl" },
        	{ pathextensionsuit+pathextensionxml+"0261.xml",pathextensionsuit+pathextensionxml+"0261.ttl" },
        	{ pathextensionsuit+pathextensionxml+"0262.xml",pathextensionsuit+pathextensionxml+"0262.ttl" },
        	{ pathextensionsuit+pathextensionxml+"0263.xml",pathextensionsuit+pathextensionxml+"0263.ttl" },
        	{ pathextensionsuit+pathextensionxml+"0264.xml",pathextensionsuit+pathextensionxml+"0264.ttl" },
        	{ pathextensionsuit+pathextensionxml+"0265.xml",pathextensionsuit+pathextensionxml+"0265.ttl" },
        	{ pathextensionsuit+pathextensionxml+"0266.xml",pathextensionsuit+pathextensionxml+"0266.ttl" },
        	{ pathextensionsuit+pathextensionxml+"0267.xml",pathextensionsuit+pathextensionxml+"0267.ttl" },
        	{ pathextensionsuit+pathextensionxml+"0268.xml",pathextensionsuit+pathextensionxml+"0268.ttl" },
        	{ pathextensionsuit+pathextensionxml+"0269.xml",pathextensionsuit+pathextensionxml+"0269.ttl" },
        	{ pathextensionsuit+pathextensionxml+"0271.xml",pathextensionsuit+pathextensionxml+"0271.ttl" },
        	{ pathextensionsuit+pathextensionxml+"0289.xml",pathextensionsuit+pathextensionxml+"0289.ttl" },
        	{ pathextensionsuit+pathextensionxml+"0290.xml",pathextensionsuit+pathextensionxml+"0290.ttl" },
        	{ pathextensionsuit+pathextensionxml+"0291.xml",pathextensionsuit+pathextensionxml+"0291.ttl" },
        	{ pathextensionsuit+pathextensionxml+"0292.xml",pathextensionsuit+pathextensionxml+"0292.ttl" },
        	{ pathextensionsuit+pathextensionxml+"0293.xml",pathextensionsuit+pathextensionxml+"0293.ttl" },
        	{ pathextensionsuit+pathextensionxml+"0295.xml",pathextensionsuit+pathextensionxml+"0295.ttl" },
        	{ pathextensionsuit+pathextensionxml+"0296.xml",pathextensionsuit+pathextensionxml+"0296.ttl" },
        	{ pathextensionsuit+pathextensionxml+"0297.xml",pathextensionsuit+pathextensionxml+"0297.ttl" },
        	{ pathextensionsuit+pathextensionxml+"0298.xml",pathextensionsuit+pathextensionxml+"0298.ttl" },
        	{ pathextensionsuit+pathextensionxml+"0299.xml",pathextensionsuit+pathextensionxml+"0299.ttl" },
        	{ pathextensionsuit+pathextensionxml+"0300.xml",pathextensionsuit+pathextensionxml+"0300.ttl" },
        	{ pathextensionsuit+pathextensionxml+"0301.xml",pathextensionsuit+pathextensionxml+"0301.ttl" },
        	{ pathextensionsuit+pathextensionxml+"0302.xml",pathextensionsuit+pathextensionxml+"0302.ttl" },
        	{ pathextensionsuit+pathextensionxml+"0311.xml",pathextensionsuit+pathextensionxml+"0311.ttl" },
        	{ pathextensionsuit+pathextensionxml+"0318.xml",pathextensionsuit+pathextensionxml+"0318.ttl" },
        	{ pathextensionsuit+pathextensionxml+"0319.xml",pathextensionsuit+pathextensionxml+"0319.ttl" },
        	{ pathextensionsuit+pathextensionxml+"0320.xml",pathextensionsuit+pathextensionxml+"0320.ttl" },
        	{ pathextensionsuit+pathextensionxml+"0329.xml",pathextensionsuit+pathextensionxml+"0329.ttl" },
        	{ pathextensionsuit+pathextensionxml+"0330.xml",pathextensionsuit+pathextensionxml+"0330.ttl" },
        	{ pathextensionsuit+pathextensionxml+"0331.xml",pathextensionsuit+pathextensionxml+"0331.ttl" },
        	{ pathextensionsuit+pathextensionxml+"0332.xml",pathextensionsuit+pathextensionxml+"0332.ttl" },
        };
        return Arrays.asList(data);
    }
    
	@Test
	public void parsertest() throws URISyntaxException, IOException {
		sink = new InMemorySink();
		analyzer1 = new RDFaAnalyzer(collector);
//		analyzer2 = new RDFaParser(collector);
		
		String strindex = test.getMethodName();
//		strindex = strindex.substring(11, strindex.indexOf(","));
//		int index = Integer.parseInt(strindex);		
		//curi = new CrawleableUri(new URI("rdfaParserTest"));
		System.out.println(strindex);
		
		
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
		
		analyzer1.analyze(curi, test, sink);
//		analyzer2.analyze(curi, test, sink);

		collector.closeSinkForUri(curi);
		sink.closeSinkForUri(curi);
		//System.out.print("Analyze ok ");
		
		List<byte[]> tdp = sink.getCrawledUnstructuredData().get(pathcontext);
		String decodedtest = "";
		if(tdp != null) decodedtest= new String(tdp.get(0), "UTF-8");
		//if(!decodedtest.equals(""))decodedtest = decodedtest.substring(0, decodedtest.length()-1);
		
		Model decodedmodel = createModelFromN3Strings(decodedtest);
		//System.out.print("created decodemodel ");
		
		//System.out.println(decodedtest);
		//System.out.println();
				
//		String correctresult = Files.readLines(result, Charset.forName("utf-8")).toString().replaceAll(", " ,"\n");
//	    correctresult = correctresult.substring(1,correctresult.length()-1);
		String correctresult = fileToString(result);		
		Model correctmodel = createModelFromTurtle(correctresult);
		//System.out.print("created correctmodel ");
		
		List<Double> results = new ArrayList<Double>();
		double fn = 0;
		double fp = 0;
		double tp = 0;
		Set<Statement> missingstatements = getMissingStatements(correctmodel, decodedmodel);
		for (Statement statement : missingstatements) {
//			falsenegativ[index]++;
			fn++;
		}
		//System.out.println();
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
		//System.out.println();
		
		//String filepath = getFilePath(decodedtest);
		//String path = getpath(filepath);
		//correctresult = correctresult.replace("<>",filepath );
		//correctresult = correctresult.replace("|", path);
		
		//System.out.println(correctresult);
		//System.out.println();
		
		//assertEquals(decodedtest,correctresult);
		
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
		
	}
	
}