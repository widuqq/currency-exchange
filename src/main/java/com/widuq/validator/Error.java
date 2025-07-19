package com.widuq.validator;

import lombok.Value;

@Value(staticConstructor = "of")
public class Error {
    String message;
}
