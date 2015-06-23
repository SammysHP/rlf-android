package org.milderjoghurt.rlf.android.models;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.util.Date;

@JsonIgnoreProperties(ignoreUnknown = true)
public class VoteStats {
    public Date date = new Date();

    public Integer value;

    public Type type;

    public enum Type {
        ALL,
        SPEED,
        UNDERSTANDABILITY,
        REQUEST,
        CURRENTUSERS,
    }

    public VoteStats() {
    }

    public VoteStats(Type type, Integer value) {
        this.type = type;
        this.value = value;
    }

}
