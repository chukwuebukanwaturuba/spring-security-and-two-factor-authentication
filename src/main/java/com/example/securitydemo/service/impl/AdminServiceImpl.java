package com.example.securitydemo.service.impl;

import com.example.securitydemo.exception.ExistingException;
import com.example.securitydemo.model.dto.request.LoginRequest;
import com.example.securitydemo.model.dto.request.RegistrationRequest;
import com.example.securitydemo.model.dto.response.AuthenticationResponse;
import com.example.securitydemo.model.entity.User;
import com.example.securitydemo.model.enums.Role;
import com.example.securitydemo.repository.UserRepository;
import com.example.securitydemo.security.JwtService;
import com.example.securitydemo.service.AdminService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import static com.example.securitydemo.model.enums.Role.ROLE_USER;

@Service
@RequiredArgsConstructor
public class AdminServiceImpl implements AdminService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;

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
                .build();
        userRepository.save(user);
//        var jwtToken = jwtService.generateToken(user);
        return AuthenticationResponse.builder()
                .token("")
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
        var jwtToken = jwtService.generateToken(user, servletRequest);
        return AuthenticationResponse.builder()
                .token(jwtToken)
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
                .build();
        userRepository.save(user);
        return AuthenticationResponse.builder()
                .token("")
                .build();
    }
}
