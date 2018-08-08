package org.aksw.simba.squirrel.analyzer.compress.impl;

import org.aksw.simba.squirrel.analyzer.compress.Decompressor;
import org.aksw.simba.squirrel.utils.TempPathUtils;
import org.apache.commons.compress.archivers.zip.ZipArchiveEntry;
import org.apache.commons.compress.archivers.zip.ZipArchiveInputStream;
import org.apache.commons.compress.utils.IOUtils;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.List;

public class ZipDecompressor extends AbstractDecompressor implements Decompressor {

    protected ZipDecompressor() throws IOException {
        super();
    }

    @Override
    public List<File> decompress(File inputFile) throws IOException {

        File outputFile = createOutputFile();

        ZipArchiveInputStream fin = new ZipArchiveInputStream(new FileInputStream(inputFile));

        ZipArchiveEntry entry;

        while ((entry = (ZipArchiveEntry) fin.getNextEntry()) != null) {
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
