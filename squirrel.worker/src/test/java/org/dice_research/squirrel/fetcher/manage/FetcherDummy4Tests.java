package org.dice_research.squirrel.fetcher.manage;

import org.dice_research.squirrel.data.uri.CrawleableUri;
import org.dice_research.squirrel.fetcher.Fetcher;

import java.io.File;
import java.io.IOException;
/**
 * A very simple dummy fetcher which provides the fetcher instances for {@link SimpleOrderedFetcherManagerTest)
 * in order to test the {@link SimpleOrderedFetcherManager}
 * There are two flags which have been used (resultFile and calledToFetch)
 * The constructor takes a resultFile flag which tells the fetcher that it should return a file if the flag is set to true
 * if resultFile = true , then the fetcher must return some file.
 * Additionally, calledToFetch flag is set as soon as the fetcher is asked to fetch i.e. setCalledToFetch(true);
 *
 * @author Ajay (ajay@uni-paderborn.de)
 *
 */
public class FetcherDummy4Tests implements Fetcher {

    public boolean resultFile = false;
    public boolean calledToFetch = false;
    public File output = null ;

    /**
     *
     * @param flag
     * sets the resultFile = flag and which specifies what must be resulted by the fetch method
     */
    public  FetcherDummy4Tests(boolean flag)
    {
        setResultFile(flag);
    }

    /**
     *
     * @param uri The URI from which data should be fetched.
     * @return returns the output based on the resultFile flag value.
     */
    public File fetch(CrawleableUri uri) {
        setCalledToFetch(true);
        if(isResultFile())
        { //checks isResultFile value and sets the result accordingly

            setOutput(new File("File.txt")); // to make sure there is some file to return
        }
        else{
            setOutput(null);
        }
        output = getOutput();
        return output;
    }

    public boolean isResultFile() {
        return resultFile;
    }

    public void setResultFile(boolean resultFile) {
        this.resultFile = resultFile;
    }

    public boolean isCalledToFetch() {
        return calledToFetch;
    }

    public void setCalledToFetch(boolean calledToFetch) {
        this.calledToFetch = calledToFetch;
    }

    public File getOutput() {
        return output;
    }

    public void setOutput(File output) {
        this.output = output;
    }

    public void close() throws IOException {

    }
}
