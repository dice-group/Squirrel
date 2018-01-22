package org.aksw.simba.squirrel.analyzer.compress.impl;

import java.io.File;
import java.io.IOException;
import java.util.List;

import org.aksw.simba.squirrel.analyzer.compress.enums.MimeTypeEnum;
import org.apache.tika.Tika;

public class FileManager{
	
	
	
	public List<File> decompressFile(File inputFile) {
		
		
		
		try {
			String mime_type = detectMimeType(inputFile);
			if(MimeTypeEnum.TAR.mime_type().equals(mime_type)) {
					return new TarDecompressor().decompress(inputFile);
			}else if(MimeTypeEnum.TAR_GZ.mime_type().equals(mime_type)) {
					return new GzDecompressor().decompress(inputFile);
			} else if(MimeTypeEnum.ZIP.mime_type().equals(mime_type)) {
					return new ZipDecompressor().decompress(inputFile);
			}else if(MimeTypeEnum.F7Z.mime_type().equals(mime_type)) {
					return new SevenZipDecompressor().decompress(inputFile);
			}else if(MimeTypeEnum.BZ2.mime_type().equals(mime_type)) {
					return new BzipDecompressor().decompress(inputFile);
			}else {
				return null;
			}
		}catch(IOException e) {
			//TODO implement return messages
			return null;			
		}
	}
	

	private String detectMimeType(File file) throws IOException{
		Tika tika = new Tika();
        
            String mediaType = tika.detect(file);
            return mediaType;
        
	}

		

}
