package org.aksw.simba.squirrel.sink.impl;

import java.text.SimpleDateFormat;
import java.util.Date;

public class TimeStamp {
    //private String getCurrentTimeStamp() {
    public static void main(String[] args) {
        Date date = new java.util.Date();
        String st= new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS").format(date);
        System.out.println("time=" + st );
        //return new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS").format(date);
    }
}
