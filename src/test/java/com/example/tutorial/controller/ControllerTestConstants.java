package com.example.tutorial.controller;

import org.springframework.http.MediaType;

public class ControllerTestConstants {
    public static final MediaType CONTENT_TYPE = MediaType.APPLICATION_JSON;
    protected static final int NEGATIVE_INT_VALUE = Integer.MIN_VALUE;
    protected static final int ZERO_INT_VALUE = 0;
    protected static final String STRING_VALUE = "string";
    protected static final String INVALID_SORT_DIRECTION = "invalidDirection";
    protected static final String INVALID_SORT_PROPERTY = "theresNoWayThatAPropertyCanBeLikeThis";

    //User controller test constants
    protected static String DEFAULT_USER_EMAIL = "defaultuser@gmail.com";
    protected static String DEFAULT_USER_NAME = "defaultuser";
    protected static final String DEFAULT_PASSWORD = "Defaultpassword";
    public static final String USER_ROUTE = "/users";
    public static final String REGISTER_USER_ROUTE = USER_ROUTE + "/register";
    public static final String FIND_USERS_ROUTE = USER_ROUTE;
    public static final String FIND_USER_BY_ID_ROUTE = USER_ROUTE + "/{userId}";
    public static final String DELETE_USER_BY_ID_ROUTE = USER_ROUTE + "/{userId}";

    //Auth controller test constants
    public static final String AUTH_ROUTE = "/auth";
}
