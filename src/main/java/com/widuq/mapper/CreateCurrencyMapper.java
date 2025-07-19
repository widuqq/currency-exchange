package com.widuq.mapper;

import com.widuq.dto.CreateCurrencyDto;
import com.widuq.entity.Currency;

public class CreateCurrencyMapper implements Mapper<CreateCurrencyDto, Currency> {
    private static final CreateCurrencyMapper INSTANCE = new CreateCurrencyMapper();

    private CreateCurrencyMapper() {}

    public static CreateCurrencyMapper getInstance() {
        return INSTANCE;
    }

    @Override
    public Currency mapFrom(CreateCurrencyDto object) {
        return Currency.builder()
                .code(object.getCode())
                .fullName(object.getName())
                .sign(object.getSign())
                .build();
    }
}
