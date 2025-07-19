package com.widuq.exception;

import com.widuq.validator.Error;
import lombok.Getter;

@Getter
public class DuplicateExchangeRateException extends RuntimeException {
    private static final String TEMPLATE_ERROR_MESSAGE = "Exchange rate already exists: %s, %s";
    private final Error error;

    public DuplicateExchangeRateException(String baseCode, String targetCode) {
        this.error = Error.of(TEMPLATE_ERROR_MESSAGE.formatted(baseCode, targetCode));
    }
}
