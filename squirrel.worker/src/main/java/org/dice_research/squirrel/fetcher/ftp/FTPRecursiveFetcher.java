package org.dice_research.squirrel.fetcher.ftp;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Path;

import org.apache.commons.net.ftp.FTPClient;
import org.apache.commons.net.ftp.FTPFile;

public class FTPRecursiveFetcher {

    private Path path;


    protected FTPRecursiveFetcher(Path path) throws IOException {
        this.path = path;
        //OutputStream output = new FileOutputStream(File.createTempFile("fetched_", "", path.toFile()));
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
                    ftpClient.retrieveFile(parentDir + "/" + aFile.getName(), new FileOutputStream(File.createTempFile("fetched_", "", path.toFile())));
                    System.out.println(currentFileName);
                }
            }
        }
    }


}
