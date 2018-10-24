package org.dice_research.squirrel.analyzer.compress.impl;

import org.apache.commons.compress.compressors.bzip2.BZip2CompressorInputStream;
import org.dice_research.squirrel.analyzer.compress.Decompressor;
import org.dice_research.squirrel.utils.TempPathUtils;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;

public class BzipDecompressor extends TarDecompressor implements Decompressor {

    protected BzipDecompressor() throws IOException {
        super();
    }

    @Override
    public List<File> decompress(File inputFile) throws IOException {

        File outputFile = createOutputFile();

        InputStream fin = Files.newInputStream(Paths.get(inputFile.getAbsolutePath()));
        BufferedInputStream in = new BufferedInputStream(fin);
        OutputStream out = Files.newOutputStream(Paths.get(outputFile + ".tar"));
        BZip2CompressorInputStream bzIn = new BZip2CompressorInputStream(in);
        final byte[] buffer = new byte[1000];
        int n = 0;
        while (-1 != (n = bzIn.read(buffer))) {
            out.write(buffer, 0, n);
        }
        out.close();
        bzIn.close();

        File tempoutputFile = new File(outputFile + ".tar");

        if (tempoutputFile.exists() && tempoutputFile.isFile()) {
            return new TarDecompressor().decompress(tempoutputFile);
        }

        return TempPathUtils.searchPath4Files(outputFile);
    }

}
