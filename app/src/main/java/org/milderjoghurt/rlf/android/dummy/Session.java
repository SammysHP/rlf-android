package org.milderjoghurt.rlf.android.dummy;

import java.util.Date;
import java.util.Random;

public class Session {
    String id;
    String name;
    Boolean open;
    Date date;

    public Session(String id, String name, Boolean open) {
        this.id = id;
        this.name = name;
        this.open = open;
        Random rnd = new Random();
        this.date = new Date(Math.abs(System.currentTimeMillis() - rnd.nextInt()));
    }

    @Override
    public String toString() {
        return name;
    }
}
