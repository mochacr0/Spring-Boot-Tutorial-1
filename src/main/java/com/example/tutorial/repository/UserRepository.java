package com.example.tutorial.repository;

import com.example.tutorial.model.UserEntity;
import jakarta.validation.constraints.NotNull;
import lombok.NonNull;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.lang.NonNullApi;

import java.util.Optional;
import java.util.UUID;

public interface UserRepository extends JpaRepository<UserEntity, UUID> {
    @NonNull
    Page<UserEntity> findAll(@NonNull Pageable pageable);
    Optional<UserEntity> findByName(String name);
    Optional<UserEntity> findByEmail(String email);
}
