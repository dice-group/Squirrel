package org.aksw.simba.squirrel.analyzer.compress.impl;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;

import org.apache.tika.Tika;

public abstract class AbstractDecompressor {
	
	
	protected AbstractDecompressor() throws IOException {
//		tempFile = Files.createTempDirectory("file_").toFile();
//		this.mime_type = detectMimeType(inputFile);
//		this.inputFile = inputFile;
	}
	
	protected List<File> searchPath4Files(File tempPath){
		List<File> listFiles = new ArrayList<File>();
		
		for (final File fileEntry : tempPath.listFiles()) {
	        if (fileEntry.isDirectory()) {
	            listFiles.addAll(searchPath4Files(fileEntry));
	        } else {	        	
	        	listFiles.add(fileEntry);

	        }
	    }
		
		return listFiles;
	}

	private String detectMimeType(File file) throws IOException{
		Tika tika = new Tika();
        
            String mediaType = tika.detect(file);
            return mediaType;
        
	}
	
	protected File createOutputFile() throws IOException {
		return Files.createTempDirectory("file_").toFile();
	}

}
