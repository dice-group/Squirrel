package org.aksw.simba.squirrel.analyzer.compress.impl;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;

public abstract class AbstractDecompressor {


    protected AbstractDecompressor() throws IOException {
//		tempFile = Files.createTempDirectory("file_").toFile();
//		this.mime_type = detectMimeType(inputFile);
//		this.inputFile = inputFile;
    }


    protected File createOutputFile() throws IOException {
        return Files.createTempDirectory("file_").toFile();
    }

}
