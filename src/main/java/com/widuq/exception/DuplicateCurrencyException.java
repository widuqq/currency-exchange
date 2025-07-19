package com.widuq.exception;

import com.widuq.validator.Error;
import lombok.Getter;

@Getter
public class DuplicateCurrencyException extends RuntimeException {
    private static final String TEMPLATE_ERROR_MESSAGE = "Currency already exists: %s";
    private final Error error;

    public DuplicateCurrencyException(String code) {
        this.error = Error.of(TEMPLATE_ERROR_MESSAGE.formatted(code));
    }
}
