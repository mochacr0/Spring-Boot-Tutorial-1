package com.example.tutorial.model;

import com.example.tutorial.common.data.UserCredentials;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import org.springframework.boot.Banner;

import java.util.UUID;

@Entity
@Table(name = ModelConstants.USER_CREDENTIALS_TABLE)
@Data
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class UserCredentialsEntity extends AbstractEntity<UserCredentials>{

    @Column(name = ModelConstants.USER_CREDENTIALS_USER_ID_COLUMN, nullable = false)
    private UUID userId;
    @Column(name = ModelConstants.USER_CREDENTIALS_PASSWORD_COLUMN)
    private String password;
    @Column(name = ModelConstants.USER_CREDENTIALS_ACTIVATE_TOKEN_COLUMN)
    private String activateToken;
    @Column(name = ModelConstants.USER_CREDENTIALS_RESET_PASSWORD_TOKEN_COLUMN)
    private String resetPasswordToken;
    @Column(name = ModelConstants.USER_CREDENTIALS_FAILED_LOGIN_ATTEMPTS_TOKEN_COLUMN)
    private int failedLoginAttempts;
//    @Column(name = ModelConstants.USER_CREDENTIALS_MAX_FAILED_LOGIN_ATTEMPTS_RESET_PASSWORD_TOKEN_COLUMN)
//    private int maxFailedLoginAttempts;
    @Column(name = ModelConstants.USER_CREDENTIALS_IS_ENABLED_COLUMN)
    private boolean isEnabled;
    @Override
    public UserCredentials toData() {
        UserCredentials data = new UserCredentials();
        data.setId(this.getId());
        data.setUserId(this.getId());
        data.setHashedPassword(this.getPassword());
        data.setActivateToken(this.getActivateToken());
        data.setResetPasswordToken(this.getResetPasswordToken());
        data.setFailedLoginAttempts(this.getFailedLoginAttempts());
//        data.setMaxFailedLoginAttempts(this.getMaxFailedLoginAttempts());
        data.setEnabled(this.isEnabled());
        return data;
    }
    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append("UserCredentialsEntity [id=");
        builder.append(this.getId());
        builder.append(", userId=");
        builder.append(this.getUserId());
        builder.append(", password=");
        builder.append(this.getPassword());
        builder.append(", activateToken=");
        builder.append(this.getActivateToken());
        builder.append(", resetPasswordToken=");
        builder.append(this.getResetPasswordToken());
        builder.append(", createdAt=");
        builder.append(this.getCreatedAt());
        builder.append(", updatedAt=");
        builder.append(this.getUpdatedAt());
        builder.append(", failedLoginAttempts=");
        builder.append(this.getFailedLoginAttempts());
        builder.append(", updatedAt=");
        builder.append(this.getUpdatedAt());
        return builder.toString();
    }

}
