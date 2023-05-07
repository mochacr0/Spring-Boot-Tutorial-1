package com.example.tutorial.controller;

import com.example.tutorial.common.data.User;
import com.example.tutorial.service.UserService;
import jakarta.xml.bind.annotation.XmlType;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import java.util.UUID;

import static com.example.tutorial.controller.ControllerTestConstants.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

public class AuthControllerTest extends AbstractControllerTest {
    private final String BAD_CREDENTIALS_EXCEPTION_MESSAGE = "Authentication failed: The username or password you entered is incorrect. Please try again";
    private final String LOCKED_EXCEPTION_MESSAGE = "Authentication failed: Username was locked due to security policy";
    @Autowired
    private UserService userService;
    @Test
    void testGetUserPasswordPolicy() throws Exception {
        performGet("/auth/passwordPolicy").andExpect(status().isOk());
    }

    @Nested
    @TestInstance(TestInstance.Lifecycle.PER_CLASS)
    class LoginMethodTest {
        private final String INVALID_PASSWORD = "Not" + DEFAULT_PASSWORD;
        private User user;

        @BeforeAll
        void setUp() throws Exception {
            user = createUser(DEFAULT_USER_NAME, DEFAULT_USER_EMAIL, DEFAULT_PASSWORD, DEFAULT_PASSWORD);
            performPostWithEmptyBody(FIND_USER_BY_ID_ROUTE + "/activate", user.getId().toString());
        }

        @AfterAll
        void tearDown() throws Exception {
            if (user != null) {
                deleteUser(user.getId());
            }
        }

        @Test
        void testLoginWithValidCredentials() throws Exception {
            login(DEFAULT_USER_NAME, DEFAULT_PASSWORD).andExpect(status().isOk());
        }

        @Test
        void testLoginWithFailedLoginLock() throws Exception {
            //failed maximum times, should return bad credentials
            for (int i = 0; i < maxFailedLoginAttempts; i++) {
                login(DEFAULT_USER_NAME, INVALID_PASSWORD)
                        .andExpect(status().isUnauthorized())
                        .andExpect(jsonPath("$.message", Matchers.is(BAD_CREDENTIALS_EXCEPTION_MESSAGE)));
            }
            //exceeded maximum allowed attempts, should return locked
            login(DEFAULT_USER_NAME, INVALID_PASSWORD)
                    .andExpect(status().isUnauthorized())
                    .andExpect(jsonPath("$.message", Matchers.is(LOCKED_EXCEPTION_MESSAGE)));
            //Wait until the expiration time has passed
            Thread.sleep(failedLoginLockExpirationMillis);
            //Now login should return ok
            login(DEFAULT_USER_NAME, DEFAULT_PASSWORD).andExpect(status().isOk());
        }

        @Test
        void testLoginFailMultipleTimesButNotWithinTheSameInterval() throws Exception{
            //test login fail multiple times but not within the same interval
            //first failed login
            login(DEFAULT_USER_NAME, INVALID_PASSWORD)
                    .andExpect(status().isUnauthorized())
                    .andExpect(jsonPath("$.message", Matchers.is(BAD_CREDENTIALS_EXCEPTION_MESSAGE)));
            //Wait until the interval time has passed
            Thread.sleep(failedLoginIntervalMillis);
            //then try to fail maximum times, should return bad credentials
            for (int i = 0; i < maxFailedLoginAttempts; i++) {
                login(DEFAULT_USER_NAME, INVALID_PASSWORD)
                        .andExpect(status().isUnauthorized())
                        .andExpect(jsonPath("$.message", Matchers.is(BAD_CREDENTIALS_EXCEPTION_MESSAGE)));
            }
            //should return locked
            login(DEFAULT_USER_NAME, INVALID_PASSWORD)
                    .andExpect(status().isUnauthorized())
                    .andExpect(jsonPath("$.message", Matchers.is(LOCKED_EXCEPTION_MESSAGE)));
        }
    }



}
