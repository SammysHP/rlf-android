package org.milderjoghurt.rlf.android.models;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.util.Date;

@JsonIgnoreProperties(ignoreUnknown = true)
public class Session {

    public String id;

    public String owner;

    public String name;

    public Date date = new Date();

    public Boolean open;

    public Date resetDate = new Date();

    public Session() {
    }

    public Session(String name) {
        this.name = name;
        this.open = true;
    }

    public Session(String name, Boolean open, Date date) {
        this(name);
        this.open = open;
        this.date = date;
    }
}
