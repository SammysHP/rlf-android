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

    public Session(String owner, String name) {
        this.owner = owner;
        this.name = name;
        this.open = true;
    }

    public Session(String owner, String name, Boolean open, Date date) {
        this(owner, name);
        this.open = open;
        this.date = date;
    }
}
