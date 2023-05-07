package com.example.tutorial.service;

import com.example.tutorial.common.data.UserCredentials;
import com.example.tutorial.common.utils.DaoUtils;
import com.example.tutorial.common.validator.UserCredentialsDataValidator;
import com.example.tutorial.config.SecuritySettingsConfiguration;
import com.example.tutorial.model.ModelConstants;
import com.example.tutorial.model.UserCredentialsEntity;
import com.example.tutorial.repository.UserCredentialsRepository;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.LockedException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.ui.Model;

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
    @Autowired
    private ObjectMapper objectMapper;

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
    //note: should implement IP blocking and failed
    public void validatePassword(UserCredentials userCredentials, String password, String clientIpAddress) {
        log.info("Performing userCredentialsService validatePassword");
        if (!userCredentials.isVerified()) {
            throw new LockedException("Authentication failed: Email address has not yet been verified");
        }
        JsonNode currentClientIpAddressNode = userCredentials.getFailedLoginHistory().get(clientIpAddress);
        long currentTimeMillis = System.currentTimeMillis();
        boolean isMatched = passwordEncoder.matches(password, userCredentials.getHashedPassword());
        if (currentClientIpAddressNode == null) {
            //first time login failed
            if (!isMatched) {
                userCredentials.setFailedLoginCount(1, clientIpAddress);
                userCredentials.setFirstFailedLoginAttemptMillis(currentTimeMillis, clientIpAddress);
                userCredentials.setEnabled(true, clientIpAddress);
                save(userCredentials);
                throw new BadCredentialsException("Authentication failed: The username or password you entered is incorrect. Please try again");
            }
            //password matched, return
            return;
        }
        //not the first time
        //password not matched
        if (!userCredentials.isEnabled(clientIpAddress).asBoolean(true) &&
                userCredentials.getFailedLoginLockExpirationMillis(clientIpAddress).asLong() > currentTimeMillis) {
            throw new LockedException("Authentication failed: Username was locked due to security policy");
        }
        if (isMatched) {
            //remove failed login history record for current client ip address
            ((ObjectNode)userCredentials.getFailedLoginHistory()).remove(clientIpAddress);
            return;
        }
        //not the first time but interval time had passed
        if (currentTimeMillis > userCredentials.getFirstFailedLoginAttemptMillis(clientIpAddress).asLong() + securitySettings.getFailedLoginIntervalMillis()) {
            userCredentials.setFirstFailedLoginAttemptMillis(currentTimeMillis, clientIpAddress);
            userCredentials.setFailedLoginCount(1, clientIpAddress);
            save(userCredentials);
            throw new BadCredentialsException("Authentication failed: The username or password you entered is incorrect. Please try again");
        }
        int currentFailedLoginCount = userCredentials.getFailedLoginCount(clientIpAddress).asInt() + 1;
        if (currentFailedLoginCount <= securitySettings.getMaxFailedLoginAttempts()) {
            userCredentials.setFailedLoginCount(currentFailedLoginCount, clientIpAddress);
            userCredentials.setEnabled(true, clientIpAddress);
            save(userCredentials);
            throw new BadCredentialsException("Authentication failed: The username or password you entered is incorrect. Please try again");
        }
        //lock account
        userCredentials.setEnabled(false, clientIpAddress);
        userCredentials.setFailedLoginCount(0, clientIpAddress);
        userCredentials.setFailedLoginLockExpirationMillis(currentTimeMillis + securitySettings.getFailedLoginLockExpirationMillis(), clientIpAddress);
        save(userCredentials);
        throw new LockedException("Authentication failed: Username was locked due to security policy");

//        if (!userCredentials.isEnabled() && userCredentials.getFailedLoginLockExpirationMillis() > System.currentTimeMillis()) {
//            throw new LockedException("Authentication failed: Username was locked due to security policy");
//        }
//        long currentTimeMillis = System.currentTimeMillis();
//        //password matched
//        if (passwordEncoder.matches(password, userCredentials.getHashedPassword())) {
//            userCredentials.setEnabled(true);
//            userCredentials.setFailedLoginAttempts(0);
//            userCredentials.setFailedLoginLockExpirationMillis(0);
//            save(userCredentials);
//            return;
//        }

//        //password not matched
//        //0 is default state
//        //first failed login attempt
//        if (userCredentials.getFirstFailedLoginAttemptsMillis() == 0
//                //not the first time but interval time had passed
//                || currentTimeMillis > userCredentials.getFirstFailedLoginAttemptsMillis() + securitySettings.getFailedLoginIntervalMillis()) {
//            userCredentials.setFirstFailedLoginAttemptsMillis(currentTimeMillis);
//            userCredentials.setFailedLoginAttempts(1);
//            save(userCredentials);
//            throw new BadCredentialsException("Authentication failed: The username or password you entered is incorrect. Please try again");
//        }
//        else {
//            //multiple failed login attempts
//            int currentFailedLoginAttempts = userCredentials.getFailedLoginAttempts() + 1;
//            //not yet reached maximum attempts
//            if (currentFailedLoginAttempts <= securitySettings.getMaxFailedLoginAttempts()) {
//                userCredentials.setFailedLoginAttempts(currentFailedLoginAttempts);
//                save(userCredentials);
//                throw new BadCredentialsException("Authentication failed: The username or password you entered is incorrect. Please try again");
//            }
//            //exceeded
//            else {
//                userCredentials.setEnabled(false);
//                userCredentials.setFailedLoginAttempts(0);
//                userCredentials.setFailedLoginLockExpirationMillis(System.currentTimeMillis() + securitySettings.getFailedLoginLockExpirationMillis());
//                save(userCredentials);
//                throw new LockedException("Authentication failed: Username was locked due to security policy");
//            }
//        }
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

    public void testJsonNode() {
        JsonNode jsonNode = objectMapper.createObjectNode();
    }

}
