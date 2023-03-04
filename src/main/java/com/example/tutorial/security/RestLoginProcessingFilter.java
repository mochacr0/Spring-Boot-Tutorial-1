package com.example.tutorial.security;

import com.example.tutorial.exception.AuthMethodNotSupportedException;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationServiceException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.AbstractAuthenticationProcessingFilter;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;

import java.io.IOException;

public class RestLoginProcessingFilter extends AbstractAuthenticationProcessingFilter {
    private final ObjectMapper mapper;
    private final AuthenticationSuccessHandler successHandler;
    private final AuthenticationFailureHandler failureHandler;

    public RestLoginProcessingFilter(String defaultFilterProcessUrl, AuthenticationSuccessHandler successHandler,
                                     AuthenticationFailureHandler failureHandler, ObjectMapper mapper) {
        super(defaultFilterProcessUrl);
        this.successHandler = successHandler;
        this.failureHandler = failureHandler;
        this.mapper = mapper;
    }

    @Override
    public Authentication attemptAuthentication(HttpServletRequest request, HttpServletResponse response) throws AuthenticationException, IOException, ServletException {
        if (!request.getMethod().equals(HttpMethod.POST.toString())) {
            throw new AuthMethodNotSupportedException("Authentication method not supported: " + request.getMethod().toString());
        }
        LoginRequest loginRequest;
        try {
            loginRequest = mapper.readValue(request.getReader(), LoginRequest.class);
        }
        catch (Exception e) {
            throw new AuthenticationServiceException("Invalid username or password");
        }
        if (loginRequest.getUsername().isEmpty() || loginRequest.getPassword().isEmpty()) {
            throw new AuthenticationServiceException("Username or password not provided");
        }
        //setup token from request
        UsernamePasswordAuthenticationToken token = new UsernamePasswordAuthenticationToken(loginRequest.getUsername(), loginRequest.getPassword());
        return this.getAuthenticationManager().authenticate(token);
    }

    @Override
    protected void successfulAuthentication(HttpServletRequest request, HttpServletResponse response, FilterChain chain, Authentication authResult) throws IOException, ServletException {
        this.successHandler.onAuthenticationSuccess(request, response, authResult);
    }

    @Override
    protected void unsuccessfulAuthentication(HttpServletRequest request, HttpServletResponse response, AuthenticationException failed) throws IOException, ServletException {
        SecurityContextHolder.clearContext();
        this.failureHandler.onAuthenticationFailure(request, response, failed);
    }
}
