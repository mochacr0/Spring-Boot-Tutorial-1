package com.example.tutorial.security;

import com.example.tutorial.exception.TutorialErrorHandler;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component(value = "RestAuthenticationFailureHandler")
public class RestAuthenticationFailureHandler implements AuthenticationFailureHandler {
    @Autowired
    private TutorialErrorHandler errorHandler;

    @Override
    public void onAuthenticationFailure(HttpServletRequest request, HttpServletResponse response, AuthenticationException exception) throws IOException, ServletException {
        this.errorHandler.handle(exception, response);
    }
}
