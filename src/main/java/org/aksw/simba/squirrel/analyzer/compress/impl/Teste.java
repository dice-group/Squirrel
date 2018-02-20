package org.aksw.simba.squirrel.analyzer.compress.impl;

import java.io.File;
import java.io.IOException;
import java.util.List;

public class Teste {
	
	public static void main(String[] args) throws IOException {
		
		File arquivo = new File("/home/gsjunior/Documents/test_decompress/test_file.tar.gz");
		FileManager fm = new FileManager();
		List<File> arquivos =  fm.decompressFile(arquivo);
		
		for(File file : arquivos) {
			System.out.println(file);
		}
		
	}

}
