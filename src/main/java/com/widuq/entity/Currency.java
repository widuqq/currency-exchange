package com.widuq.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@Builder
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@AllArgsConstructor
public class Currency {
    @EqualsAndHashCode.Include
    private Integer id;
    private String code;
    private String fullName;
    private String sign;
}
