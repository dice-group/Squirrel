package org.dice_research.squirrel.analyzer.compress.impl;

import org.apache.commons.compress.archivers.sevenz.SevenZArchiveEntry;
import org.apache.commons.compress.archivers.sevenz.SevenZFile;
import org.dice_research.squirrel.analyzer.compress.Decompressor;
import org.dice_research.squirrel.utils.TempPathUtils;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.List;

public class SevenZipDecompressor extends AbstractDecompressor implements Decompressor {

    protected SevenZipDecompressor() throws IOException {
        super();
    }

    @Override
    public List<File> decompress(File inputFile) throws IOException {
        File outputFile = createOutputFile();

        SevenZFile sevenZFile = new SevenZFile(inputFile);

        SevenZArchiveEntry entry;
        while ((entry = sevenZFile.getNextEntry()) != null) {
            if (entry.isDirectory()) {
                continue;
            }
            File curfile = new File(outputFile, entry.getName());
            File parent = curfile.getParentFile();
            if (!parent.exists()) {
                parent.mkdirs();
            }
            FileOutputStream out = new FileOutputStream(curfile);
            byte[] content = new byte[(int) entry.getSize()];
            sevenZFile.read(content, 0, content.length);
            out.write(content);
            out.close();
        }
        sevenZFile.close();

        return TempPathUtils.searchPath4Files(outputFile);
    }

}
