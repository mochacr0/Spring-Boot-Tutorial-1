package com.example.tutorial.security.oauth2;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.oauth2.client.web.AuthorizationRequestRepository;
import org.springframework.security.oauth2.core.endpoint.OAuth2AuthorizationRequest;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationFailureHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

@Component(value = "OAuth2AuthenticationFailureHandler")
public class OAuth2AuthenticationFailureHandler extends SimpleUrlAuthenticationFailureHandler {
    @Autowired
    private HttpCookieOAuth2AuthorizationRequestRepository authorizationRequestRepository;

    public void onAuthenticationFailure(HttpServletRequest request, HttpServletResponse response, AuthenticationException exception) throws IOException, ServletException {
//        this.authorizationRequestRepository.removeAuthorizationRequestCookie(request, response);
        String errorPath = "/oauth2?error=" + URLEncoder.encode(exception.getMessage(), StandardCharsets.UTF_8);
        this.getRedirectStrategy().sendRedirect(request, response, errorPath);
    }
}
