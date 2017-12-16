package comSquirrel.rabbitExchange;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

public class SquirrelWebObject {
    public enum State { NEW, WRITE, READ, OBSOLETE }

    ///////////////////
    //DATA/////////////
    ///////////////////

    //METADATA
    private int ID;
    private static int IDCOUNTER = 0;
    private Date writeTempStamp = null;
    private Date readTempStamp = null;
    private State currentState = State.NEW;

    //Data (Footer)
    private List<String> pendingURIs;
    private Map<String, List<String>> IPMapPendingURis;
    private List<String> nextCrawledURIs;
    private List<String> crawledURIs;
    private int countOfWorker;
    private int countofDeadWorker;
    private long RuntimeInSeconds;

    ///////////////////
    //METHODS//////////
    ///////////////////

    //Constructor
    public SquirrelWebObject() {
        ID = SquirrelWebObject.IDCOUNTER;
        SquirrelWebObject.IDCOUNTER++;
    }

    //Getter

    private void checkObsolete() {
        if (writeTempStamp == null)
            return;

        Date refer = new Date();
        //1h old = obsolete!
        refer.setTime(refer.getTime()-3600000);
        if (writeTempStamp.before(refer)) {
            currentState = State.OBSOLETE;
        }
    }

    private List<String> isReadable (List<String> object) {
        checkObsolete();

        List<String> ret = new ArrayList<>();
        if (currentState == State.NEW || currentState == State.OBSOLETE)  {
            ret.add("I'm sorry, but I can not read - this object " + ID + " is " + currentState.toString() + " and not written.");
        }
        if (object == null) {
            ret.add("The object, that you want to read is not set until yet!");
        }

        if (ret.isEmpty()) {
            currentState = State.READ;
            readTempStamp = new Date();
            return null;
        } else {
            return ret;
        }
    }

    private <E extends Number> E isReadable(E object) {
        checkObsolete();

        if (currentState == State.NEW || currentState == State.OBSOLETE || object == null)  {
            if(object instanceof Integer)
                return (E) Integer.valueOf(-1);
            else if (object instanceof Long)
                return (E) Long.valueOf(-1);
            else if (object instanceof Double)
                return (E) Double.valueOf(-1);
            else
                return null;
        }

        currentState = State.READ;
        readTempStamp = new Date();
        return object;
    }

    public List<String> getPendingURIs() {
        List<String> error = isReadable(pendingURIs);
        if (error == null) {
            return pendingURIs;
        } else {
            return error;
        }
    }

    public int getCountOfPendingURIs() {
        List<String> error = isReadable(pendingURIs);
        if (error == null) {
            return pendingURIs.size();
        } else {
            return -1;
        }
    }

    public List<String> getNextCrawledURIs() {
        List<String> error = isReadable(nextCrawledURIs);
        if (error == null) {
            return nextCrawledURIs;
        } else {
            return error;
        }
    }

    public List<String> getCrawledURIs() {
        List<String> error = isReadable(crawledURIs);
        if (error == null) {
            return crawledURIs;
        } else {
            return error;
        }
    }

    public int getCountOfWorker() {
        return isReadable(countOfWorker);
    }

    public int getCountOfDeadWorker() {
        return isReadable(countofDeadWorker);
    }

    public long getRuntimeInSeconds() {
        return isReadable(RuntimeInSeconds);
    }

    public String getWriteTime() {
        if (writeTempStamp == null) {
            return "Until the execution of this method, the object was never wrote!";
        }
        return writeTempStamp.toString();
    }

    public String getReadTime() {
        if (readTempStamp == null) {
            return "Until the execution of this method, the object was never read!";
        }

        return readTempStamp.toString() + ((currentState == State.OBSOLETE) ? " | WARNING: The object is obsolete!" : " | The object is ready to read!");
    }

    @Override
    public String toString() {
        return ID + ". Container in status " + currentState.toString() + " (" + hashCode() + ")";
    }

    //Setter

    private void isWritable() throws IllegalAccessException {
        if (currentState == State.READ) {
            throw new IllegalAccessException("The object was already readed! Please use a fresh new SquirrelWebObject!");
        }
        currentState = State.WRITE;
        writeTempStamp = new Date();
    }

    public void setPendingURIs(List<String> pendingURIs) {
        this.pendingURIs = pendingURIs;
    }

    public void setIPMapPendingURis(Map<String, List<String>> IPMapPendingURis) {
        this.IPMapPendingURis = IPMapPendingURis;
    }

    public void setCrawledURIs(List<String> crawledURIs) {
        this.crawledURIs = crawledURIs;
    }

    public void setCountOfWorker(int countOfWorker) {
        this.countOfWorker = countOfWorker;
    }

    public void setCountofDeadWorker(int countofDeadWorker) {
        this.countofDeadWorker = countofDeadWorker;
    }

    public void setNextCrawledURIs(List<String> nextCrawledURIs) {
        this.nextCrawledURIs = nextCrawledURIs;
    }

    public void setRuntimeInSeconds(long runtimeInSeconds) {
        RuntimeInSeconds = runtimeInSeconds;
    }
}