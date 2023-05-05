package com.example.tutorial.service;

import com.example.tutorial.common.data.User;
import com.example.tutorial.common.data.UserCredentials;
import com.example.tutorial.common.utils.UrlUtils;
import com.example.tutorial.config.MailConfiguration;
import com.example.tutorial.config.SecuritySettingsConfiguration;
import com.example.tutorial.exception.IncorrectParameterException;
import com.example.tutorial.exception.ItemNotFoundException;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.RandomStringUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

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
        userCredentials.setEnabled(true);
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
        UserCredentials savedUserCredentials = userCredentialsService.save(userCredentials);
        String activationLink = String.format(this.ACTIVATION_URL_PATTERN, UrlUtils.getBaseUrl(request), savedUserCredentials.getActivationToken());
        mailService.sendActivationMail(email, activationLink);
    }
}
