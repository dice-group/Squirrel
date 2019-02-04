package org.dice_research.squirrel.fetcher.utils;

import java.io.File;
import static org.junit.Assert.*;
import org.junit.Test;

public class ZipArchiverTest {

    @Test
    public void testZipExtract() throws Exception {

        String password = null;
        String src = ZipArchiverTest.class.getClassLoader().getResource("archiverTest_files/sample_test.zip").getPath();
        String dest = ZipArchiverTest.class.getClassLoader().getResource("archiverTest_files/expected_files").getPath();
        int expected_files = 2;
        File[] actual_files = ZipArchiver.unzip(src, dest, password);
        assertEquals(expected_files, actual_files.length);
    }
}
