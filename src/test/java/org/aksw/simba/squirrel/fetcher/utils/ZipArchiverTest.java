package org.aksw.simba.squirrel.fetcher.utils;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.fail;

import java.io.File;
import java.util.zip.ZipFile;

import org.junit.Test;

import com.mchange.util.AssertException;

public class ZipArchiverTest {

    @Test
    public void testZipExtract() throws Exception{
        String src = "C:\\Users\\kpten\\IdeaProjects\\Integration_Testing\\Squirrel\\src\\test\\resources\\sample_test.zip";
        String dest = "C:\\Users\\kpten\\IdeaProjects\\Integration_Testing\\Squirrel\\src\\test\\resources\\extracted_files" ;
        String password = null;
        ZipArchiver archiver = new ZipArchiver();
        File[] data = ZipArchiver.unzip(src, dest, password);
        assertNotNull(data);
    }
}
