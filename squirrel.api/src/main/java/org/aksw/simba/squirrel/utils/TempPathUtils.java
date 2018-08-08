package org.aksw.simba.squirrel.utils;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class TempPathUtils {

    public static List<File> searchPath4Files(File tempPath) {
        if (tempPath == null) {
            return Collections.emptyList();
        }

        List<File> listFiles = new ArrayList<>();

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
