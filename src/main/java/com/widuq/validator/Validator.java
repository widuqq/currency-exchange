package com.widuq.validator;

@FunctionalInterface
public interface Validator<T> {
    ValidationResult isValid(T object);
}
