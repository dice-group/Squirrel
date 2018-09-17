package org.aksw.simba.squirrel.utils;

import java.io.File;
import java.io.IOException;

public class TempFileHelper {

    /**
     * Creates a temporary directory that can be used for tests. The directory will
     * be deleted when the JVM exits.
     * 
     * @param prefix
     *            a prefix for the directory name
     * @param suffix
     *            a suffix for the directory name
     * @return a {@link File} pointing to the generated directory
     * @throws IOException
     *             If an error occurs when creating the directory
     */
    public static File getTempDir(String prefix, String suffix) throws IOException {
        File temp = File.createTempFile(prefix, suffix);
        temp.delete();
        temp.mkdir();
        temp.deleteOnExit();
        return temp;
    }
}
