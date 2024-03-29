package com.example.tutorial.common.data;

import lombok.Data;

@Data
public class PasswordResetRequest {
    private String passwordResetToken;
    private String newPassword;
    private String confirmPassword;
}
