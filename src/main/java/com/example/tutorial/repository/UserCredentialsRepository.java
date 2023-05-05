package com.example.tutorial.repository;

import com.example.tutorial.model.UserCredentialsEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface UserCredentialsRepository extends JpaRepository<UserCredentialsEntity, UUID> {
    Optional<UserCredentialsEntity> findByUserId(UUID id);
    Optional<UserCredentialsEntity> findByActivationToken(String activationToken);
    void deleteByUserId(UUID userId);
    @Query("SELECT u FROM UserCredentialsEntity u WHERE u.isVerified = FALSE AND u.activationTokenExpirationMillis < :currentTime")
    List<UserCredentialsEntity> findUnverifiedUsers(@Param("currentTime") long currentTime);
}
