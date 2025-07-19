package com.widuq.validator;

import com.widuq.dto.UpdateExchangeRateDto;
import com.widuq.util.ExchangeRateValidationHelper;

public class UpdateExchangeRateValidator implements Validator<UpdateExchangeRateDto> {
    private static final UpdateExchangeRateValidator INSTANCE = new UpdateExchangeRateValidator();

    private UpdateExchangeRateValidator() {}

    public static UpdateExchangeRateValidator getInstance() {
        return INSTANCE;
    }

    @Override
    public ValidationResult isValid(UpdateExchangeRateDto object) {
        var validationResult = new ValidationResult();

        ExchangeRateValidationHelper.validateRate(object.getRate(), validationResult);

        return validationResult;
    }
}
