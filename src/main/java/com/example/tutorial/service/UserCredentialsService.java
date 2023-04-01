package com.example.tutorial.service;

import com.example.tutorial.common.data.UserCredentials;
import com.example.tutorial.model.UserCredentialsEntity;

import java.util.UUID;

public interface UserCredentialsService {
    UserCredentials saveUserCredentials(UserCredentials userCredentials);
    UserCredentials findUserCredentialsById(UUID id);
    UserCredentials findUserCredentialsByUserId(UUID id);
}
