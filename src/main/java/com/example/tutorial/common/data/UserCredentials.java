package com.example.tutorial.common.data;

import com.example.tutorial.model.ModelConstants;
import com.example.tutorial.model.ToEntity;
import com.example.tutorial.model.UserCredentialsEntity;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.springframework.beans.factory.annotation.Autowired;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
@Schema
@EqualsAndHashCode(callSuper = true)
public class UserCredentials extends AbstractData implements ToEntity<UserCredentialsEntity> {
    private final ObjectMapper objectMapper = new ObjectMapper();
    @Schema(title = "User ID", description = "User ID", defaultValue = "123e4567-e89b-12d3-a456-426614174000")
    private UUID userId;
    @Schema(title = "Raw password", description = "Raw password")
    private String rawPassword;
    @Schema(title = "Hashed password", description = "Raw password hashed using BCryptPasswordEncoder")
    private String hashedPassword;
    @Schema(title = "Activation token", description = "Send this token to activate email")
    private String activationToken;
    @Schema(title = "Activation token expiry time", description = "After this time, the email activation token will be invalid")
    private long activationTokenExpirationMillis;
    @Schema(title = "Reset password token", description = "Send this token to reset password")
    private String resetPasswordToken;
    @Schema(title = "Failed login history", description = "The number of times that user's login has failed")
    private JsonNode failedLoginHistory;
//    @Schema(title = "Failed login lock expiration", description = "The number of milliseconds a user will be locked after exceeding the maximum number of failed login attempts")
//    private long failedLoginLockExpirationMillis;
//    @Schema(title = "First failed login attempts in milliseconds", description = "The time in milliseconds when a user first failed to login")
//    private long firstFailedLoginAttemptsMillis;
    @Schema(title = "Is email verified", description = "A boolean value indicates whether the user's email address has been verified", defaultValue = "false")
    private boolean isVerified;
//    @Schema(title = "Is account enabled", description = "A boolean value indicates whether or not the user account is enabled", defaultValue = "false")
//    private boolean isEnabled;
    @Schema(title = "Additional Information", description = "A JSON value contains all additional information")
    private JsonNode additionalInfo;

    public UserCredentials() {}

    public UUID getId() {
        return this.id;
    }
    public LocalDateTime getCreatedAt() {
        return this.createdAt;
    }
    public LocalDateTime getUpdatedAt() {
        return this.updatedAt;
    }
    @Override
    public UserCredentialsEntity toEntity() {
        UserCredentialsEntity entity = new UserCredentialsEntity();
        entity.setId(this.getId());
        entity.setUserId(this.getUserId());
        entity.setPassword(this.getHashedPassword());
        entity.setActivationToken(this.getActivationToken());
        entity.setActivationTokenExpirationMillis(this.getActivationTokenExpirationMillis());
        entity.setResetPasswordToken(this.getResetPasswordToken());
        entity.setFailedLoginHistory(this.getFailedLoginHistory());
//        entity.setFailedLoginLockExpirationMillis(this.getFailedLoginLockExpirationMillis());
//        entity.setFirstFailedLoginAttemptsMillis(this.getFirstFailedLoginAttemptsMillis());
        entity.setVerified(this.isVerified());
//        entity.setEnabled(this.isEnabled());
        entity.setAdditionalInfo(this.getAdditionalInfo());
        return entity;
    }

    public JsonNode getFailedLoginHistory() {
        if (this.failedLoginHistory == null) {
            this.failedLoginHistory = objectMapper.createObjectNode();
        }
        return this.failedLoginHistory;
    }

    public JsonNode getClientIpAddressNode(String clientIpAddress) {
        JsonNode clientIpAddressNode = this.getFailedLoginHistory().get(clientIpAddress);
        return clientIpAddressNode == null ? objectMapper.createObjectNode() : clientIpAddressNode;
    }

    public void setClientIpAddressNode(String clientIpAddress, JsonNode value) {
        if (this.failedLoginHistory == null) {
            this.failedLoginHistory = objectMapper.createObjectNode();
        }
        ((ObjectNode)this.failedLoginHistory).set(clientIpAddress, value);
    }

    public JsonNode isEnabled(String clientIpAddress) {
        return this.getClientIpAddressNode(clientIpAddress).get(ModelConstants.USER_CREDENTIALS_IS_ENABLED_COLUMN);
    }

    public void setEnabled(boolean isEnabled, String clientIpAddress) {
        JsonNode clientIpAddressNode = this.getClientIpAddressNode(clientIpAddress);
        ((ObjectNode)clientIpAddressNode).put(ModelConstants.USER_CREDENTIALS_IS_ENABLED_COLUMN, isEnabled);
        this.setClientIpAddressNode(clientIpAddress, clientIpAddressNode);
    }

    public JsonNode getFailedLoginLockExpirationMillis(String clientIpAddress) {
        return this.getClientIpAddressNode(clientIpAddress).get(ModelConstants.USER_CREDENTIALS_FAILED_LOGIN_LOCK_EXPIRATION_MILLIS_COLUMN);

    }

    public void setFailedLoginLockExpirationMillis(long failedLoginLockExpirationMillis, String clientIpAddress) {
        JsonNode clientIpAddressNode = this.getClientIpAddressNode(clientIpAddress);
        ((ObjectNode)clientIpAddressNode).put(ModelConstants.USER_CREDENTIALS_FAILED_LOGIN_LOCK_EXPIRATION_MILLIS_COLUMN, failedLoginLockExpirationMillis);
        this.setClientIpAddressNode(clientIpAddress, clientIpAddressNode);
    }

    public JsonNode getFirstFailedLoginAttemptMillis(String clientIpAddress) {
        return this.getClientIpAddressNode(clientIpAddress).get(ModelConstants.USER_CREDENTIALS_FIRST_FAILED_LOGIN_ATTEMPT_MILLIS_COLUMN);

    }

    public void setFirstFailedLoginAttemptMillis(long firstFailedLoginAttemptMillis, String clientIpAddress) {
        JsonNode clientIpAddressNode = this.getClientIpAddressNode(clientIpAddress);
        ((ObjectNode)clientIpAddressNode).put(ModelConstants.USER_CREDENTIALS_FIRST_FAILED_LOGIN_ATTEMPT_MILLIS_COLUMN, firstFailedLoginAttemptMillis);
        this.setClientIpAddressNode(clientIpAddress, clientIpAddressNode);
    }

    public JsonNode getFailedLoginCount(String clientIpAddress) {
        return this.getClientIpAddressNode(clientIpAddress).get(ModelConstants.USER_CREDENTIALS_FAILED_LOGIN_COUNT_COLUMN);

    }

    public void setFailedLoginCount(int failedLoginCount, String clientIpAddress) {
        JsonNode clientIpAddressNode = this.getClientIpAddressNode(clientIpAddress);
        ((ObjectNode)clientIpAddressNode).put(ModelConstants.USER_CREDENTIALS_FAILED_LOGIN_COUNT_COLUMN, failedLoginCount);
        this.setClientIpAddressNode(clientIpAddress, clientIpAddressNode);
    }

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append("User credentials [id=");
        builder.append(this.getId());
        builder.append(", userId=");
        builder.append(this.getUserId());
        builder.append(", rawPassword=");
        builder.append(this.getRawPassword());
        builder.append(", hashedPassword=");
        builder.append(this.getHashedPassword());
        builder.append(", activateToken=");
        builder.append(this.getActivationToken());
        builder.append(", activationTokenExpirationMillis=");
        builder.append(this.getActivationTokenExpirationMillis());
        builder.append(", resetPasswordToken=");
        builder.append(this.getResetPasswordToken());
        builder.append(", failedLoginHistory=");
        builder.append(this.getFailedLoginHistory());
        builder.append(", failedLoginLockExpirationMillis=");
//        builder.append(this.getFailedLoginLockExpirationMillis());
//        builder.append(", firstFailedLoginAttempts=");
//        builder.append(this.getFirstFailedLoginAttemptsMillis());
        builder.append(", isVerified=");
        builder.append(this.isVerified());
//        builder.append(", isEnabled=");
//        builder.append(this.isEnabled());
        builder.append(", createdAt=");
        builder.append(this.getCreatedAt());
        builder.append(", updatedAt=");
        builder.append(this.getUpdatedAt());
        return builder.toString();
    }
}
