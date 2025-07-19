package com.widuq.exception;

import com.widuq.validator.Error;
import lombok.Getter;

@Getter
public class CurrencyNotFoundException extends RuntimeException {
    private static final String TEMPLATE_ERROR_MESSAGE = "Currency not found: %s";
    private final Error error;

    public CurrencyNotFoundException(String currencyCode) {
        this.error = Error.of(TEMPLATE_ERROR_MESSAGE.formatted(currencyCode));
    }
}
