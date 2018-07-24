package com.gci.pickem.exception;

public class InvalidUserAccessException extends Exception {

    public InvalidUserAccessException() {
    }

    public InvalidUserAccessException(String message) {
        super(message);
    }

    public InvalidUserAccessException(String message, Throwable cause) {
        super(message, cause);
    }

    public InvalidUserAccessException(Throwable cause) {
        super(cause);
    }

    public InvalidUserAccessException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
