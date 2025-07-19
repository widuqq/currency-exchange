package com.widuq.util;

import com.widuq.validator.Error;
import com.widuq.validator.ValidationResult;
import lombok.experimental.UtilityClass;

import java.util.regex.Pattern;

@UtilityClass
public class CurrencyCodeValidationHelper {
    private static final String CURRENCY_CODE_REGEX = "^[A-Z]{3}$";
    private static final Pattern CURRENCY_CODE_PATTERN = Pattern.compile(CURRENCY_CODE_REGEX);
    private static final String CODE_FORMAT_ERROR = "Code must be exactly 3 uppercase letters (A-Z)";
    private static final String EMPTY_CODE_ERROR = "Code must not be empty";

    public static void validateCode(String code, ValidationResult result) {
        if (code == null || code.isBlank()) {
            result.add(Error.of(EMPTY_CODE_ERROR));
            return;
        }

        if (!CURRENCY_CODE_PATTERN.matcher(code).matches()) {
            result.add(Error.of(CODE_FORMAT_ERROR));
        }
    }
}
