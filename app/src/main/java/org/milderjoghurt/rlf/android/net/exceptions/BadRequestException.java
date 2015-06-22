package org.milderjoghurt.rlf.android.net.exceptions;

public class BadRequestException extends RuntimeException {
    public final String error;

    public BadRequestException(final String error) {
        this.error = error;
    }

    @Override
    public String toString() {
        return "Bad request: " + error;
    }
}
