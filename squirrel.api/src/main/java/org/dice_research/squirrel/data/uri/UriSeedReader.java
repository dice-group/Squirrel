package org.dice_research.squirrel.data.uri;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.Reader;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.TreeMap;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVRecord;
import org.apache.commons.io.FileUtils;
import org.apache.tika.Tika;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * CSV Parser for CSV Seed files.
 * 
 * @author Geraldo de Souza Junior (gsjunior@mail.uni-paderborn.de)
 *
 */


public class UriSeedReader {

    private static final String URI = "uri";
    private boolean isCsv = true;
    private Reader in;
    private String filePath = "";
    
    
    private static final Logger LOGGER = LoggerFactory.getLogger(UriSeedReader.class);



    public UriSeedReader(String seedFile) {
    	this.filePath = seedFile;
        Tika tika = new Tika();
        String mimetype = tika.detect(seedFile);
        isCsv = "text/csv".equals(mimetype);
        
        try {
            in =   new FileReader(seedFile);

        } catch (Exception e) {
            LOGGER.error("Could not load seed file: " + seedFile);
         }
       
    }

    public List<CrawleableUri> getUris() throws IllegalArgumentException, IOException, URISyntaxException{
        List<CrawleableUri> listUris = new ArrayList<CrawleableUri>();
        Iterable<CSVRecord> records = CSVFormat.DEFAULT.withFirstRecordAsHeader().parse(in);

        if(isCsv) {
            for (CSVRecord record : records) {
                Map<String,String> mapRecords= new TreeMap<String, String>(String.CASE_INSENSITIVE_ORDER);
                mapRecords.putAll(record.toMap());
                if(!mapRecords.containsKey(URI) ) {
                    throw new IllegalArgumentException("The header <uri> is missing");
                }
                
                if(mapRecords.get(URI).isEmpty())
                    continue;
                CrawleableUri curi = new CrawleableUri(new URI(mapRecords.get(URI)));
                
                for(Entry<String, String> entry : mapRecords.entrySet()) {
                    if(entry.getKey().equalsIgnoreCase(URI))
                        continue;
                    
                    curi.addData(entry.getKey(), entry.getValue());
                }
                
                listUris.add(curi);
                
            }
        }else {
        	listUris = UriUtils.createCrawleableUriList(FileUtils.readLines(new File(filePath), StandardCharsets.UTF_8));
        }

        return listUris;
    }

}
