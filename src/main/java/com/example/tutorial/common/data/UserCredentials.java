package com.example.tutorial.common.data;

import com.example.tutorial.model.ToEntity;
import com.example.tutorial.model.UserCredentialsEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
@EqualsAndHashCode(callSuper = true)
public class UserCredentials extends AbstractData implements ToEntity<UserCredentialsEntity> {
    private UUID userId;
    private String rawPassword;
    private String hashedPassword;
    private String activateToken;
    private String resetPasswordToken;
    private int failedLoginAttempts;
//    private int maxFailedLoginAttempts;
    private boolean isEnabled;

    public UserCredentials() {
        setFailedLoginAttempts(0);
        setEnabled(false);
    }

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
        entity.setActivateToken(this.getActivateToken());
        entity.setResetPasswordToken(this.getResetPasswordToken());
        return entity;
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
        builder.append(this.getActivateToken());
        builder.append(", resetPasswordToken=");
        builder.append(this.getResetPasswordToken());
        builder.append(", createdAt=");
        builder.append(this.getCreatedAt());
        builder.append(", updatedAt=");
        builder.append(this.getUpdatedAt());
        return builder.toString();
    }
}
