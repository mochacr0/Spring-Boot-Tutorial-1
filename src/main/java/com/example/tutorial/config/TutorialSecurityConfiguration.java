package com.example.tutorial.config;

import com.example.tutorial.security.*;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Lazy;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.DefaultAuthenticationEventPublisher;
import org.springframework.security.config.annotation.ObjectPostProcessor;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.client.web.AuthorizationRequestRepository;
import org.springframework.security.oauth2.core.endpoint.OAuth2AuthorizationRequest;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static com.example.tutorial.controller.ControllerConstants.*;

@Configuration
@EnableWebSecurity
public class TutorialSecurityConfiguration {

    @Autowired
    @Lazy
    private AuthenticationManager authenticationManager;

    @Autowired
    private ObjectMapper mapper;

    @Autowired
    private RestAuthenticationProvider restAuthenticationProvider;

    @Autowired
    private JwtAuthenticationProvider jwtAuthenticationProvider;

    @Autowired
    @Qualifier(value = "RestAuthenticationSuccessHandler")
    private AuthenticationSuccessHandler restAuthenticationSuccessHandler;

    @Autowired
    @Qualifier(value = "RestAuthenticationFailureHandler")
    private AuthenticationFailureHandler restAuthenticationFailureHandler;

    @Autowired
    @Qualifier(value = "OAuth2AuthenticationSuccessHandler")
    private AuthenticationSuccessHandler oauth2AuthenticationSuccessHandler;

    @Autowired
    @Qualifier(value = "OAuth2AuthenticationFailureHandler")
    private AuthenticationFailureHandler oauth2AuthenticationFailureHandler;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private JwtTokenExtractor jwtTokenExtractor;

    @Autowired
    private AuthorizationRequestRepository<OAuth2AuthorizationRequest> authorizationRequestRepository;

    private static final List<String> NON_TOKEN_BASED_AUTH_ENTRY_ENDPOINTS = new ArrayList<>(Arrays.asList(
            AUTH_LOGIN_ENDPOINT,
            AUTH_REQUEST_PASSWORD_RESET_EMAIL_ROUTE,
            AUTH_ACTIVATE_EMAIL_ROUTE,
            AUTH_RESEND_ACTIVATION_TOKEN_ROUTE,
            AUTH_RESET_PASSWORD_ROUTE,
//            AUTH_CHANGE_PASSWORD_ROUTE,
            "/swagger-ui.html", "/swagger-ui/**", "/v3/api-docs/**",
            "/users/**"));

    @Bean
    public AuthenticationManager authenticationManager(ObjectPostProcessor<Object> objectPostProcessor) throws Exception {
        DefaultAuthenticationEventPublisher eventPublisher = objectPostProcessor
                .postProcess(new DefaultAuthenticationEventPublisher());
        var builder = new AuthenticationManagerBuilder(objectPostProcessor);
        builder.authenticationEventPublisher(eventPublisher);
        builder.authenticationProvider(this.restAuthenticationProvider);
        builder.authenticationProvider(this.jwtAuthenticationProvider);
        return builder.build();
    }

    public RestLoginProcessingFilter buildRestLoginProcessingFilter() {
        RestLoginProcessingFilter filter = new RestLoginProcessingFilter(
                AUTH_LOGIN_ENDPOINT,
                restAuthenticationSuccessHandler,
                restAuthenticationFailureHandler,
                mapper);
        filter.setAuthenticationManager(this.authenticationManager);
        return filter;
    }

    public JwtAuthenticationProcessingFilter buildJwtAuthenticationProcessingFilter() {
        String processingPath = "/**";
        List<String> skipPaths = new ArrayList<>();
        skipPaths.addAll(NON_TOKEN_BASED_AUTH_ENTRY_ENDPOINTS);
        JwtAuthenticationProcessingFilter filter = new JwtAuthenticationProcessingFilter(new SkipPathRequestMatcher(skipPaths, processingPath), restAuthenticationFailureHandler, jwtTokenExtractor);
        filter.setAuthenticationManager(this.authenticationManager);
        return filter;
    }

    @Bean
    SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
            .cors()
                .and()
            .sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                .and()
            .csrf().disable()
            .authorizeHttpRequests()
                .requestMatchers("/swagger-ui.html", "/swagger-ui/**", "/v3/api-docs/**").permitAll()
//                .requestMatchers("/users").permitAll()
//                .requestMatchers("/oauth2").permitAll()
//                .requestMatchers("/user").permitAll()
                .requestMatchers(AUTH_LOGIN_ENDPOINT).permitAll()
                .anyRequest().permitAll()
                .and()
            .addFilterBefore(buildRestLoginProcessingFilter(), UsernamePasswordAuthenticationFilter.class)
            .addFilterBefore(buildJwtAuthenticationProcessingFilter(), UsernamePasswordAuthenticationFilter.class)
            .oauth2Login()
                .authorizationEndpoint()
                    .authorizationRequestRepository(this.authorizationRequestRepository)
            .and()
                .successHandler(this.oauth2AuthenticationSuccessHandler)
                .failureHandler(this.oauth2AuthenticationFailureHandler);
        return http.build();
    }

}
