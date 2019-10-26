package org.dice_research.squirrel.seed.csv;

import java.util.List;

import org.dice_research.squirrel.data.uri.CrawleableUri;
import org.dice_research.squirrel.data.uri.UriSeedReader;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

/**
 * CSV Parser Test for CSV Seed files.
 * 
 * @author Geraldo de Souza Junior (gsjunior@mail.uni-paderborn.de)
 *
 */

public class CsvSeedTest {

    private String seedFile1 = "src/test/resources/seeds/file1.csv";
    private String seedFile2 = "src/test/resources/seeds/file2.csv";
    private String seedFile3 = "src/test/resources/seeds/file3.csv";
    private String seedFile4 = "src/test/resources/seeds/singleUriColumn.csv";
    
    @Rule
    public ExpectedException exceptionRule = ExpectedException.none();

    
    /**
     * Testing the file with no problems
     * @throws Exception
     */
    @Test
    public void testFile1() throws Exception {
        int expected = 130;
        List<CrawleableUri> listUris = new UriSeedReader(seedFile1).getUris();
        Assert.assertEquals(expected, listUris.size());
    }

    /**
     * Testing the file with some records missing the type of key-value pair
     * @throws Exception
     */
    @Test
    public void testFile2() throws Exception {
        int expected = 130;
        List<CrawleableUri> listUris = new UriSeedReader(seedFile2).getUris();
        Assert.assertEquals(expected, listUris.size());
    }
    
    /**
     * Testing the file with missing header, expecting exception
     * @throws Exception
     */
    @Test
    public void testFile3() throws Exception {
        exceptionRule.expect(Exception.class);
        exceptionRule.expectMessage("The header <uri> is missing");
        new UriSeedReader(seedFile3).getUris();
    }
    
    /**
     * Testing the file with only one single URI column
     * @throws Exception
     */
    @Test
    public void testSingleUriColumn() throws Exception {
        int expected = 5;
        List<CrawleableUri> listUris = new UriSeedReader(seedFile4).getUris();
        Assert.assertEquals(expected, listUris.size());
    }

}
