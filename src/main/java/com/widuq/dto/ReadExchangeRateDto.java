package com.widuq.dto;

import lombok.Builder;
import lombok.Value;

import java.math.BigDecimal;

@Value
@Builder
public class ReadExchangeRateDto {
    Integer id;
    ReadCurrencyDto baseCurrency;
    ReadCurrencyDto targetCurrency;
    BigDecimal rate;
}
