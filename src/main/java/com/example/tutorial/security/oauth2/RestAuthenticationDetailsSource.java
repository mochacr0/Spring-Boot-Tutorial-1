package com.example.tutorial.security.oauth2;

import com.example.tutorial.security.RestAuthenticationDetails;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.security.authentication.AuthenticationDetailsSource;

public class RestAuthenticationDetailsSource implements AuthenticationDetailsSource<HttpServletRequest, RestAuthenticationDetails> {
    @Override
    public RestAuthenticationDetails buildDetails(HttpServletRequest context) {
        return new RestAuthenticationDetails(context);
    }
}
