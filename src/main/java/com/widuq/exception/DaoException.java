package com.widuq.exception;

public class DaoException extends RuntimeException {
    public DaoException(String msg, Throwable cause) {
        super(msg, cause);
    }
}
