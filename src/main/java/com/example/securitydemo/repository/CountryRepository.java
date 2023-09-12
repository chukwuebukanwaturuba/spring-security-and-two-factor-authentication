package com.example.securitydemo.repository;

import com.example.securitydemo.model.entity.Country;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface CountryRepository extends JpaRepository<Country, Long> {
    //Optional<Country> findByCountryCode(String countryCode);
    boolean existsByCountryCode(String countryCode);
}
