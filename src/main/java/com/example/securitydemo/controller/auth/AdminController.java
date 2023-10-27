package com.example.securitydemo.controller.auth;

import com.example.securitydemo.exception.ExistingException;
import com.example.securitydemo.model.dto.request.LoginRequest;
import com.example.securitydemo.model.dto.request.RegistrationRequest;
import com.example.securitydemo.model.dto.request.VerificationRequest;
import com.example.securitydemo.model.dto.response.AuthenticationResponse;
import com.example.securitydemo.service.AdminService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/admin")
public class AdminController {
    private final AdminService adminService;
    @PostMapping("/register")
    public ResponseEntity<?> register(
            @RequestBody RegistrationRequest request) throws ExistingException {
        var response = adminService.register(request);
        if (request.isMfaEnabled()){
            return ResponseEntity.ok(response);
        }
        return ResponseEntity.accepted().build();
    }

    @PostMapping("/authenticate")
    public ResponseEntity<AuthenticationResponse> authenticate(
            @RequestBody LoginRequest request, HttpServletRequest servletRequest){
        return ResponseEntity.ok(adminService.authenticate(request, servletRequest));
    }

    @PostMapping("/registerUser")
    @PreAuthorize("hasAnyAuthority('ROLE_ADMIN')")
    public ResponseEntity<AuthenticationResponse> registerUser(
            @RequestBody RegistrationRequest request) throws ExistingException {
        return ResponseEntity.ok(adminService.registerUser(request));
    }

    @PostMapping("/verify")
    public ResponseEntity<?> verifyCode(
            @RequestBody VerificationRequest verificationRequest
    ){
        return ResponseEntity.ok(adminService.verifyCode(verificationRequest));
    }
}
