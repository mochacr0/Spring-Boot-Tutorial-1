package com.example.tutorial.service;

import com.example.tutorial.common.data.PageParameter;
import com.example.tutorial.common.data.RegisterUserRequest;
import com.example.tutorial.common.data.User;
import com.example.tutorial.common.security.SecurityUser;
import com.example.tutorial.exception.IncorrectParameterException;
import com.example.tutorial.exception.InvalidDataException;
import org.apache.logging.log4j.util.Strings;
import org.springframework.security.authentication.AuthenticationServiceException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

public class AbstractService {
    protected final int DEFAULT_TOKEN_LENGTH = 30;
    protected final String ACTIVATION_URL_PATTERN = "%s/auth/activate?activationToken=%s";
    public void validatePageParameter(PageParameter pageParameter) {
        if (pageParameter.getPage() < 0) {
            throw new IncorrectParameterException("Page number should be positive");
        }
        if (pageParameter.getPageSize() < 0) {
            throw new IncorrectParameterException("Page size should be positive");
        }
//        boolean isSortPropertySupported = Arrays.stream(getEntityClass().getFields()).anyMatch(field -> field.getName().equals(pageParameter.getSortProperty()));
//        if (!isSortPropertySupported) {
//            throw new IncorrectParameterException("Unsupported sort property for " + this.getEntityClass().getSimpleName() + ": " + pageParameter.getSortProperty());
//        }
    }
    protected void validatePasswords(String password, String confirmPassword) {
        if (Strings.isEmpty(password)) {
            throw new InvalidDataException("Password field cannot be empty");
        }
        if (Strings.isEmpty(confirmPassword)) {
            throw new InvalidDataException("Confirm password field cannot be empty");
        }
        if (!password.equals(confirmPassword)) {
            throw new InvalidDataException("Password and confirm password are not matched");
        }
    }

    protected SecurityUser getCurrentUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !(authentication.getPrincipal() instanceof SecurityUser)) {
            throw new AuthenticationServiceException("You aren't authorized to perform this operation!");
        }
        return (SecurityUser)authentication.getPrincipal();
    }
}
