package org.milderjoghurt.rlf.android.net.exceptions;

public class NoSuchSessionException extends RuntimeException {
    public final String id;

    public NoSuchSessionException(final String id) {
        this.id = id;
    }

    @Override
    public String toString() {
        return "No such session: " + id;
    }
}
