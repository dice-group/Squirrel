package org.aksw.simba.squirrel.fetcher.ftp;

import java.io.IOException;
import java.io.OutputStream;

import org.apache.commons.net.ftp.FTPClient;
import org.apache.commons.net.ftp.FTPFile;

public class FTPRecursiveFetcher {
	
	private OutputStream output;
	
	protected FTPRecursiveFetcher(OutputStream output) {
		this.output = output;
	}
	
	
	 protected void listDirectory(FTPClient ftpClient, String parentDir,
	            String currentDir, int level) throws IOException {
	        String dirToList = parentDir;
	        if (!currentDir.equals("")) {
	            dirToList += "/" + currentDir;
	        }
	        FTPFile[] subFiles = ftpClient.listFiles(dirToList);
	        if (subFiles != null && subFiles.length > 0) {
	            for (FTPFile aFile : subFiles) {
	                String currentFileName = aFile.getName();
	                if (currentFileName.equals(".")
	                        || currentFileName.equals("..")) {
	                    // skip parent directory and directory itself
	                    continue;
	                }
	                for (int i = 0; i < level; i++) {
	                    System.out.print("\t");
	                }
	                if (aFile.isDirectory()) {
	                    System.out.println("[" + currentFileName + "]");
	                    listDirectory(ftpClient, dirToList, currentFileName, level + 1);
	                } else {
	                	ftpClient.retrieveFile(parentDir +"/"+ aFile.getName(),output);
	                    System.out.println(currentFileName);
	                }
	            }
	        }
	    }

}
