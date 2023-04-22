package com.example.tutorial.service;

import com.example.tutorial.common.data.UserCredentials;
import com.example.tutorial.common.utils.DaoUtils;
import com.example.tutorial.common.validator.UserCredentialsDataValidator;
import com.example.tutorial.model.UserCredentialsEntity;
import com.example.tutorial.repository.UserCredentialsRepository;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.UUID;
@Service
@Slf4j
public class UserCredentialsServiceImpl extends DataBaseService<UserCredentials, UserCredentialsEntity> implements UserCredentialsService{

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
    public UserCredentials create(UserCredentials userCredentials) {
        this.userCredentialsDataValidator.validateOnCreate(userCredentials);
        userCredentials.setHashedPassword(passwordEncoder.encode(userCredentials.getRawPassword()));
        return super.save(userCredentials);
    }

    @Override
    public UserCredentials save(UserCredentials userCredentials) {
        log.info("Performing UserCredentials save");
        this.userCredentialsDataValidator.validateOnUpdate(userCredentials);
        if (StringUtils.isNotEmpty(userCredentials.getRawPassword())) {
            userCredentials.setHashedPassword(passwordEncoder.encode(userCredentials.getRawPassword()));
        }
        return super.save(userCredentials);
    }

    @Override
    public UserCredentials findById(UUID id) {
        log.info("Performing UserCredentials findById");
        return DaoUtils.toData(userCredentialsRepository.findById(id));
    }

    @Override
    public UserCredentials findByUserId(UUID id) {
        log.info("Performing UserCredentials findByUserId");
        return DaoUtils.toData(userCredentialsRepository.findByUserId(id));
    }

    @Override
    public UserCredentials findByActivationToken(String activationToken) {
        log.info("Performing UserCredentials findByActivationToken");
        return DaoUtils.toData(userCredentialsRepository.findByActivationToken(activationToken));
    }

    @Override
    public void deleteByUserId(UUID userId) {
        log.info("Performing UserCredentials deleteByUserId");
        userCredentialsRepository.deleteByUserId(userId);
    }

    @Override
    public void deleteById(UUID id) {
        log.info("Performing UserCredentials deleteById");
        userCredentialsRepository.deleteById(id);
    }

}
