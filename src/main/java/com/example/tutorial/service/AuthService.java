package com.example.tutorial.service;

import com.example.tutorial.common.data.ChangePasswordRequest;
import com.example.tutorial.common.data.PasswordResetRequest;
import com.example.tutorial.security.JwtToken;
import jakarta.servlet.http.HttpServletRequest;

public interface AuthService {
    public void activateEmail(String activationToken);
    public void resendActivationTokenByEmail(String email, HttpServletRequest request);
    public JwtToken changePassword(ChangePasswordRequest request);
    public void requestPasswordResetEmail(String email, HttpServletRequest request);
    public void resetPassword(PasswordResetRequest request);
}
