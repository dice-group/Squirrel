package org.dice_research.squirrel.analyzer.compress.impl;

import org.apache.commons.compress.archivers.ArchiveInputStream;
import org.apache.commons.compress.archivers.tar.TarArchiveEntry;
import org.apache.commons.compress.archivers.tar.TarArchiveInputStream;
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
 * Decompression implementation for the *.tar format
 * 
 * @author gsjunior gsjunior@mail.uni-paderborn.de
 *
 */

public class TarDecompressor extends AbstractDecompressor implements Decompressor {


    protected TarDecompressor() throws IOException {
        super();
    }

    @Override
    public List<File> decompress(CrawleableUri curi, File inputFile) throws IOException {

        File outputFile = createOutputFile();

        ArchiveInputStream fin = null;

        fin = new TarArchiveInputStream(new FileInputStream(inputFile));


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

        curi.addData(Constants.URI_HTTP_MIME_TYPE_KEY, "text/plain");

        return TempPathUtils.searchPath4Files(outputFile);

    }


}
