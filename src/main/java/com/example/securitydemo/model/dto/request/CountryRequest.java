package com.example.securitydemo.model.dto.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class CountryRequest {
    private Long id;
    private String countryName;
    private String countryCode;
}
