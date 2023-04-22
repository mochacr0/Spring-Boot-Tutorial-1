package com.example.tutorial.common.data;

import com.example.tutorial.common.security.Authority;
import com.example.tutorial.common.validator.Length;
import com.example.tutorial.model.ToEntity;
import com.example.tutorial.model.UserEntity;
import com.example.tutorial.security.oauth2.OAuth2UserInfo;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
@Schema
public class User extends AbstractData implements ToEntity<UserEntity> {

    @Length(fieldName = "name")
    @Schema(title = "name", description = "Username", example = "user00")
    private String name;

    @Length(fieldName = "email")
    @Schema(title = "email", description = "User email", example = "nthai2001cr@gmail.com")
    @Email
    private String email;

//    @Length(fieldName = "password")
//    @Schema(title = "password", description = "User password")
//    private String password;

    @Schema(title = "role", description = "User role", example = "USER, ADMIN")
    private Authority authority = Authority.USER;

    @Schema(title = "id", description = "User ID", example = "123e4567-e89b-12d3-a456-426614174000")
    public UUID getId() {
        return this.id;
    }

    @Schema(title = "createdAt", description = "Timestamp of the user creation", accessMode = Schema.AccessMode.READ_ONLY)
    public LocalDateTime getCreatedAt() {
        return this.createdAt;
    }

    @Schema(title = "updatedAt", description = "Timestamp of the user update", accessMode = Schema.AccessMode.READ_ONLY)
    public LocalDateTime getUpdatedAt() {
        return this.updatedAt;
    }

    public Authority getAuthority() {
        return this.authority;
    }

    public User(User user) {
        this.setId(user.getId());
        this.setName(user.getName());
        this.setEmail(user.getEmail());
//        this.setPassword(user.getPassword());
        this.setAuthority(user.getAuthority());
        this.setCreatedAt(user.getCreatedAt());
        this.setUpdatedAt(user.getUpdatedAt());
    }

    public User(OAuth2UserInfo oauth2UserInfo) {
        this.setName(oauth2UserInfo.getName());
        this.setEmail(oauth2UserInfo.getEmail());
//        this.setPassword("123456");
    }

    public User(RegisterUserRequest registerUserRequest) {
        this.setName(registerUserRequest.getName());
        this.setEmail(registerUserRequest.getEmail());
    }

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append("User [id=");
        builder.append(this.id);
        builder.append(", name=");
        builder.append(this.name);
        builder.append(", email=");
        builder.append(this.email);
        builder.append(", authority");
        builder.append(this.authority.name());
        builder.append(", createdAt=");
        builder.append(this.createdAt);
        builder.append(", updatedAt=");
        builder.append(this.updatedAt);
        builder.append("]");
        return builder.toString();
    }

    @Override
    public UserEntity toEntity() {
        UserEntity entity = new UserEntity();
        entity.setId(this.getId());
        entity.setName(this.getName());
        entity.setEmail(this.getEmail());
//        entity.setPassword(this.getPassword());
        entity.setAuthority(this.getAuthority());
        return entity;
    }
}
