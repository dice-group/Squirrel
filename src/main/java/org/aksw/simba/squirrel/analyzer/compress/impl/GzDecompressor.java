package org.aksw.simba.squirrel.analyzer.compress.impl;

import org.aksw.simba.squirrel.analyzer.compress.Decompressor;
import org.aksw.simba.squirrel.utils.TempPathUtils;
import org.apache.commons.compress.archivers.ArchiveInputStream;
import org.apache.commons.compress.archivers.tar.TarArchiveEntry;
import org.apache.commons.compress.archivers.tar.TarArchiveInputStream;
import org.apache.commons.compress.compressors.gzip.GzipCompressorInputStream;
import org.apache.commons.compress.utils.IOUtils;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.List;

public class GzDecompressor extends TarDecompressor implements Decompressor {

    protected GzDecompressor() throws IOException {
        super();
        // TODO Auto-generated constructor stub
    }

    @Override
    public List<File> decompress(File inputFile) throws IOException {
        File outputFile = createOutputFile();

        ArchiveInputStream fin = null;

        fin = new TarArchiveInputStream(new GzipCompressorInputStream(new FileInputStream(inputFile)));

        TarArchiveEntry entry;
        while ((entry = (TarArchiveEntry) fin.getNextEntry()) != null) {
            if (entry.isDirectory()) {
                continue;
            }
            File curfile = new File(outputFile, entry.getName());
            File parent = curfile.getParentFile();
            if (!parent.exists()) {
                parent.mkdirs();
            }
            IOUtils.copy(fin, new FileOutputStream(curfile));
        }


        return TempPathUtils.searchPath4Files(outputFile);

    }

}
