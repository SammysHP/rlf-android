package org.milderjoghurt.rlf.android.net.exceptions;

public class PermissionDeniedException extends RuntimeException {
    public final String error;

    public PermissionDeniedException(final String error) {
        this.error = error;
    }

    @Override
    public String toString() {
        return "Permission denied: " + error;
    }
}
