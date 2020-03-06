package org.dice_research.squirrel.analyzer.compress.impl;

import org.apache.commons.compress.archivers.zip.ZipArchiveEntry;
import org.apache.commons.compress.archivers.zip.ZipArchiveInputStream;
import org.apache.commons.compress.utils.IOUtils;
import org.dice_research.squirrel.Constants;
import org.dice_research.squirrel.analyzer.compress.Decompressor;
import org.dice_research.squirrel.data.uri.CrawleableUri;
import org.dice_research.squirrel.utils.TempPathUtils;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.List;

/**
 * Decompression implementation for the BZip format
 * 
 * @author gsjunior gsjunior@mail.uni-paderborn.de
 *
 */

public class ZipDecompressor extends AbstractDecompressor implements Decompressor {

    protected ZipDecompressor() throws IOException {
        super();
    }

    @Override
    public List<File> decompress(CrawleableUri curi,File inputFile) throws IOException {

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

        curi.addData(Constants.URI_HTTP_MIME_TYPE_KEY, "text/plain");
        return TempPathUtils.searchPath4Files(outputFile);
    }

}
