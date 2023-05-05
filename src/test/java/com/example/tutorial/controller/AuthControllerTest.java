package com.example.tutorial.controller;

import com.example.tutorial.common.data.User;
import com.example.tutorial.service.UserService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import java.util.UUID;

import static com.example.tutorial.controller.ControllerTestConstants.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

public class AuthControllerTest extends AbstractControllerTest {
    @Autowired
    private UserService userService;
    @Test
    void testGetUserPasswordPolicy() throws Exception {
        performGet("/auth/passwordPolicy").andExpect(status().isOk());
    }
    @Test
    void testLoginWithFailedLoginLock() throws Exception {
        User user = createUser(DEFAULT_USER_NAME, DEFAULT_USER_EMAIL, DEFAULT_PASSWORD, DEFAULT_PASSWORD);
        performPostWithEmptyBody(FIND_USER_BY_ID_ROUTE + "/activate", user.getId().toString());
        String invalidPassword = "Not" + DEFAULT_PASSWORD;
        for (int i = 0; i <= this.maxFailedLoginAttempts; i++) {
            login(DEFAULT_USER_NAME, invalidPassword).andExpect(status().isUnauthorized());
        }
        login(DEFAULT_USER_NAME, invalidPassword).andExpect(status().isUnauthorized());
        Thread.sleep(failedLoginLockExpirationMillis);
        login(DEFAULT_USER_NAME, DEFAULT_PASSWORD).andExpect(status().isOk());
        deleteUser(user.getId());
    }

}
