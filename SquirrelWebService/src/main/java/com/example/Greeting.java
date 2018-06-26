package com.example;

import java.util.ArrayList;
import java.util.List;

public class Greeting {

    private final long id;
    private final String content;

    public Greeting(long id, String content) {
        this.id = id;
        this.content = content;
    }

    public long getId() {
        return id;
    }

    public String getContent() {
        return content;
    }

    public List<String> getList() {
        List<String> r = new ArrayList<>(2);
        r.add("ArrayList: ");
        r.add(content);
        return r;
    }

}
