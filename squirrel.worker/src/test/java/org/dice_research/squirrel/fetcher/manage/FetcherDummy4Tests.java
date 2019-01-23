package org.dice_research.squirrel.fetcher.manage;

import org.dice_research.squirrel.data.uri.CrawleableUri;
import org.dice_research.squirrel.fetcher.Fetcher;
import org.springframework.beans.factory.annotation.Autowired;

import java.io.File;
import java.io.IOException;

public class FetcherDummy4Tests implements Fetcher {

    public boolean preFlag = false;
    public boolean postFlag = false;
    public File result = null ;

    public  FetcherDummy4Tests(boolean flag)  {
        setPreFlag(flag);
    }

    public File fetch(CrawleableUri uri) {
        setPostFlag(true);
        if(isPreFlag())
        { //checks preflag status and sets the result accordingly
            //setResult(new File(""));
            setResult(new File("File.txt")); // to make sure there is some file to return
        }
        else{
            setResult(null);
        }
        result = getResult();
        return result;
    }

    public boolean isPreFlag() {
        return preFlag;
    }

    public void setPreFlag(boolean preFlag) {
        this.preFlag = preFlag;
    }

    public boolean isPostFlag() {
        return postFlag;
    }

    public void setPostFlag(boolean postFlag) {
        this.postFlag = postFlag;
    }

    public File getResult() {
        return result;
    }

    public void setResult(File result) {
        this.result = result;
    }

    public void close() throws IOException {

    }
}
