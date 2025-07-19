package com.widuq.exception;

import com.widuq.validator.Error;
import lombok.Getter;

@Getter
public class ExchangeRateNotFoundException extends RuntimeException {
    private static final String TEMPLATE_ERROR_MESSAGE = "Exchange rate not found: %s, %s";
    private final Error error;

    public ExchangeRateNotFoundException(String baseCode, String targetCode) {
        this.error = Error.of(TEMPLATE_ERROR_MESSAGE.formatted(baseCode, targetCode));
    }
}
