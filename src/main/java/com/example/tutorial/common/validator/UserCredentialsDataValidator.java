package com.example.tutorial.common.validator;

import com.example.tutorial.common.data.User;
import com.example.tutorial.common.data.UserCredentials;
import com.example.tutorial.config.SecuritySettingsConfiguration;
import com.example.tutorial.exception.InvalidDataException;
import com.example.tutorial.exception.ItemNotFoundException;
import com.example.tutorial.service.UserCredentialsService;
import com.example.tutorial.service.UserService;
import io.micrometer.common.util.StringUtils;
import org.passay.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class UserCredentialsDataValidator extends DataValidator<UserCredentials>{
    @Autowired
    private SecuritySettingsConfiguration securitySettings;
    @Autowired
    private UserCredentialsService userCredentialsService;
    @Autowired
    private UserService userService;

    @Override
    protected void validateOnCreateImpl(UserCredentials data) {
        UserCredentials existingCredentialsWithGivenUserId = userCredentialsService.findByUserId(data.getUserId());
        if (existingCredentialsWithGivenUserId != null) {
            throw new InvalidDataException("User credentials with userId [" + data.getUserId() + "] already exists");
        }
    }

    @Override
    protected void validateOnUpdateImpl(UserCredentials data) {
        if (data.getId() == null) {
            throw new InvalidDataException("User credentials ID should be specified");
        }
        UserCredentials existingUserCredentials = userCredentialsService.findById(data.getId());
        if (existingUserCredentials == null) {
            throw new ItemNotFoundException("User credentials with id [" + data.getId() + "] is not found");
        }
    }

    @Override
    protected void validateCommon(UserCredentials data) {
        if (data.getUserId() == null) {
            throw new InvalidDataException("User credentials should be assigned to a specific user");
        }
        User existingUser = userService.findById(data.getUserId());
        if (existingUser == null) {
            throw new InvalidDataException("User credentials cannot be assigned to a non-existent user");
        }
        if (StringUtils.isNotEmpty(data.getRawPassword())) {
            validateRawPassword(data.getRawPassword());
        }
    }

    public void validateRawPassword(String rawPassword) {
        List<Rule> passwordRules = securitySettings.getPasswordPolicy().getPasswordRules();
        PasswordValidator validator = new PasswordValidator(passwordRules);
        RuleResult validateResult = validator.validate(new PasswordData(rawPassword));
        if (!validateResult.isValid()) {
            String violationMessage = String.join("\n", validator.getMessages(validateResult));
            throw new InvalidDataException(violationMessage);
        }
        //validate password reuse frequency
    }


}
