package com.example.tutorial.config;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConfigurationProperties(prefix = "security")
@Getter
@Setter
public class SecuritySettingsConfiguration {
    private UserPasswordPolicy passwordPolicy;
    private int maxFailedLoginAttempts;
    private long failedLoginLockExpirationMillis;
    private long activationTokenExpirationMillis;

}
