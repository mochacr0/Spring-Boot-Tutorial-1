package com.example.tutorial.service;

import com.example.tutorial.common.data.PageData;
import com.example.tutorial.common.data.UserCredentials;
import com.example.tutorial.model.UserCredentialsEntity;
import org.springframework.data.domain.Page;

import java.util.List;
import java.util.UUID;

public interface UserCredentialsService {
    UserCredentials save(UserCredentials userCredentials);
    UserCredentials create(UserCredentials userCredentials);
    UserCredentials findById(UUID id);
    UserCredentials findByUserId(UUID id);
    UserCredentials findByActivationToken(String activationToken);
    List<UserCredentials> findUnverifiedUserCredentials();
    void deleteById(UUID id);
    void deleteByUserId(UUID userId);
    void validatePassword(UserCredentials userCredentials, String password, String clientIpAddress);
//    void logLoginAction(UserCredentials userCredentials);
}
