package org.dice_research.squirrel.data.uri;

import java.io.FileReader;
import java.io.Reader;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.TreeMap;
import java.net.URI;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVRecord;
import org.apache.tika.Tika;
import org.dice_research.squirrel.Constants;

/**
 * CSV Parser for CSV Seed files.
 * 
 * @author Geraldo de Souza Junior (gsjunior@mail.uni-paderborn.de)
 *
 */


public class UriSeedReader {

    private Iterable<CSVRecord> records;
    private static final String URI = "uri";
    private static final String TYPE = "type";
    private boolean isCsv = true;


    public UriSeedReader(String seedFile) throws Exception {
        Tika tika = new Tika();
        String mimetype = tika.detect(seedFile);
        isCsv = mimetype.equals("text/csv");
        
        Reader in = new FileReader(seedFile);
        records = CSVFormat.DEFAULT.withFirstRecordAsHeader().parse(in);

    }

    public List<CrawleableUri> getUris() throws Exception{
        List<CrawleableUri> listUris = new ArrayList<CrawleableUri>();
        if(isCsv)
            for (CSVRecord record : records) {
                Map<String,String> mapRecords= new TreeMap<String, String>(String.CASE_INSENSITIVE_ORDER);
                mapRecords.putAll(record.toMap());
                if(!mapRecords.containsKey(URI) || !mapRecords.containsKey(TYPE)) {
                    throw new Exception("CSV seed files must contains at least uri and type as headers");
                }
                
                if(mapRecords.get(URI).isEmpty() || mapRecords.get(TYPE).isEmpty())
                    continue;
                
                System.out.println(mapRecords.get(URI));
                CrawleableUri curi = new CrawleableUri(new URI(mapRecords.get(URI)));
                curi.addData(Constants.URI_TYPE_KEY, mapRecords.get(TYPE));
                
                for(Entry<String, String> entry : mapRecords.entrySet()) {
                    if(entry.getKey().equalsIgnoreCase(URI) || entry.getKey().equalsIgnoreCase(TYPE))
                        continue;
                    
                    curi.addData(entry.getKey(), entry.getValue());
                }
                
                listUris.add(curi);
                
            }

        return listUris;
    }

}
