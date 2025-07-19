package com.widuq.mapper;

import com.widuq.dto.ReadCurrencyDto;
import com.widuq.entity.Currency;

public class ReadCurrencyMapper implements Mapper<Currency, ReadCurrencyDto> {
    private static final ReadCurrencyMapper INSTANCE = new ReadCurrencyMapper();

    private ReadCurrencyMapper() {}

    public static ReadCurrencyMapper getInstance() {
        return INSTANCE;
    }

    @Override
    public ReadCurrencyDto mapFrom(Currency object) {
        return ReadCurrencyDto.builder()
                .id(object.getId())
                .code(object.getCode())
                .name(object.getFullName())
                .sign(object.getSign())
                .build();
    }
}
