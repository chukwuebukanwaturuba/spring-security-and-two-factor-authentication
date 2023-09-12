package com.example.securitydemo.service;

import com.example.securitydemo.model.dto.request.LoginRequest;
import com.example.securitydemo.model.dto.request.RegistrationRequest;
import com.example.securitydemo.model.dto.response.AuthenticationResponse;
import jakarta.servlet.http.HttpServletRequest;

public interface AuthenticationService {

    //AuthenticationResponse register(RegistrationRequest request);
    AuthenticationResponse authenticate(LoginRequest request, HttpServletRequest servletRequest);
}
