package com.example.tutorial.service;

import com.example.tutorial.common.data.UserCredentials;
import com.example.tutorial.common.utils.DaoUtils;
import com.example.tutorial.common.validator.UserCredentialsDataValidator;
import com.example.tutorial.model.UserCredentialsEntity;
import com.example.tutorial.repository.UserCredentialsRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.UUID;
@Service
@Slf4j
public class UserCredentialsServiceImpl extends BaseService<UserCredentials, UserCredentialsEntity> implements UserCredentialsService{

    @Autowired
    private UserCredentialsRepository userCredentialsRepository;
    @Autowired
    private UserCredentialsDataValidator userCredentialsDataValidator;
    @Autowired
    private PasswordEncoder passwordEncoder;
    @Override
    public JpaRepository<UserCredentialsEntity, UUID> getRepository() {
        return this.userCredentialsRepository;
    }

    @Override
    public Class<UserCredentialsEntity> getEntityClass() {
        return UserCredentialsEntity.class;
    }

    @Override
    public UserCredentials saveUserCredentials(UserCredentials userCredentials) {
        log.info("Performing service saveUserCredentials");
        this.userCredentialsDataValidator.validate(userCredentials);
        userCredentials.setHashedPassword(passwordEncoder.encode(userCredentials.getRawPassword()));
        return save(userCredentials);
    }

    @Override
    public UserCredentials findUserCredentialsById(UUID id) {
        log.info("Performing service findUserCredentialsById");
        return DaoUtils.toData(userCredentialsRepository.findById(id));
    }

    @Override
    public UserCredentials findUserCredentialsByUserId(UUID id) {
        log.info("Performing service findUserCredentialsByUserId");
        return DaoUtils.toData(userCredentialsRepository.findByUserId(id));
    }
}
