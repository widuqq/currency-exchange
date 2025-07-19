package com.widuq.mapper;

import com.widuq.dto.ReadExchangeRateDto;
import com.widuq.entity.ExchangeRate;

import java.math.RoundingMode;

public class ReadExchangeRateMapper implements Mapper<ExchangeRate, ReadExchangeRateDto> {
    private static final ReadExchangeRateMapper INSTANCE = new ReadExchangeRateMapper();
    private final ReadCurrencyMapper readCurrencyMapper = ReadCurrencyMapper.getInstance();

    private ReadExchangeRateMapper() {}

    public static ReadExchangeRateMapper getInstance() {
        return INSTANCE;
    }

    @Override
    public ReadExchangeRateDto mapFrom(ExchangeRate object) {
        return ReadExchangeRateDto.builder()
                .id(object.getId())
                .baseCurrency(readCurrencyMapper.mapFrom(object.getBaseCurrency()))
                .targetCurrency(readCurrencyMapper.mapFrom(object.getTargetCurrency()))
                .rate(object.getRate().setScale(2, RoundingMode.HALF_UP))
                .build();
    }
}
