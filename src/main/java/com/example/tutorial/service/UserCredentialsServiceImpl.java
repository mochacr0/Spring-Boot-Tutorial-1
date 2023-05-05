package com.example.tutorial.service;

import com.example.tutorial.common.data.UserCredentials;
import com.example.tutorial.common.utils.DaoUtils;
import com.example.tutorial.common.validator.UserCredentialsDataValidator;
import com.example.tutorial.config.SecuritySettingsConfiguration;
import com.example.tutorial.model.UserCredentialsEntity;
import com.example.tutorial.repository.UserCredentialsRepository;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.LockedException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
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
    @Autowired
    private SecuritySettingsConfiguration securitySettings;

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
        log.info("Performing UserCredentialsService save");
        this.userCredentialsDataValidator.validateOnUpdate(userCredentials);
        if (StringUtils.isNotEmpty(userCredentials.getRawPassword())) {
            userCredentials.setHashedPassword(passwordEncoder.encode(userCredentials.getRawPassword()));
        }
        return super.save(userCredentials);
    }

    @Override
    public UserCredentials findById(UUID id) {
        log.info("Performing UserCredentialsService findById");
        return DaoUtils.toData(userCredentialsRepository.findById(id));
    }

    @Override
    public UserCredentials findByUserId(UUID id) {
        log.info("Performing UserCredentialsService findByUserId");
        return DaoUtils.toData(userCredentialsRepository.findByUserId(id));
    }

    @Override
    public UserCredentials findByActivationToken(String activationToken) {
        log.info("Performing UserCredentialsService findByActivationToken");
        return DaoUtils.toData(userCredentialsRepository.findByActivationToken(activationToken));
    }

    @Override
    public void deleteByUserId(UUID userId) {
        log.info("Performing UserCredentialsService deleteByUserId");
        userCredentialsRepository.deleteByUserId(userId);
    }

    @Override
    public void validatePassword(UserCredentials userCredentials, String password) {
        log.info("Performing userCredentialsService validatePassword");
        if (!userCredentials.isVerified()) {
            throw new LockedException("Authentication failed: Email address has not yet been verified");
        }
        if (!userCredentials.isEnabled() && userCredentials.getFailedLoginLockExpirationMillis() > System.currentTimeMillis()) {
            throw new LockedException("Authentication failed: Username was locked due to security policy.");
        }
        if (!passwordEncoder.matches(password, userCredentials.getHashedPassword())) {
            int currentFailedLoginAttempts = userCredentials.getFailedLoginAttempts() + 1;
            if (currentFailedLoginAttempts > securitySettings.getMaxFailedLoginAttempts()) {
                userCredentials.setEnabled(false);
                userCredentials.setFailedLoginAttempts(0);
                userCredentials.setFailedLoginLockExpirationMillis(System.currentTimeMillis() + securitySettings.getFailedLoginLockExpirationMillis());
                save(userCredentials);
                throw new LockedException("Authentication failed: Username was locked due to security policy.");
            }
            else {
                userCredentials.setFailedLoginAttempts(currentFailedLoginAttempts);
                save(userCredentials);
                throw new BadCredentialsException("Authentication failed: The username or password you entered is incorrect. Please try again");
            }
        }
        //password is matched
        userCredentials.setEnabled(true);
        userCredentials.setFailedLoginAttempts(0);
        userCredentials.setFailedLoginLockExpirationMillis(0);
        save(userCredentials);
    }

    @Override
    public List<UserCredentials> findUnverifiedUserCredentials() {
        log.info("Performing UserCredentialsService findUnverifiedUserCredentials");
        return DaoUtils.toListData(userCredentialsRepository.findUnverifiedUsers(System.currentTimeMillis()));
    }

    @Override
    public void deleteById(UUID id) {
        log.info("Performing UserCredentials deleteById");
        userCredentialsRepository.deleteById(id);
    }

}
