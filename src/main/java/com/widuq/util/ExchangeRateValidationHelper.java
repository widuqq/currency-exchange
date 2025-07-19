package com.widuq.util;

import com.widuq.validator.Error;
import com.widuq.validator.ValidationResult;
import lombok.experimental.UtilityClass;

import java.math.BigDecimal;

@UtilityClass
public class ExchangeRateValidationHelper {
    private static final String NULL_RATE_ERROR = "Exchange rate must not be null";
    private static final String NON_POSITIVE_RATE_ERROR = "Exchange rate must be positive";
    private static final String TOO_MANY_DECIMALS_ERROR = "Exchange rate cannot have more than 6 decimal places";
    private static final String RATE_TOO_LARGE_ERROR = "Exchange rate value is too large";
    private static final String FORMAT_RATE_ERROR = "Exchange rate must be a valid number";

    public static void validateRate(String strRate, ValidationResult validationResult) {
        if (strRate == null) {
            validationResult.add(Error.of(NULL_RATE_ERROR));
            return;
        }

        BigDecimal rate;

        try {
            rate = new BigDecimal(strRate);
        } catch (NumberFormatException e) {
            validationResult.add(Error.of(FORMAT_RATE_ERROR));
            return;
        }

        if (rate.compareTo(BigDecimal.ZERO) <= 0) {
            validationResult.add(Error.of(NON_POSITIVE_RATE_ERROR));
            return;
        }

        if (rate.scale() > 6) {
            validationResult.add(Error.of(TOO_MANY_DECIMALS_ERROR));
            return;
        }

        if (rate.precision() > 10) {
            validationResult.add(Error.of(RATE_TOO_LARGE_ERROR));
        }
    }
}
