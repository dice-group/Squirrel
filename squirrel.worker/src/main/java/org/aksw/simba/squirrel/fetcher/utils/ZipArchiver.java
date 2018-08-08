package org.aksw.simba.squirrel.fetcher.utils;

import net.lingala.zip4j.core.ZipFile;
import net.lingala.zip4j.exception.ZipException;

import java.io.File;

/**
 * Created by ivan on 8/11/16.
 */
public class ZipArchiver {
    public static File[] unzip(String source, String destination, String password){
        try {
            ZipFile zipFile = new ZipFile(source);
            if (zipFile.isEncrypted()) {
                zipFile.setPassword(password);
            }
            zipFile.extractAll(destination);
        } catch (ZipException e) {
            e.printStackTrace();
        }
        return listFilesIn(destination);
    }

    private static File[] listFilesIn(String path) {
        File folder = new File(path);
        return folder.listFiles();
    }
}
