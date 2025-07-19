package com.widuq.dto;

import lombok.Builder;
import lombok.Value;

@Value
@Builder
public class UpdateExchangeRateDto {
    String rate;
}
