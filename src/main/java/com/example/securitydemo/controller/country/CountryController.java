package com.example.securitydemo.controller.country;


import com.example.securitydemo.exception.ExistingException;
import com.example.securitydemo.model.dto.request.CountryRequest;
import com.example.securitydemo.model.dto.response.ApiResponse;
import com.example.securitydemo.model.entity.Country;
import com.example.securitydemo.service.CountryService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;


@RestController
@RequiredArgsConstructor
@RequestMapping("/country")
public class CountryController {
    private final CountryService countryService;

    @PreAuthorize("hasAnyAuthority('ROLE_ADMIN')")
    @PostMapping("/register")
    public ApiResponse registerCountry(@RequestBody CountryRequest countryRequest) throws ExistingException {
       return countryService.registerCountry(countryRequest);
    }

    @PreAuthorize("hasAnyAuthority('ROLE_ADMIN')")
    @GetMapping("/all")
    public ResponseEntity<List<Country>> getAllCountries(){
        List<Country> countries = countryService.getAllCountries();
        return new ResponseEntity<>(countries, HttpStatus.OK);
    }

}
