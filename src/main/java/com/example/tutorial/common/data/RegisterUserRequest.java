package com.example.tutorial.common.data;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class RegisterUserRequest {
    @Schema(title = "name", description = "User name", example = "user00", requiredMode = Schema.RequiredMode.REQUIRED)
    private String name;
    @Schema(title = "email", description = "User email", example = "nthai2001cr@gmail.com", requiredMode = Schema.RequiredMode.REQUIRED)
    private String email;
    @Schema(title = "password", description = "User password", example = "String", requiredMode = Schema.RequiredMode.REQUIRED)
    private String password;
    @Schema(title = "confirmPassword", description = "Confirm password, which must be the same as password", example = "String", requiredMode = Schema.RequiredMode.REQUIRED)
    private String confirmPassword;
    public void setMatchedPasswords(String password) {
        this.password = password;
        this.confirmPassword = password;
    }
}
