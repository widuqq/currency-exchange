package com.widuq.validator;

import com.widuq.dto.ExchangeRequestDto;
import com.widuq.util.CurrencyCodeValidationHelper;

import java.math.BigDecimal;

public class CreateExchangeValidator implements Validator<ExchangeRequestDto> {
    private static final String EMPTY_AMOUNT_ERROR = "Amount must not be empty";
    private static final String FORMAT_AMOUNT_ERROR = "Invalid amount format";
    private static final String NON_POSITIVE_AMOUNT_ERROR = "Amount must be positive";

    private static final CreateExchangeValidator INSTANCE = new CreateExchangeValidator();

    private CreateExchangeValidator() {}

    @Override
    public ValidationResult isValid(ExchangeRequestDto object) {
        var validationResult = new ValidationResult();

        CurrencyCodeValidationHelper.validateCode(object.getBaseCurrencyCode(), validationResult);
        CurrencyCodeValidationHelper.validateCode(object.getTargetCurrencyCode(), validationResult);
        validateAmount(object.getAmount(), validationResult);

        return validationResult;
    }

    private void validateAmount(String amountStr, ValidationResult validationResult) {
        if (amountStr == null || amountStr.isBlank()) {
            validationResult.add(Error.of(EMPTY_AMOUNT_ERROR));
            return;
        }

        try {
            BigDecimal amount = new BigDecimal(amountStr);
            if (amount.compareTo(BigDecimal.ZERO) <= 0) {
                validationResult.add(Error.of(NON_POSITIVE_AMOUNT_ERROR));
            }
        } catch (NumberFormatException e) {
            validationResult.add(Error.of(FORMAT_AMOUNT_ERROR));
        }
    }

    public static CreateExchangeValidator getInstance() {
        return INSTANCE;
    }
}
