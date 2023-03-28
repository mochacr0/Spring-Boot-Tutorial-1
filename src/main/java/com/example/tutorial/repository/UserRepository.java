package com.example.tutorial.repository;

import com.example.tutorial.model.UserEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface UserRepository extends JpaRepository<UserEntity, UUID> {
    Page<UserEntity> findAll(Pageable pageable);
    Optional<UserEntity> findByName(String name);
    Optional<UserEntity> findByEmail(String email);
}
