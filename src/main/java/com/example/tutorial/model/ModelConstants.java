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
    public static final String USER_CREDENTIALS_PASSWORD_RESET_TOKEN_COLUMN = "passwordResetToken";
    public static final String USER_CREDENTIALS_PASSWORD_RESET_TOKEN_EXPIRATION_MILLIS_COLUMN = "passwordResetTokenExpirationMillis";
    public static final String USER_CREDENTIALS_FAILED_LOGIN_HISTORY_TOKEN_COLUMN = "failedLoginHistory";
    public static final String USER_CREDENTIALS_IS_VERIFIED_COLUMN = "isVerified";
    public static final String USER_CREDENTIALS_IS_ENABLED_COLUMN = "isEnabled";
    public static final String USER_CREDENTIALS_ACTIVATION_TOKEN_EXPIRATION_MILLIS_COLUMN = "activationTokenExpirationMillis";
    public static final String USER_CREDENTIALS_FAILED_LOGIN_LOCK_EXPIRATION_MILLIS_COLUMN = "failedLoginLockExpirationMillis";
    public static final String USER_CREDENTIALS_FAILED_LOGIN_COUNT_COLUMN = "failedLoginCount";
    public static final String USER_CREDENTIALS_FIRST_FAILED_LOGIN_ATTEMPT_MILLIS_COLUMN = "firstFailedLoginAttemptMillis";
    public static final String USER_CREDENTIALS_ADDITIONAL_INFO_COLUMN = "additionalInfo";


}
