package org.dice_research.squirrel.analyzer.compress.impl;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.zip.GZIPInputStream;

import org.apache.tika.Tika;
import org.dice_research.squirrel.Constants;
import org.dice_research.squirrel.analyzer.compress.Decompressor;
import org.dice_research.squirrel.analyzer.compress.enums.MimeTypeEnum;
import org.dice_research.squirrel.data.uri.CrawleableUri;


/**
 * Decompression implementation for the GZ format
 * 
 * @author gsjunior gsjunior@mail.uni-paderborn.de
 *
 */
public class GzDecompressor extends TarDecompressor implements Decompressor {
	

    protected GzDecompressor() throws IOException {
        super();
        // TODO Auto-generated constructor stub
    }

    @Override
    public List<File> decompress(CrawleableUri curi, File inputFile) throws IOException {
        File outputFile = File.createTempFile("tempFile", Long.toString(System.nanoTime()));
        
        byte[] buffer = new byte[1024];


        GZIPInputStream gzis = null;

        gzis =  new GZIPInputStream(new FileInputStream(inputFile));

        FileOutputStream out = 
                new FileOutputStream(outputFile);
     
            int len;
            while ((len = gzis.read(buffer)) > 0) {
            	out.write(buffer, 0, len);
            }
     
            gzis.close();
        	out.close();
        	
        List<File> listFiles = new ArrayList<>();
        listFiles.add(outputFile);
        curi.addData(Constants.URI_HTTP_MIME_TYPE_KEY, "text/plain");
        
        Tika tika = new Tika();
        if(listFiles.size() == 1 && tika.detect(listFiles.get(0)).equals(MimeTypeEnum.TAR.mime_type()))
            return super.decompress(curi, listFiles.get(0));
        
        


        return listFiles;

    }

}
