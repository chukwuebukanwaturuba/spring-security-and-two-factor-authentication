package com.example.securitydemo.model.dto.response;

import lombok.Builder;
import lombok.Data;
import org.springframework.http.HttpStatus;

@Data
@Builder
public class ApiResponse {
    private String message;
    private String content;
    private HttpStatus status;
}
