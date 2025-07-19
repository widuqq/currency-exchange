package com.widuq.validator;

import com.widuq.dto.CreateCurrencyDto;
import com.widuq.util.CurrencyCodeValidationHelper;

public class CreateCurrencyValidator  implements Validator<CreateCurrencyDto> {
    private static final int MAX_NAME_LENGTH = 128;
    private static final int MAX_SIGN_LENGTH = 8;

    private static final String EMPTY_NAME_ERROR = "Name must not be empty";
    private static final String NAME_LENGTH_ERROR = "Name must be ≤ " + MAX_NAME_LENGTH + " chars";
    private static final String EMPTY_SIGN_ERROR = "Sign must not be empty";
    private static final String SIGN_LENGTH_ERROR = "Sign must be ≤ " + MAX_SIGN_LENGTH + " chars";

    private static final CreateCurrencyValidator INSTANCE = new CreateCurrencyValidator();

    private CreateCurrencyValidator() {}

    @Override
    public ValidationResult isValid(CreateCurrencyDto object) {
        ValidationResult validationResult = new ValidationResult();

        validateName(object.getName(), validationResult);
        CurrencyCodeValidationHelper.validateCode(object.getCode(), validationResult);
        validateSign(object.getSign(), validationResult);

        return validationResult;
    }

    private void validateName(String name, ValidationResult result) {
        if (name == null || name.isBlank()) {
            result.add(Error.of(EMPTY_NAME_ERROR));
            return;
        }

        if (name.length() > MAX_NAME_LENGTH) {
            result.add(Error.of(NAME_LENGTH_ERROR));
        }
    }

    private void validateSign(String sign, ValidationResult result) {
        if (sign == null || sign.isBlank()) {
            result.add(Error.of(EMPTY_SIGN_ERROR));
            return;
        }

        if (sign.length() > MAX_SIGN_LENGTH) {
            result.add(Error.of(SIGN_LENGTH_ERROR));
        }
    }

    public static CreateCurrencyValidator getInstance() {
        return INSTANCE;
    }
}
