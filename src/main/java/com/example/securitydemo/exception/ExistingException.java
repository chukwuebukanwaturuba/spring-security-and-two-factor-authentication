package com.example.securitydemo.exception;

import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class ExistingException extends Exception{
    private String message;


}
