package com.widuq.validator;

import com.widuq.dto.CreateExchangeRateDto;
import com.widuq.util.CurrencyCodeValidationHelper;
import com.widuq.util.ExchangeRateValidationHelper;

public class CreateExchangeRateValidator implements Validator<CreateExchangeRateDto> {
    private static final String BASE_CODE_EQUALS_TARGET_CODE_ERROR = "Base currency code and target currency code must not be equals";

    private static final CreateExchangeRateValidator INSTANCE = new CreateExchangeRateValidator();

    private CreateExchangeRateValidator() {
    }

    public static CreateExchangeRateValidator getInstance() {
        return INSTANCE;
    }

    @Override
    public ValidationResult isValid(CreateExchangeRateDto object) {
        var validationResult = new ValidationResult();

        String baseCode = object.getBaseCurrencyCode();
        String targetCode = object.getTargetCurrencyCode();

        CurrencyCodeValidationHelper.validateCode(baseCode, validationResult);
        CurrencyCodeValidationHelper.validateCode(targetCode, validationResult);
        validateCurrencyPair(baseCode, targetCode, validationResult);
        ExchangeRateValidationHelper.validateRate(object.getRate(), validationResult);

        return validationResult;
    }

    private void validateCurrencyPair(String baseCode, String targetCode, ValidationResult validationResult) {
        String normalizedBase = baseCode.toUpperCase().trim();
        String normalizedTarget = targetCode.toUpperCase().trim();

        if (normalizedBase.equals(normalizedTarget))
            validationResult.add(Error.of(BASE_CODE_EQUALS_TARGET_CODE_ERROR));
    }
}