package com.example.tutorial.model;

import lombok.NoArgsConstructor;

@NoArgsConstructor
public class ModelConstants {

    /**
     * Generics constants.
     */
    public static final String NAME_COLUMN = "name";
    public static final String CREATED_AT_COLUMN = "createdAt";
    public static final String UPDATED_AT_COLUMN = "updatedAt";

    /**
     * ID constants.
     */
    public static final String ID_COLUMN = "id";
    public static final String USER_CREDENTIALS_USER_ID_COLUMN = "userId";

    /**
     * User constants.
     */
    public static final String PASSWORD_COLUMN = "password";
    public static final String AUTHORITY_COLUMN = "authority";
    public static final String EMAIL_COLUMN = "email";
    public static final String USER_TABLE = "users";

    /**
     * User credentials constants.
     */
    public static final String USER_CREDENTIALS_TABLE = "userCredentials";
    public static final String USER_CREDENTIALS_ACTIVATION_TOKEN_COLUMN = "activationToken";
    public static final String USER_CREDENTIALS_PASSWORD_COLUMN = "password";
    public static final String USER_CREDENTIALS_RESET_PASSWORD_TOKEN_COLUMN = "resetPasswordToken";
    public static final String USER_CREDENTIALS_FAILED_LOGIN_ATTEMPTS_TOKEN_COLUMN = "failedLoginAttempts";
//    public static final String USER_CREDENTIALS_MAX_FAILED_LOGIN_ATTEMPTS_RESET_PASSWORD_TOKEN_COLUMN = "maxFailedLoginAttempts";
    public static final String USER_CREDENTIALS_IS_ENABLED_COLUMN = "isEnabled";
    public static final String USER_CREDENTIALS_ACTIVATION_TOKEN_EXPIRATION_MILLIS= "activationTokenExpirationMillis";


}
