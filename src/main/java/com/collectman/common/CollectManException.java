package com.collectman.common;

public class CollectManException extends RuntimeException {

    public CollectManException() {
    }

    public CollectManException(String message) {
        super(message);
    }

    public CollectManException(String message, Throwable cause) {
        super(message, cause);
    }

    public CollectManException(Throwable cause) {
        super(cause);
    }
}
