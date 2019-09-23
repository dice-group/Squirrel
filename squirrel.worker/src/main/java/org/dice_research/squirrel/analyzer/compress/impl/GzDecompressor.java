package org.dice_research.squirrel.analyzer.compress.impl;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.zip.GZIPInputStream;

import org.dice_research.squirrel.analyzer.compress.Decompressor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * 
 * Decompress .gz files
 * 
 * 
 * 
 * @author Geraldo de Souza Junior gsjunior@mail.uni-paderborn.de
 *
 */
public class GzDecompressor extends TarDecompressor implements Decompressor {
	
    private static final Logger LOGGER = LoggerFactory.getLogger(GzDecompressor.class);


    protected GzDecompressor() throws IOException {
        super();
        // TODO Auto-generated constructor stub
    }

    @Override
    public List<File> decompress(File inputFile) throws IOException {
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
        return listFiles;

    }

}
