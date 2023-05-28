package com.example.tutorial.service;

import com.example.tutorial.common.data.ChangePasswordRequest;
import com.example.tutorial.common.data.PasswordResetRequest;
import com.example.tutorial.common.data.User;
import com.example.tutorial.common.data.UserCredentials;
import com.example.tutorial.common.security.SecurityUser;
import com.example.tutorial.common.utils.UrlUtils;
import com.example.tutorial.common.validator.CommonValidator;
import com.example.tutorial.common.validator.UserCredentialsDataValidator;
import com.example.tutorial.config.MailConfiguration;
import com.example.tutorial.config.SecuritySettingsConfiguration;
import com.example.tutorial.exception.IncorrectParameterException;
import com.example.tutorial.exception.InvalidDataException;
import com.example.tutorial.exception.ItemNotFoundException;
import com.example.tutorial.security.JwtToken;
import com.example.tutorial.security.JwtTokenFactory;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.RandomStringUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.awt.desktop.SystemEventListener;

@Service
@Slf4j
public class AuthServiceImpl extends AbstractService implements AuthService {
    @Autowired
    private UserCredentialsService userCredentialsService;
    @Autowired
    private UserService userService;
    @Autowired
    private MailConfiguration mailConfiguration;
    @Autowired
    private MailService mailService;
    @Autowired
    private SecuritySettingsConfiguration securitySettings;
    @Autowired
    private PasswordEncoder passwordEncoder;
    @Autowired
    private JwtTokenFactory tokenFactory;
    @Autowired
    private CommonValidator commonValidator;
    @Override
    public void activateEmail(String activationToken) {
        log.info("Performing service activateEmail");
        if (StringUtils.isEmpty(activationToken)) {
            throw new IncorrectParameterException("Activation token cannot be empty");
        }
        UserCredentials userCredentials = userCredentialsService.findByActivationToken(activationToken);
        if (userCredentials == null) {
            throw new ItemNotFoundException(String.format("Unable to find user credentials by activationToken [%s]", activationToken));
        }
        //expired activation token
        if (userCredentials.getActivationTokenExpirationMillis() <= System.currentTimeMillis()) {
            userCredentials.setActivationToken(null);
            userCredentials.setActivationTokenExpirationMillis(0);
            userCredentialsService.save(userCredentials);
            throw new IncorrectParameterException(String.format("Activation token is no longer valid: [%s]", activationToken));
        }
        //valid activation token
        userCredentials.setVerified(true);
        userCredentials.setActivationToken(null);
        userCredentials.setActivationTokenExpirationMillis(0);
        userCredentialsService.save(userCredentials);
    }

    @Override
    public void resendActivationTokenByEmail(String email, HttpServletRequest request) {
        log.info("Performing service resendActivationTokenByEmail");
        if (StringUtils.isEmpty(email)) {
            throw new IncorrectParameterException("Email cannot be empty");
        }
        User user = userService.findByEmail(email);
        if (user == null) {
            throw new ItemNotFoundException(String.format("Unable to find user with given email [%s]", email));
        }
        UserCredentials userCredentials = userCredentialsService.findByUserId(user.getId());
        if (userCredentials == null) {
            throw new ItemNotFoundException(String.format("Unable to find user credentials with userId [%s]", user.getId()));
        }
        if (userCredentials.isVerified()) {
            throw new IncorrectParameterException(String.format("User with given email [%s] is already verified", user.getEmail()));
        }
        String activationToken = RandomStringUtils.randomAlphanumeric(this.DEFAULT_TOKEN_LENGTH);
        userCredentials.setActivationToken(activationToken);
        userCredentials.setActivationTokenExpirationMillis(System.currentTimeMillis() + securitySettings.getActivationTokenExpirationMillis());
        userCredentialsService.save(userCredentials);
        String activationLink = String.format(this.ACTIVATION_URL_PATTERN, UrlUtils.getBaseUrl(request), activationToken);
        mailService.sendActivationMail(email, activationLink);
    }

    @Override
    public JwtToken changePassword(ChangePasswordRequest request) {
        log.info("Performing service changePassword");
        if (StringUtils.isEmpty(request.getCurrentPassword())) {
            throw new InvalidDataException("Current password cannot be empty");
        }
        commonValidator.validatePasswords(request.getNewPassword(), request.getConfirmPassword());
        SecurityUser currentUser = this.getCurrentUser();
        UserCredentials userCredentials = userCredentialsService.findByUserId(currentUser.getId());
        if (userCredentials == null) {
            throw new ItemNotFoundException("Unable to find user credentials for current user");
        }
        if (!passwordEncoder.matches(request.getCurrentPassword(), userCredentials.getHashedPassword())) {
            throw new InvalidDataException("Current password is not matched");
        }
        if (!securitySettings.getPasswordPolicy().isRepeatedPasswordAllowed() &&
                request.getCurrentPassword().equals(request.getNewPassword())) {
            throw new InvalidDataException("New password must be different from the current password");
        }
        userCredentials.setHashedPassword(passwordEncoder.encode(request.getNewPassword()));
        userCredentialsService.save(userCredentials);
        return tokenFactory.createAccessToken(currentUser);
    }

    @Override
    public void requestPasswordResetEmail(String email, HttpServletRequest request) {
        log.info("Performing requestPasswordResetEmail service");
        if (StringUtils.isBlank(email)) {
            throw new InvalidDataException("Email cannot be empty");
        }
        User user = userService.findByEmail(email);
        if (user == null) {
            throw new ItemNotFoundException(String.format("Unable to find user with given email [%s]", email));
        }
        UserCredentials userCredentials = userCredentialsService.findByUserId(user.getId());
        if (userCredentials == null) {
            throw new ItemNotFoundException("Unable to find user credentials for given user");
        }
        if (!userCredentials.isVerified()) {
            throw new DisabledException("User account is not verified");
        }
        String passwordResetToken = RandomStringUtils.randomAlphanumeric(this.DEFAULT_TOKEN_LENGTH);
        userCredentials.setPasswordResetToken(passwordResetToken);
        userCredentials.setPasswordResetTokenExpirationMillis(System.currentTimeMillis() + securitySettings.getPasswordResetTokenExpirationMillis());
        userCredentialsService.save(userCredentials);
        String passwordResetLink = String.format(this.PASSWORD_RESET_PATTERN, UrlUtils.getBaseUrl(request), passwordResetToken);
        mailService.sendPasswordResetMail(email, passwordResetLink);
    }

    @Override
    public void resetPassword(PasswordResetRequest request) {
        log.info("Performing resetPassword service");
        if (StringUtils.isBlank(request.getPasswordResetToken())) {
            throw new InvalidDataException("Password reset token cannot be empty");
        }
        commonValidator.validatePasswords(request.getNewPassword(), request.getConfirmPassword());
        UserCredentials userCredentials = userCredentialsService.findByPasswordResetToken(request.getPasswordResetToken());
        if (userCredentials == null) {
            throw new ItemNotFoundException(String.format("Unable to find user credentials with given password reset token [%s]", request.getPasswordResetToken()));
        }
        if (userCredentials.getPasswordResetTokenExpirationMillis() < System.currentTimeMillis()) {
            throw new InvalidDataException("Invalid password reset token");
        }
        if (passwordEncoder.matches(request.getNewPassword(), userCredentials.getHashedPassword())) {
            throw new InvalidDataException("New password must be different from the current password");
        }
        userCredentials.setPasswordResetToken(null);
        userCredentials.setPasswordResetTokenExpirationMillis(0);
        userCredentials.setHashedPassword(passwordEncoder.encode(request.getNewPassword()));
        userCredentialsService.save(userCredentials);
    }
}
