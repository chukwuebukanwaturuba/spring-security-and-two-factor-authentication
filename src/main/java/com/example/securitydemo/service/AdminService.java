package com.example.securitydemo.service;

import com.example.securitydemo.exception.ExistingException;
import com.example.securitydemo.model.dto.request.LoginRequest;
import com.example.securitydemo.model.dto.request.RegistrationRequest;
import com.example.securitydemo.model.dto.request.VerificationRequest;
import com.example.securitydemo.model.dto.response.AuthenticationResponse;
import jakarta.servlet.http.HttpServletRequest;

public interface AdminService {

    AuthenticationResponse register(RegistrationRequest request) throws ExistingException;
    AuthenticationResponse authenticate(LoginRequest request, HttpServletRequest servletRequest);
    AuthenticationResponse registerUser(RegistrationRequest request) throws ExistingException;

    AuthenticationResponse verifyCode(VerificationRequest verificationRequest);
}
