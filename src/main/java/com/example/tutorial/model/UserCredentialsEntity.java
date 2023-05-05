package com.example.tutorial.model;

import com.example.tutorial.common.data.UserCredentials;
import com.example.tutorial.common.utils.JsonNodeStringConverter;
import com.fasterxml.jackson.databind.JsonNode;
import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.Type;

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
    @Column(name = ModelConstants.USER_CREDENTIALS_ACTIVATION_TOKEN_COLUMN, unique = true)
    private String activationToken;
    @Column(name = ModelConstants.USER_CREDENTIALS_ACTIVATION_TOKEN_EXPIRATION_MILLIS)
    private long activationTokenExpirationMillis;
    @Column(name = ModelConstants.USER_CREDENTIALS_RESET_PASSWORD_TOKEN_COLUMN, unique = true)
    private String resetPasswordToken;
    @Column(name = ModelConstants.USER_CREDENTIALS_FAILED_LOGIN_ATTEMPTS_TOKEN_COLUMN)
    private int failedLoginAttempts;
    @Column(name = ModelConstants.USER_CREDENTIALS_FAILED_LOGIN_LOCK_EXPIRATION_MILLIS)
    private long failedLoginLockExpirationMillis;
    @Column(name = ModelConstants.USER_CREDENTIALS_IS_VERIFIED_COLUMN)
    private boolean isVerified;
    @Column(name = ModelConstants.USER_CREDENTIALS_IS_ENABLED_COLUMN)
    private boolean isEnabled;
    @Column(name = ModelConstants.USER_CREDENTIALS_ADDITIONAL_INFO)
    @Convert(converter = JsonNodeStringConverter.class)
    private JsonNode additionalInfo;
    @Override
    public UserCredentials toData() {
        UserCredentials data = new UserCredentials();
        data.setId(this.getId());
        data.setUserId(this.getUserId());
        data.setHashedPassword(this.getPassword());
        data.setActivationToken(this.getActivationToken());
        data.setActivationTokenExpirationMillis(this.getActivationTokenExpirationMillis());
        data.setResetPasswordToken(this.getResetPasswordToken());
        data.setFailedLoginAttempts(this.getFailedLoginAttempts());
        data.setFailedLoginLockExpirationMillis(this.getFailedLoginLockExpirationMillis());
        data.setVerified(this.isVerified());
        data.setEnabled(this.isEnabled());
        data.setCreatedAt(this.getCreatedAt());
        data.setUpdatedAt(this.getUpdatedAt());
        data.setAdditionalInfo(this.getAdditionalInfo());
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
        builder.append(this.getActivationToken());
        builder.append(", activationTokenExpirationMillis=");
        builder.append(this.getActivationTokenExpirationMillis());
        builder.append(", resetPasswordToken=");
        builder.append(this.getResetPasswordToken());
        builder.append(this.getResetPasswordToken());
        builder.append(", maxFailedLoginAttempts=");
        builder.append(this.getFailedLoginAttempts());
        builder.append(", failedLoginLockExpirationMillis=");
        builder.append(this.getFailedLoginLockExpirationMillis());
        builder.append(", isVerified=");
        builder.append(this.isVerified());
        builder.append(", isEnabled=");
        builder.append(this.isEnabled());
        builder.append(", createdAt=");
        builder.append(this.getCreatedAt());
        builder.append(", updatedAt=");
        builder.append(this.getUpdatedAt());
        return builder.toString();
    }

}
