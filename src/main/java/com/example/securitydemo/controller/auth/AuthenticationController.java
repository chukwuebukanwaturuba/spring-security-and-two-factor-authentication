package com.example.securitydemo.controller.auth;

import com.example.securitydemo.model.dto.request.LoginRequest;
import com.example.securitydemo.model.dto.request.RegistrationRequest;
import com.example.securitydemo.model.dto.response.AuthenticationResponse;
import com.example.securitydemo.service.impl.AuthenticationServiceImpl;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthenticationController {
    private final AuthenticationServiceImpl authenticationServiceImpl;

//    @PostMapping("/register")
//    public ResponseEntity<AuthenticationResponse> register(
//            @RequestBody RegistrationRequest request){
//        return ResponseEntity.ok(authenticationServiceImpl.registerUser(request));
//    }

    @PostMapping("/authenticate")
    public ResponseEntity<AuthenticationResponse> authenticate(
            @RequestBody LoginRequest request, HttpServletRequest servletRequest){
        return ResponseEntity.ok(authenticationServiceImpl.authenticate(request, servletRequest));
    }
}
// List of countries