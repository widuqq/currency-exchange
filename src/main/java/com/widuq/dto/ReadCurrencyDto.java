package com.widuq.dto;

import lombok.Builder;
import lombok.Value;

@Value
@Builder
public class ReadCurrencyDto {
    Integer id;
    String name;
    String code;
    String sign;
}
