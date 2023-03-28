package com.example.tutorial.model;

import com.example.tutorial.common.data.User;
import com.example.tutorial.common.security.Authority;
import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;

import static com.example.tutorial.model.ModelConstants.*;


@Entity
@Table(name = USER_TABLE)
@Data
@NoArgsConstructor
public class UserEntity extends AbstractEntity<User> {
    @Column(name = NAME_COLUMN, unique = true)
    private String name;

    @Column(name = EMAIL_COLUMN, unique = true)
    private String email;

    @Column(name = PASSWORD_COLUMN)
    private String password;

    @Enumerated(EnumType.STRING)
    @Column(name = AUTHORITY_COLUMN)
    private Authority authority;

    @Override
    public User toData() {
        User user = new User();
        user.setId(this.getId());
        user.setName(this.getName());
        user.setEmail(this.getEmail());
        user.setPassword(this.getPassword());
        user.setAuthority(this.getAuthority());
        user.setCreatedAt(this.getCreatedAt());
        user.setUpdatedAt(this.getUpdatedAt());
        return user;
    }

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append("UserEntity [id=");
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

}
