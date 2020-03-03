package org.dice_research.squirrel.analyzer.compress.impl;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.tika.Tika;
import org.dice_research.squirrel.analyzer.compress.enums.MimeTypeEnum;
import org.dice_research.squirrel.data.uri.CrawleableUri;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * Class responsible for detecting the fetched file mimetype
 * and decompress the file if necessary 
 * 
 * @author gsjunior gsjunior@mail.uni-paderborn.de
 *
 */

public class FileManager {

    private static final Logger LOGGER = LoggerFactory.getLogger(FileManager.class);

    public List<File> decompressFile(CrawleableUri curi, File inputFile) {
        List<File> file = new ArrayList<File>();
        try {
            String mime_type = detectMimeType(inputFile);
            if (MimeTypeEnum.TAR.mime_type().equals(mime_type)) {
                return new TarDecompressor().decompress(curi,inputFile);
            } else if (MimeTypeEnum.GZ.mime_type().equals(mime_type)) {
            	LOGGER.info("GZIP DETECTED");
                return new GzDecompressor().decompress(curi,inputFile);
            } else if (MimeTypeEnum.ZIP.mime_type().equals(mime_type)) {
                LOGGER.info("ZIP DETECTED");
                return new ZipDecompressor().decompress(curi,inputFile);
            } else if (MimeTypeEnum.F7Z.mime_type().equals(mime_type)) {
                return new SevenZipDecompressor().decompress(curi,inputFile);
            } else if (MimeTypeEnum.BZ2.mime_type().equals(mime_type)) {
                LOGGER.info("BZ2 DETECTED");
                return new BzipDecompressor().decompress(curi,inputFile);
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
