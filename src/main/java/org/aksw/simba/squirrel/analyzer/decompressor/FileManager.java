package org.aksw.simba.squirrel.analyzer.decompressor;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

import org.aksw.simba.squirrel.analyzer.decompressor.enums.MimeTypeEnum;
import org.apache.commons.compress.archivers.ArchiveInputStream;
import org.apache.commons.compress.archivers.sevenz.SevenZArchiveEntry;
import org.apache.commons.compress.archivers.sevenz.SevenZFile;
import org.apache.commons.compress.archivers.tar.TarArchiveEntry;
import org.apache.commons.compress.archivers.tar.TarArchiveInputStream;
import org.apache.commons.compress.archivers.zip.ZipArchiveEntry;
import org.apache.commons.compress.archivers.zip.ZipArchiveInputStream;
import org.apache.commons.compress.compressors.bzip2.BZip2CompressorInputStream;
import org.apache.commons.compress.compressors.gzip.GzipCompressorInputStream;
import org.apache.commons.compress.utils.IOUtils;
import org.apache.tika.Tika;

public class FileManager{
	
	private String in;
	private File tempFile;
	private String mime_type;
	private final File out; 
	
	
	public FileManager(File inputFile) throws IOException {
		this.in = inputFile.getAbsolutePath();
		this.tempFile = Files.createTempDirectory("file_").toFile();
		this.mime_type = detectMimeType(inputFile);
		this.out = new File(tempFile.getAbsolutePath());
	}
	
	
	public List<File> decompressFile() {
		
		try {
		if(MimeTypeEnum.TAR.mime_type().equals(mime_type) || MimeTypeEnum.TAR_GZ.mime_type().equals(mime_type)) {
				return decompressTar();

		} else if(MimeTypeEnum.ZIP.mime_type().equals(mime_type)) {
			return decompressZip();
		}else if(MimeTypeEnum.F7Z.mime_type().equals(mime_type)) {
			return decompress7z();
		}else if(MimeTypeEnum.BZ2.mime_type().equals(mime_type)) {
			return decompressBzip();
		}
		
		else return null;
		}catch(IOException e) {
			return null;
		}
	}
	
	/**
	 * Method responsible for decompressing Zip Files
	 * 
	 * @return
	 * @throws IOException
	 */

	private List<File> decompressZip() throws IOException{
		ZipArchiveInputStream fin = new ZipArchiveInputStream(new FileInputStream(in));
		
		ZipArchiveEntry entry;
		
		while ((entry = (ZipArchiveEntry) fin.getNextEntry()) != null) {
	        if (entry.isDirectory()) {
	            continue;
	        }
	        File curfile = new File(out, entry.getName());
	        File parent = curfile.getParentFile();
	        if (!parent.exists()) {
	            parent.mkdirs();
	        }
	        IOUtils.copy(fin, new FileOutputStream(curfile));
	    }
	    
	    	
	 return searchPath4Files(tempFile);
		
	}
	
	
	/**
	 * Method responsible for decompressing 7-Zip Files
	 * 
	 * @return
	 * @throws IOException
	 */
	
	private List<File> decompress7z() throws IOException{
		  SevenZFile sevenZFile = new SevenZFile(new File(in));
		  
	        SevenZArchiveEntry entry;
	        while ((entry = sevenZFile.getNextEntry()) != null){
	            if (entry.isDirectory()){
	                continue;
	            }
	            File curfile = new File(tempFile, entry.getName());
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
		
		return searchPath4Files(tempFile);
	}


	/**
	 * 
	 * Method responsible for decompressing Tar and Tar.Gz Files
	 * 
	 * @return
	 * @throws IOException
	 */
	private List<File> decompressTar() throws IOException {
			
			ArchiveInputStream fin = null;
			
			if(MimeTypeEnum.TAR.mime_type().equals(mime_type)) {
				fin = new TarArchiveInputStream(new FileInputStream(in));
			}
			else if(MimeTypeEnum.TAR_GZ.mime_type().equals(mime_type)) {
				fin = new TarArchiveInputStream(new GzipCompressorInputStream(new FileInputStream(in)));
			}else if(MimeTypeEnum.ZIP.mime_type().equals(mime_type)) {
				fin = new ZipArchiveInputStream(new FileInputStream(in));
			}
	
	            TarArchiveEntry entry;
	            while ((entry = (TarArchiveEntry) fin.getNextEntry()) != null) {
	                if (entry.isDirectory()) {
	                    continue;
	                }
	                File curfile = new File(out, entry.getName());
	                File parent = curfile.getParentFile();
	                if (!parent.exists()) {
	                    parent.mkdirs();
	                }
	                IOUtils.copy(fin, new FileOutputStream(curfile));
	            }
	            
	            	
	         return searchPath4Files(tempFile);
	       
	        
	    }

	
	/**
	 * 
	 * Method responsible for decompressing Tar and Tar.Gz Files
	 * 
	 * @return
	 * @throws IOException
	 */
	public List<File> decompressBzip() throws IOException {
		InputStream fin = Files.newInputStream(Paths.get(in));
		BufferedInputStream in = new BufferedInputStream(fin);
		OutputStream out = Files.newOutputStream(Paths.get(this.out + ".tar"));
		BZip2CompressorInputStream bzIn = new BZip2CompressorInputStream(in);
		final byte[] buffer = new byte[1000];
		int n = 0;
		while (-1 != (n = bzIn.read(buffer))) {
		    out.write(buffer, 0, n);
		}
		out.close();
		bzIn.close();
		
		this.in = this.out + ".tar";
		this.mime_type = MimeTypeEnum.TAR.mime_type();
		decompressTar();
		
		return searchPath4Files(tempFile);
	}



	private String detectMimeType(File file) throws IOException{
		Tika tika = new Tika();
        
            String mediaType = tika.detect(file);
            return mediaType;
        
	}

	
	private List<File> searchPath4Files(File tempPath){
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
	

}
