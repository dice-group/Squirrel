package org.aksw.simba.squirrel.analyzer.compress.impl;

import org.aksw.simba.squirrel.analyzer.compress.enums.MimeTypeEnum;
import org.apache.tika.Tika;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class FileManager {

    private static final Logger LOGGER = LoggerFactory.getLogger(FileManager.class);

    public List<File> decompressFile(File inputFile) {
        List<File> file = new ArrayList<File>();
        try {
            String mime_type = detectMimeType(inputFile);
            if (MimeTypeEnum.TAR.mime_type().equals(mime_type)) {
                return new TarDecompressor().decompress(inputFile);
            } else if (MimeTypeEnum.TAR_GZ.mime_type().equals(mime_type)) {
                return new GzDecompressor().decompress(inputFile);
            } else if (MimeTypeEnum.ZIP.mime_type().equals(mime_type)) {
                return new ZipDecompressor().decompress(inputFile);
            } else if (MimeTypeEnum.F7Z.mime_type().equals(mime_type)) {
                return new SevenZipDecompressor().decompress(inputFile);
            } else if (MimeTypeEnum.BZ2.mime_type().equals(mime_type)) {
                return new BzipDecompressor().decompress(inputFile);
            } else {
                file.add(inputFile);
                return file;
            }
        } catch (IOException e) {
            LOGGER.error("Exception while Decompressing Data. Skipping...", e);
            return file;
        }
    }


    private String detectMimeType(File file) throws IOException {
        Tika tika = new Tika();

        String mediaType = tika.detect(file);
        return mediaType;

    }


}
