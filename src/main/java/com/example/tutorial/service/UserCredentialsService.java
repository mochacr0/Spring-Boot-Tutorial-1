package com.example.tutorial.service;

import com.example.tutorial.common.data.UserCredentials;
import com.example.tutorial.model.UserCredentialsEntity;

import java.util.UUID;

public interface UserCredentialsService {
    UserCredentials save(UserCredentials userCredentials);
    UserCredentials create(UserCredentials userCredentials);
    UserCredentials findById(UUID id);
    UserCredentials findByUserId(UUID id);
    UserCredentials findByActivationToken(String activationToken);
    void deleteById(UUID id);
    void deleteByUserId(UUID userId);

}
