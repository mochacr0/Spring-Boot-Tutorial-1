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
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

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
    private PasswordEncoder passwordEncoder;

    @Autowired
    private JwtTokenExtractor jwtTokenExtractor;

    public static final String HTTP_LOGIN_ENDPOINT = "/auth/login";

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
                HTTP_LOGIN_ENDPOINT,
                restAuthenticationSuccessHandler,
                restAuthenticationFailureHandler,
                mapper);
        filter.setAuthenticationManager(this.authenticationManager);
        return filter;
    }

    public JwtAuthenticationProcessingFilter buildJwtAuthenticationProcessingFilter() {
        JwtAuthenticationProcessingFilter filter = new JwtAuthenticationProcessingFilter("/users", restAuthenticationFailureHandler, jwtTokenExtractor);
        filter.setAuthenticationManager(this.authenticationManager);
        return filter;
    }

    @Bean
    SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        return http
                .cors()
                    .and()
                .sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                    .and()
                .csrf().disable()
                .authorizeHttpRequests()
                    .requestMatchers(HTTP_LOGIN_ENDPOINT).permitAll()
                    .requestMatchers("/swagger-ui.html", "/swagger-ui/**", "/v3/api-docs/**").permitAll()
                    .requestMatchers("/**").permitAll()
                    .and()
                .addFilterBefore(buildRestLoginProcessingFilter(), UsernamePasswordAuthenticationFilter.class)
                //.addFilterBefore(buildJwtAuthenticationProcessingFilter(), UsernamePasswordAuthenticationFilter.class)
                .build();
    }

}
