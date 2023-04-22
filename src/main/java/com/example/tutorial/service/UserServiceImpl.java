package com.example.tutorial.service;

import com.example.tutorial.common.data.*;
import com.example.tutorial.common.utils.DaoUtils;
import com.example.tutorial.common.utils.UrlUtils;
import com.example.tutorial.common.validator.DataValidator;
import com.example.tutorial.config.MailConfiguration;
import com.example.tutorial.exception.InvalidDataException;
import com.example.tutorial.exception.ItemNotFoundException;
import com.example.tutorial.model.UserEntity;
import com.example.tutorial.repository.UserRepository;
import io.micrometer.common.util.StringUtils;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.transaction.Transactional;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.RandomStringUtils;
import org.apache.logging.log4j.util.Strings;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
@Slf4j
public class UserServiceImpl extends DataBaseService<User, UserEntity> implements UserService {

    @Autowired
    private UserRepository userRepository;
    @Autowired
    private DataValidator<User> userDataValidator;
    @Autowired
    private PasswordEncoder passwordEncoder;
    @Autowired
    private UserCredentialsService userCredentialsService;
    @Autowired
    private MailService mailService;
    @Autowired
    private MailConfiguration mailConfiguration;

    @Override
    public JpaRepository<UserEntity, UUID> getRepository() {
        return this.userRepository;
    }

    @Override
    public Class<UserEntity> getEntityClass() {
        return UserEntity.class;
    }

    @Override
    public PageData<User> findUsers(PageParameter pageParameter) {
        log.info("Performing UserService findUsers");
        validatePageParameter(pageParameter);
        return DaoUtils.toPageData(userRepository.findAll(DaoUtils.toPageable(pageParameter)));
    }

    @Override
    public User save(User user) {
        log.info("Performing UserService save");
        userDataValidator.validateOnUpdate(user);
//        if (!StringUtils.isEmpty(user.getPassword())) {
//            String encodedPassword = passwordEncoder.encode(user.getPassword());
//            user.setPassword(encodedPassword);
//        }
        return super.save(user);
    }

    @Override
    public User findByName(String name) {
        log.info("Performing UserService findByName");
        return DaoUtils.toData(userRepository.findByName(name));
    }

    @Override
    public User findById(UUID id) {
        log.info("Performing UserService findById");
        User user = DaoUtils.toData(userRepository.findById(id));
        if (user == null) {
            throw new ItemNotFoundException(String.format("User with id [%s] is not found", id));
        }
        return user;
    }

    @Override
    public User findByEmail(String email) {
        log.info("Performing UserService findByEmail");
        return DaoUtils.toData(userRepository.findByEmail(email));
    }

    @Override
    @Transactional
    public User register(RegisterUserRequest registerUserRequest, HttpServletRequest request, boolean isMailRequired) {
        log.info("Performing UserService register");
        validateRegisterPasswords(registerUserRequest);
        if (!registerUserRequest.getPassword().equals(registerUserRequest.getConfirmPassword())) {
            throw new InvalidDataException("Password and confirm password are not matched");
        }
        User user = new User(registerUserRequest);
        //validate user
        userDataValidator.validateOnCreate(user);
        //save user
        User savedUser = super.save(user);
        if (savedUser.getId() != null) {
            UserCredentials userCredentials = new UserCredentials();
            userCredentials.setUserId(savedUser.getId());
            userCredentials.setRawPassword(registerUserRequest.getPassword());
            if (isMailRequired) {
                String activateToken = RandomStringUtils.randomAlphanumeric(this.DEFAULT_TOKEN_LENGTH);
                //note: expiryTime for activateToken
                userCredentials.setActivationToken(activateToken);
                userCredentials.setActivationTokenExpirationMillis(System.currentTimeMillis() + mailConfiguration.getDefaultActivationTokenExpirationMillis());
            }
            //save credentials
            UserCredentials savedUserCredentials = userCredentialsService.create(userCredentials);
            if (savedUserCredentials.getId() != null && StringUtils.isNotEmpty(savedUserCredentials.getActivationToken())) {
                String activateLink = String.format(this.ACTIVATION_URL_PATTERN, UrlUtils.getBaseUrl(request), savedUserCredentials.getActivationToken());
                mailService.sendActivationMail(savedUser.getEmail(), activateLink);
            }
        }
        return savedUser;
    }

    public void resentEmailActivationToken() {}

    @Override
    @Transactional
    public void deleteById(UUID id) {
        log.info("Performing UserService deleteById");
        userCredentialsService.deleteByUserId(id);
        userRepository.deleteById(id);
    }

    private void validateRegisterPasswords(RegisterUserRequest request) {
        if (Strings.isEmpty(request.getPassword())) {
            throw new InvalidDataException("Password field cannot be empty");
        }
        if (Strings.isEmpty(request.getConfirmPassword())) {
            throw new InvalidDataException("Confirm password field cannot be empty");
        }
    }

}
