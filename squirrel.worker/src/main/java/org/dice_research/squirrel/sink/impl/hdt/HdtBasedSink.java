package org.dice_research.squirrel.sink.impl.hdt;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import org.apache.jena.graph.Triple;
import org.apache.jena.riot.Lang;
import org.dice_research.squirrel.data.uri.CrawleableUri;
import org.dice_research.squirrel.sink.impl.file.FileBasedSink;
import org.rdfhdt.hdt.enums.RDFNotation;
import org.rdfhdt.hdt.hdt.HDT;
import org.rdfhdt.hdt.hdt.HDTManager;
import org.rdfhdt.hdt.options.HDTSpecification;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * 
 * HDT File based Sink, uses the FileBasedSink to store the triples of the URI
 * in a temp folder and parses it to HDT when the sink is closed
 * 
 * @author gsjunior
 *
 */

public class HdtBasedSink extends FileBasedSink {
	
    private static final Logger LOGGER = LoggerFactory.getLogger(HdtBasedSink.class);

    private final ExecutorService EXECUTION_SERVICE = Executors.newScheduledThreadPool(100);
	
    protected File outputDirectory;
    
    /**
     * input type for parsing the file
     */
    protected String inputType = "ntriples";
    
    /**
     * outputstream for Metadata
     */
    protected FileOutputStream out = null;

	/**
	 * Creates a temp file for the FileBasedsink storage
	 * 
	 * @param outputDirectory
	 * @throws IOException
	 */
    public HdtBasedSink(File outputDirectory) throws IOException {

    	super(Files.createDirectory(new File(outputDirectory + "_hdt").toPath()).toFile(),Lang.NT, false);
		this.outputDirectory = outputDirectory;
	}

	@Override
	public void addTriple(CrawleableUri uri, Triple triple) {
		super.addTriple(uri, triple);
	}

	@Override
	public void openSinkForUri(CrawleableUri uri) {
		super.openSinkForUri(uri);
	}
	
//	@Override
//    public void addMetaData(Model model) {
//    	
//    	File metaDataOutputDirectory = new File(File.separator +outputDirectory.getAbsolutePath() + File.separator + "Metadata");
//    	metaDataOutputDirectory.mkdirs();
//    	
//    	CrawleableUri curi = new CrawleableUri(Constants.DEFAULT_META_DATA_GRAPH_URI);
//    	
//		try {
//			if(out == null) {
//			out = new FileOutputStream(File.separator + metaDataOutputDirectory.getAbsolutePath() + File.separator  + UriUtils.generateFileName(curi.getUri().toString(), false));
//			}
//			StreamRDFWriter.write(out, model.getGraph(), Lang.NT);
//			out.flush();
//		} catch (Exception e) {
//			// TODO Auto-generated catch block
//			LOGGER.error("Error when storing metadata: " + e);
//		}
//    	
//    }
	
	/**
	 * Recovers the temp file generated and parse it to hdt
	 * 
	 * @author gsjunior
	 */
	@Override
	public void closeSinkForUri(CrawleableUri uri) {
		String  rdfInput = super.outputDirectory.getAbsolutePath() + File.separator
                + generateFileName(uri, Lang.NT, false);
		
		super.closeSinkForUri(uri);
		
		EXECUTION_SERVICE.execute(new HDTParser(rdfInput,uri));
		try {
			EXECUTION_SERVICE.awaitTermination(1, TimeUnit.SECONDS);
		} catch (InterruptedException e) {
			LOGGER.error("",e);
		}
		
//		HDT hdt;
//		try {
//			hdt = HDTManager.generateHDT(
//			        rdfInput,         // Input RDF File
//			        uri.getUri().toString(),          // Base URI
//			        RDFNotation.parse(inputType), // Input Type
//			        new HDTSpecification(),   // HDT Options
//			        null              // Progress Listener
//   );
//			// Save generated HDT to a file
//			hdt.saveToHDT(outputDirectory.getAbsolutePath() + File.separator
//	                + generateFileName(uri.getUri().toString(), false), null);
//			
//		File file = new File(rdfInput);
//		file.delete();
//		} catch (Exception e) {
//            LOGGER.error("Should close the sink for the URI \"" + uri.getUri().toString() + "\" but an error occurred.");
//		}
		
	}

	@Override
	public void addData(CrawleableUri uri, InputStream stream) {
		super.addData(uri, stream);		
	}
	
    protected class HDTParser implements Runnable{

    	private String rdfInput;
    	private CrawleableUri uri;
    	
    	public HDTParser(String rdfInput, CrawleableUri uri) {
			this.rdfInput = rdfInput;
			this.uri = uri;
		}
    	
		@Override
		public void run() {
			HDT hdt;
			try {
				hdt = HDTManager.generateHDT(
				        rdfInput,         // Input RDF File
				        uri.getUri().toString(),          // Base URI
				        RDFNotation.parse(inputType), // Input Type
				        new HDTSpecification(),   // HDT Options
				        null              // Progress Listener
	   );
				// Save generated HDT to a file
				hdt.saveToHDT(outputDirectory.getAbsolutePath() + File.separator
		                + generateFileName(uri, null, false), null);
			
				File file = new File(rdfInput);
				file.delete();
		}catch (Exception e) {
            LOGGER.error("Should close the sink for the URI \"" + uri.getUri().toString() + "\" but an error occurred.");
		}
    	
    }
	

    }
    
}
