package com.example.securitydemo.service.impl;

import com.example.securitydemo.exception.ExistingException;
import com.example.securitydemo.model.dto.request.LoginRequest;
import com.example.securitydemo.model.dto.request.RegistrationRequest;
import com.example.securitydemo.model.dto.request.VerificationRequest;
import com.example.securitydemo.model.dto.response.AuthenticationResponse;
import com.example.securitydemo.model.entity.User;
import com.example.securitydemo.model.enums.Role;
import com.example.securitydemo.repository.UserRepository;
import com.example.securitydemo.security.JwtService;
import com.example.securitydemo.service.AdminService;
import com.example.securitydemo.tfa.TwoFactorAuthentication;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.persistence.EntityNotFoundException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.io.IOException;

import static com.example.securitydemo.model.enums.Role.ROLE_USER;

@Service
@RequiredArgsConstructor
public class AdminServiceImpl implements AdminService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;
    private final TwoFactorAuthentication twoFactorAuthentication;

    @Override
    public AuthenticationResponse register(RegistrationRequest request) throws ExistingException{
        String email = request.getEmail();

        if (userRepository.existsByEmail(email)){
            throw new ExistingException("Email has already be registered as Admin");
        }
        var user = User.builder()
                .name(request.getName())
                .email(request.getEmail())
                .password(passwordEncoder.encode(request.getPassword()))
                .role(Role.ROLE_ADMIN)
                .mfaEnabled(request.isMfaEnabled())
                .build();
        if (request.isMfaEnabled()){
            user.setSecret(twoFactorAuthentication.generateNewSecret());
        }
        userRepository.save(user);
        return AuthenticationResponse.builder()
                .token("")
                .secretImageUri(twoFactorAuthentication.generateQrCodeImageUri(user.getSecret()))
                .mfaEnable(user.isMfaEnabled())
                .build();
    }

    @Override
    public AuthenticationResponse authenticate(LoginRequest request, HttpServletRequest servletRequest) {
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        request.getEmail(),
                        request.getPassword()
                )
        );
        var user = userRepository.findByEmail(request.getEmail())
                .orElseThrow();
        if (user.isMfaEnabled()){
            return AuthenticationResponse.builder()
                    .token("")
                    .mfaEnable(true)
                    .build();

        }
        var jwtToken = jwtService.generateToken(user);
        return AuthenticationResponse.builder()
                .token(jwtToken)
                .mfaEnable(false)
                .build();
    }


    public AuthenticationResponse registerUser(RegistrationRequest request) throws ExistingException{
        String email = request.getEmail();
        if (userRepository.existsByEmail(email)){
            throw new ExistingException("User with email " + email +" already exist");
        }
        var user = User.builder()
                .name(request.getName())
                .email(request.getEmail())
                .password(passwordEncoder.encode(request.getPassword()))
                .role(ROLE_USER)
         //       .mfaEnabled(request.isMfaEnabled())
                .build();
        userRepository.save(user);
        return AuthenticationResponse.builder()
                .token("")
                .build();
    }

    @Override
    public AuthenticationResponse verifyCode(VerificationRequest verificationRequest) {
        User user = userRepository
                .findByEmail(verificationRequest.getEmail())
                .orElseThrow(()-> new EntityNotFoundException(
                        String.format("No user found with %S", verificationRequest.getEmail())));
        if (twoFactorAuthentication.isOtpNotValid(user.getSecret(), verificationRequest.getCode())){
            throw new BadCredentialsException("Code is not correct");
        }
        var jwtToken = jwtService.generateToken(user);
        return AuthenticationResponse.builder()
                .token(jwtToken)
                .mfaEnable(user.isMfaEnabled())
                .build();
    }

    public void refreshToken(
            HttpServletRequest request,
            HttpServletResponse response
    ) throws IOException{
        final String authHeader = request.getHeader(HttpHeaders.AUTHORIZATION);
        final String refreshToken;
        final String userEmail;

        if (authHeader == null || !authHeader.startsWith("Bearer ")){
            return;
        }
        refreshToken = authHeader.substring(7);
        userEmail = jwtService.extractUsername(refreshToken);
        if (userEmail != null) {
            var user = this.userRepository.findByEmail(userEmail)
                    .orElseThrow();
            if (jwtService.isTokenValid(refreshToken, user)) {
                var accessToken = jwtService.generateToken(user);
                var authResponse = AuthenticationResponse.builder()
                        .token(accessToken)
                        .refreshToken(refreshToken)
                        .mfaEnable(false)
                        .build();
                new ObjectMapper().writeValue(response.getOutputStream(), authResponse);

            }
        }
    }
}
