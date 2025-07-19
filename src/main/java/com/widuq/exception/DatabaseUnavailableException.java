package com.widuq.exception;

public class DatabaseUnavailableException extends RuntimeException {
    public DatabaseUnavailableException(String msg, Throwable cause) {
        super(msg, cause);
    }
}
