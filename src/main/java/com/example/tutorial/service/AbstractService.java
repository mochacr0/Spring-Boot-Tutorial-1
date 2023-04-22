package com.example.tutorial.service;

import com.example.tutorial.common.data.PageParameter;
import com.example.tutorial.exception.IncorrectParameterException;

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
}
