package com.widuq.dto;

import lombok.Builder;
import lombok.Value;

import java.math.BigDecimal;

@Value
@Builder
public class ExchangeResponseDto {
    ReadCurrencyDto baseCurrency;
    ReadCurrencyDto targetCurrency;
    BigDecimal rate;
    BigDecimal amount;
    BigDecimal convertedAmount;
}
