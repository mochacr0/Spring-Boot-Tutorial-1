package com.example.tutorial.service;

import io.micrometer.common.util.StringUtils;
import jakarta.servlet.http.HttpServletRequest;

public interface AuthService {
    public void activateEmail(String activationToken);

    public void resendActivationTokenByEmail(String email, HttpServletRequest request);
}
