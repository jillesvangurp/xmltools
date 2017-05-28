package io.inbot.xmltools.exceptions;

/**
 * Allows rethrowing pesky checked exceptions as a RethrownException.
 */
public class RethrownException extends RuntimeException {
    private static final long serialVersionUID = 8148492633858561044L;

    private final Class<?> type;

    public RethrownException(Throwable t) {
        super(t.getMessage(),t);
        this.type=t.getClass();
    }

    public Class<?> type() {
        return type;
    }

    public static RethrownException rethrow(Throwable t) {
        return new RethrownException(t);
    }
}
