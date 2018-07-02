package com;

import sun.reflect.generics.reflectiveObjects.NotImplementedException;

import java.io.*;
import java.util.*;

/**
 * SquirrelWebObject is a container class for the RabbitMQ to deliver the data from the Frontier to the Web-Service
 * @author Philipp Heinisch
 */
public class SquirrelWebObject implements Serializable {
    public enum State implements Serializable { NEW, WRITE, READ, OBSOLETE }

    ///////////////////
    //DATA/////////////
    ///////////////////

    //METADATA
    private int ID;
    private static int IDCOUNTER = 0;
    private Date writeDatetime = null;
    private Date readDatetime = null;
    private State currentState = State.NEW;

    //Data (Footer)
    private String pendingURIs;
    private String IPMapPendingURis;
    private String nextCrawledURIs;
    //private String crawledURIs;
    private int countOfCrawledURIs;
    private int countOfWorker;
    private int countOfDeadWorker;
    private long RuntimeInSeconds;

    ///////////////////
    //METHODS//////////
    ///////////////////

    //Constructor
    public SquirrelWebObject() {
        ID = SquirrelWebObject.IDCOUNTER;
        SquirrelWebObject.IDCOUNTER++;
    }

    private String ListToString(List<String> list) {
        final StringBuilder ret = new StringBuilder("<b>");
        if (list != null)
            list.forEach(e -> ret.append("<e>" + e + "</e>"));
        ret.append("</b>");
        return ret.toString();
    }

    private String MapToString(Map<String, List<String>> map) {
        StringBuilder ret = new StringBuilder("<m>");
        if (map != null) {
            map.forEach((k, v) -> ret.append("<c><h>" + k + "</h>" + ListToString(v) + "</c>"));
        }
        ret.append("</m>");

        return ret.toString();
    }

    private List<String> StringToList(String string) {
        if (string == null)
            return Collections.EMPTY_LIST;

        List<String> ret = new ArrayList<>();
        if (!string.startsWith("<b>") || !string.endsWith("</b>")) {
            ret.add("PARSING ERROR: String not valid! Pattern is not <b>{content]</b>!");
            ret.add(string);
            return ret;
        }

        StringBuilder buffer = new StringBuilder();
        boolean read = false;
        for (int i = 0; i < string.length(); i++) {
            if (string.charAt(i) == '>' && i > 3) {
                if (string.startsWith("</e", i-3)) {
                    ret.add(buffer.substring(1, buffer.length()-3));
                    buffer = new StringBuilder();
                    read = false;
                } else if (string.startsWith("<e", i-2)) {
                    read = true;
                }
            }
            if (read) {
                buffer.append(string.charAt(i));
            }
        }

        return ret;
    }

    private Map<String, List<String>> StringToMap(String string) {
        if (string == null)
            return Collections.EMPTY_MAP;

        Map<String, List<String>> ret = new HashMap<>();
        if (!string.startsWith("<m>") || !string.endsWith("</m>")) {
            List<String> retContent = new ArrayList<>(2);
            retContent.add("PARSING ERROR: String not valid! Pattern is not <b>{content]</b>!");
            retContent.add(string);
            ret.put("ERROR", retContent);
            return ret;
        }

        StringBuilder bufferKey = new StringBuilder(), bufferValue = new StringBuilder();
        boolean readKey = false;
        boolean readValue = false;
        for (int i = 0; i < string.length(); i++) {
            if (string.charAt(i) == '>' && i > 3) {
                if (string.startsWith("</c", i-3)) {
                    ret.put(bufferKey.substring(1, bufferKey.length()-3), StringToList(bufferValue.substring(1, bufferValue.length()-3)));
                    bufferKey = new StringBuilder();
                    bufferValue = new StringBuilder();
                    readKey = false;
                    readValue = false;
                }
                else if (string.startsWith("</h", i-3)) {
                    readKey = false;
                    readValue = true;
                }
                else if (string.startsWith("<h", i-2)) {
                    readKey = true;
                }
            }
            if (readKey)
                bufferKey.append(string.charAt(i));
            else if (readValue)
                bufferValue.append(string.charAt(i));
        }

        return ret;
    }

    //Getter

    private void checkObsolete() {
        if (writeDatetime == null)
            return;

        Date refer = new Date();
        //1h old = obsolete!
        refer.setTime(refer.getTime()-3600000);
        if (writeDatetime.before(refer)) {
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
            readDatetime = new Date();
            return null;
        } else {
            return ret;
        }
    }

    private Map<String, List<String>> isReadable (Map<String, List<String>> object) {
        checkObsolete();

        Map<String, List<String>> ret = new HashMap<>();
        List<String> arrayList = new ArrayList<>();
        if (currentState == State.NEW || currentState == State.OBSOLETE)  {
            arrayList.add("I'm sorry, but I can not read - this object " + ID + " is " + currentState.toString() + " and not written.");
        }
        if (object == null) {
            arrayList.add("The object, that you want to read is not set until yet!");
        }

        if (arrayList.isEmpty()) {
            currentState = State.READ;
            readDatetime = new Date();
            return null;
        } else {
            ret.put("ERROR", arrayList);
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
        readDatetime = new Date();
        return object;
    }

    public List<String> getPendingURIs() {
        List<String> ret = StringToList(pendingURIs);
        List<String> error = isReadable(ret);
        if (error == null) {
            return ret;
        } else {
            return error;
        }
    }

    public int getCountOfPendingURIs() {
        List<String> ret = StringToList(pendingURIs);
        List<String> error = isReadable(ret);
        if (error == null) {
            return ret.size();
        } else {
            return -1;
        }
    }

    public List<String> getNextCrawledURIs() {
        List<String> ret = StringToList(nextCrawledURIs);
        List<String> error = isReadable(ret);
        if (error == null) {
            return ret;
        } else {
            return error;
        }
    }

//    public List<String> getCrawledURIs() {
//        List<String> ret = StringToList(crawledURIs);
//        List<String> error = isReadable(ret);
//        if (error == null) {
//            return ret;
//        } else {
//            return error;
//        }
//    }

    public Map<String, List<String>> getIpStringListMap() {
        Map<String, List<String>> ret = StringToMap(IPMapPendingURis);
        Map<String, List<String>> error = isReadable(ret);

        if (error == null) {
            return ret;
        } else {
            return error;
        }
    }

    public int getCountOfWorker() {
        return isReadable(countOfWorker);
    }

    public int getCountOfDeadWorker() {
        return isReadable(countOfDeadWorker);
    }

    public long getRuntimeInSeconds() {
        return isReadable(RuntimeInSeconds);
    }

    public int getCountOfCrawledURIs() {
        return isReadable(countOfCrawledURIs);
    }

    public String getWriteTime() {
        if (writeDatetime == null) {
            return "Until the execution of this method, the object was never wrote!";
        }
        return writeDatetime.toString();
    }

    public String getReadTime() {
        if (readDatetime == null) {
            return "Until the execution of this method, the object was never read!";
        }

        return readDatetime.toString() + ((currentState == State.OBSOLETE) ? " | WARNING: The object is obsolete!" : " | The object is ready to read!");
    }

    @Override
    public String toString() {
        return ID + ". Container in status " + currentState.toString() + " (" + hashCode() + ")";
    }

    //Setter

    private void isWritable() throws IllegalAccessException {
        if (currentState == State.READ) {
            throw new IllegalAccessException("The object was already read! Please use a fresh new SquirrelWebObject!");
        }
        currentState = State.WRITE;
        writeDatetime = new Date();
    }

    public void setPendingURIs(List<String> pendingURIs) throws IllegalAccessException {
        isWritable();
        this.pendingURIs = ListToString(pendingURIs);
    }

    public void setIPMapPendingURis(Map<String, List<String>> IPMapPendingURis) throws IllegalAccessException {
        isWritable();
        this.IPMapPendingURis = MapToString(IPMapPendingURis);
    }

//    public void setCrawledURIs(List<String> crawledURIs) throws IllegalAccessException {
//        isWritable();
//        this.crawledURIs = ListToString(crawledURIs);
//    }

    public void setCountOfWorker(int countOfWorker) throws IllegalAccessException {
        isWritable();
        this.countOfWorker = countOfWorker;
    }

    public void setCountOfDeadWorker(int countOfDeadWorker) throws IllegalAccessException {
        isWritable();
        this.countOfDeadWorker = countOfDeadWorker;
    }

    public void setCountOfCrawledURIs(int countOfCrawledURIs) throws IllegalAccessException {
        isWritable();
        this.countOfCrawledURIs = countOfCrawledURIs;
    }

    public void setNextCrawledURIs(List<String> nextCrawledURIs) throws IllegalAccessException {
        isWritable();
        this.nextCrawledURIs = ListToString(nextCrawledURIs);
    }

    public void setRuntimeInSeconds(long runtimeInSeconds) throws IllegalAccessException {
        isWritable();
        RuntimeInSeconds = runtimeInSeconds;
    }

    @Override
    public boolean equals(Object o) {
        if (o != null && o instanceof SquirrelWebObject) {
            SquirrelWebObject compareSquirrel = (SquirrelWebObject) o;
            if (Math.abs(compareSquirrel.RuntimeInSeconds-RuntimeInSeconds) >= 10) {
                return false;
            }
            if ((pendingURIs == null && compareSquirrel.pendingURIs != null) ||
                    (nextCrawledURIs == null && compareSquirrel.nextCrawledURIs != null) ||
                    (IPMapPendingURis == null && compareSquirrel.IPMapPendingURis != null))
                return false;
            if (((pendingURIs == null && compareSquirrel.pendingURIs == null) || (pendingURIs.equals(compareSquirrel.pendingURIs))) &&
                    ((nextCrawledURIs == null && compareSquirrel.nextCrawledURIs == null) || (nextCrawledURIs.equals(compareSquirrel.nextCrawledURIs))) &&
                    ((IPMapPendingURis == null && compareSquirrel.IPMapPendingURis == null) || (IPMapPendingURis.equals(compareSquirrel.IPMapPendingURis))) &&
                    countOfWorker == compareSquirrel.countOfWorker &&
                    countOfDeadWorker == compareSquirrel.countOfDeadWorker &&
                    countOfCrawledURIs == compareSquirrel.countOfCrawledURIs)
                return true;
        }

        return false;
    }

    //for the RABBIT

    /**
     * converts the {@link SquirrelWebObject} into a byte stream (is needed for e.g. rabbitMQ)
     * @return a byte stream
     */
    public byte[] convertToByteStream() {
        try(ByteArrayOutputStream b = new ByteArrayOutputStream()){
            try(ObjectOutputStream o = new ObjectOutputStream(b)){
                o.writeObject(this);
            }
            return b.toByteArray();
        } catch (IOException e) {
            System.out.println("ERROR during serializing: " + e.getMessage());
            return new byte[] {};
        }
    }
}