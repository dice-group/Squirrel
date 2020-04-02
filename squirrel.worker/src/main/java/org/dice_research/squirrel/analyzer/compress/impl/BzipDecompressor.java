package org.dice_research.squirrel.analyzer.compress.impl;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.compress.compressors.bzip2.BZip2CompressorInputStream;
import org.apache.tika.Tika;
import org.dice_research.squirrel.Constants;
import org.dice_research.squirrel.analyzer.compress.Decompressor;
import org.dice_research.squirrel.analyzer.compress.enums.MimeTypeEnum;
import org.dice_research.squirrel.data.uri.CrawleableUri;


/**
 * Decompression implementation for the BZip format
 * 
 * @author gsjunior gsjunior@mail.uni-paderborn.de
 *
 */
public class BzipDecompressor extends TarDecompressor implements Decompressor {

    protected BzipDecompressor() throws IOException {
        super();
    }

    @Override
    public List<File> decompress(CrawleableUri curi, File inputFile) throws IOException {

        File outputFile = File.createTempFile("tempFile", Long.toString(System.nanoTime()));

        int buffersize = 1024;

        FileInputStream in = new FileInputStream(inputFile);
        FileOutputStream out = new FileOutputStream(outputFile);
        BZip2CompressorInputStream bzIn = new BZip2CompressorInputStream(in);
        final byte[] buffer = new byte[buffersize];
        int n = 0;
        while (-1 != (n = bzIn.read(buffer))) {
            out.write(buffer, 0, n);
        }
        out.close();
        bzIn.close();

        List<File> listFiles = new ArrayList<File>();
        listFiles.add(outputFile);
        curi.addData(Constants.URI_HTTP_MIME_TYPE_KEY, "text/plain");
        
        Tika tika = new Tika();
        if(listFiles.size() == 1 && tika.detect(listFiles.get(0)).equals(MimeTypeEnum.TAR.mime_type()))
            return super.decompress(curi, listFiles.get(0));
        
        return listFiles;

    }


}
