package com.example.securitydemo.service;

import com.example.securitydemo.exception.ExistingException;
import com.example.securitydemo.model.dto.request.CountryRequest;
import com.example.securitydemo.model.dto.response.ApiResponse;
import com.example.securitydemo.model.entity.Country;

import java.util.List;

public interface CountryService {
    ApiResponse registerCountry(CountryRequest countryRequest) throws ExistingException;
    List<Country> getAllCountries();
    
}
