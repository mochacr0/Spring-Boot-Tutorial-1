package com.example.tutorial.exception;

import org.springframework.security.authentication.AuthenticationServiceException;
public class AuthMethodNotSupportedException extends AuthenticationServiceException {
    public AuthMethodNotSupportedException(String message) {
        super(message);
    }
}
