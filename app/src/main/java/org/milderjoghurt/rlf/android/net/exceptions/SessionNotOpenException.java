package org.milderjoghurt.rlf.android.net.exceptions;

public class SessionNotOpenException extends RuntimeException {
    public final String id;

    public SessionNotOpenException(final String id) {
        this.id = id;
    }

    @Override
    public String toString() {
        return "Session not open: " + id;
    }
}
