package com.example.tutorial.common.data;

import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class RegisterUserRequest {
    private String name;
    private String email;
    private String password;
    private String confirmPassword;
    public void setMatchedPasswords(String password) {
        this.password = password;
        this.confirmPassword = password;
    }
}
