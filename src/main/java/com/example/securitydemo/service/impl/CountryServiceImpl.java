package com.example.securitydemo.service.impl;

import com.example.securitydemo.exception.ExistingException;
import com.example.securitydemo.model.dto.request.CountryRequest;
import com.example.securitydemo.model.dto.response.ApiResponse;
import com.example.securitydemo.model.entity.Country;
import com.example.securitydemo.repository.CountryRepository;
import com.example.securitydemo.service.CountryService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor

public class CountryServiceImpl implements CountryService {
    private final CountryRepository countryRepository;
    @Override
    public ApiResponse registerCountry(CountryRequest countryRequest) throws ExistingException {
        String countryCode = countryRequest.getCountryCode();
        if (countryRepository.existsByCountryCode(countryCode)){
            throw new ExistingException("Country with code " + countryCode + " already exist.");
        }

        Country country = new Country();
        country.setCountryCode(countryRequest.getCountryCode());
        country.setCountryName(countryRequest.getCountryName());
        countryRepository.save(country);
       return ApiResponse.builder()
               .message("country saved successfully")
               .status(HttpStatus.OK)
               .build();
    }

    @Override
    public List<Country> getAllCountries() {
        return countryRepository.findAll();
    }

}
