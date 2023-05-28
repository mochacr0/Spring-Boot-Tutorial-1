package com.example.tutorial.config;

import com.example.tutorial.common.validator.CommonValidator;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

@Configuration
public class TutorialApplicationConfiguration {
    @Bean
    public PasswordEncoder buildPasswordEncoder() {
        return new BCryptPasswordEncoder();
    }
    @Bean
    public CommonValidator buildCommonDataValidator(@Autowired SecuritySettingsConfiguration securitySettings) {
        return new CommonValidator(securitySettings);
    }

//    @Bean
//    public ObjectMapper buildObjectMapper() {
//        return new ObjectMapper();
//    }
}
