package com.widuq.exception;

import com.widuq.validator.Error;
import lombok.Getter;
import java.util.List;

@Getter
public class ValidationException extends RuntimeException {
    private static final String ERROR_MESSAGE = "Validation failed";
    private final List<Error> errors;

    public ValidationException(List<Error> errors) {
        super(ERROR_MESSAGE);
        this.errors = errors;
    }
}
