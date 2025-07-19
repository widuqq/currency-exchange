package com.widuq.dto;

import lombok.Builder;
import lombok.Value;

@Value
@Builder
public class CreateExchangeRateDto {
    String baseCurrencyCode;
    String targetCurrencyCode;
    String rate;
}
