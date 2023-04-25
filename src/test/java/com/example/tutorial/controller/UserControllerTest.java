package com.example.tutorial.controller;

import com.example.tutorial.common.data.PageData;
import com.example.tutorial.common.data.RegisterUserRequest;
import com.example.tutorial.common.data.User;
import com.example.tutorial.common.data.UserCredentials;
import com.example.tutorial.exception.InvalidDataException;
import com.fasterxml.jackson.core.type.TypeReference;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.annotation.Before;
import org.junit.jupiter.api.*;
import org.springframework.test.context.event.annotation.BeforeTestClass;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@Slf4j
public class UserControllerTest extends AbstractControllerTest {
    private final String USER_ROUTE = "/users";
    private final String REGISTER_USER_ROUTE = USER_ROUTE + "/register";
    private final String FIND_USERS_ROUTE = USER_ROUTE;
    private final String FIND_USER_BY_ID_ROUTE = USER_ROUTE + "/{userId}";
    private final String DELETE_USER_BY_ID_ROUTE = USER_ROUTE + "/{userId}";
//    private final String UPDATE_USER_ROUTE = "";
    @Nested
    class FindUsersMethodTest {

        @Test
        void testFindUsersWithInvalidPageNumberReturnBadRequestError() throws Exception {
            performGet(FIND_USERS_ROUTE + "?page={page}", NEGATIVE_INT_VALUE)
                    .andExpect(status().isBadRequest());
            String stringPageSize = "string";
            performGet(FIND_USERS_ROUTE + "?page={page}", STRING_VALUE)
                    .andExpect(status().isBadRequest());
        }

        @Test
        void testFindUsersWithInvalidPageSizeReturnBadRequestError() throws Exception {
            performGet(FIND_USERS_ROUTE + "?pageSize={pageSize}", ZERO_INT_VALUE)
                    .andExpect(status().isBadRequest());
            performGet(FIND_USERS_ROUTE + "?pageSize={pageSize}", NEGATIVE_INT_VALUE)
                    .andExpect(status().isBadRequest());
            performGet(FIND_USERS_ROUTE + "?pageSize={pageSize}", STRING_VALUE)
                    .andExpect(status().isBadRequest());
        }

        @Test
        void testFindUsersWithInvalidSortReturnBadRequestError() throws Exception {
            performGet(FIND_USERS_ROUTE +"?sortDirection={sortDirection}", INVALID_SORT_DIRECTION)
                    .andExpect(status().isBadRequest());
            performGet(FIND_USERS_ROUTE +"?sortProperty={sortProperty}", INVALID_SORT_PROPERTY)
                    .andExpect(status().isBadRequest());
        }

        @Test
        void testFindUsers() throws Exception {
            List<RegisterUserRequest> registerRequests = new ArrayList<>();
            for (int i = 0; i < 10; i++) {
                RegisterUserRequest request = new RegisterUserRequest();
                request.setName("usertest" + i);
                request.setEmail("usertest" + i + "@gmail.com");
                request.setPassword("Password");
                request.setConfirmPassword("Password");
                registerRequests.add(request);
                createUser(request);
            }
            int currentPage = 0;
            int pageSize = 3;
            List<User> savedUsers = new ArrayList<>();
            PageData<User> data;
            do {
                data = performGetWithReferencedType(FIND_USERS_ROUTE + "?page={page}&pageSize={pageSize}&sortOrder={sortOrder},sortProperty={sortProperty}",
                        new TypeReference<>(){},
                        currentPage, pageSize, "desc", "createdAt");
                savedUsers.addAll(data.getData().stream().toList());
                if (data.hasNext()) {
                    currentPage++;
                }
            } while (data.hasNext());
            for (User savedUser : savedUsers) {
                deleteUser(savedUser.getId().toString());
            }
            savedUsers.sort(new UserComparator<>());
    //        registerRequests.sort(new RegisterUserRequestComparator<>());
            boolean areListsTheSame = true;
            for (int i = 0; i < registerRequests.size(); i++) {
                if (!registerRequests.get(i).getName().equals(savedUsers.get(i).getName())
                        || !registerRequests.get(i).getEmail().equals(savedUsers.get(i).getEmail())) {
                    areListsTheSame = false;
                    break;
                }
            }
            Assertions.assertTrue(areListsTheSame, "The expected list and the actual list are not equal");
        }
    }

    @Nested
    class RegisterUserMethodTest {
        @Nested
        @TestInstance(TestInstance.Lifecycle.PER_CLASS)
        class RegisterUserWithExistedNameAndEmail {
            private User defaultUser;
            @BeforeAll
            void setUp() throws Exception {
                defaultUser = createUser(DEFAULT_USER_NAME, DEFAULT_USER_EMAIL, DEFAULT_PASSWORD, DEFAULT_PASSWORD);
            }
            @AfterAll
            void tearDown() throws Exception {
                deleteUser(defaultUser.getId().toString());
            }
            @Test
            void testRegisterUserWithExistedName() throws Exception {
                RegisterUserRequest request = new RegisterUserRequest();
                request.setName(defaultUser.getName());
                request.setEmail("duplicatednameuser@gmail.com");
                request.setPassword(DEFAULT_PASSWORD);
                request.setConfirmPassword(DEFAULT_PASSWORD);
                performPost(REGISTER_USER_ROUTE , request)
                        .andExpect(status().isBadRequest());
            }
            @Test
            void testRegisterUserWithExistedEmail() throws Exception {
                RegisterUserRequest request = new RegisterUserRequest();
                request.setName("duplicatedUserName");
                request.setEmail(defaultUser.getEmail());
                request.setPassword(DEFAULT_PASSWORD);
                request.setConfirmPassword(DEFAULT_PASSWORD);
                performPost(REGISTER_USER_ROUTE, request)
                        .andExpect(status().isBadRequest());
            }
        }

        @Nested
        class RegisterUserWithMissingRequireFieldTest {
            private RegisterUserRequest request;
            @BeforeEach
            void setUp() {
                request = new RegisterUserRequest();
                request.setName(DEFAULT_USER_NAME);
                request.setEmail(DEFAULT_USER_EMAIL);
                request.setMatchedPasswords(DEFAULT_PASSWORD);
            }
            @Test
            void testRegisterUserWithMissingNameField () throws Exception {
                request.setName(null);
                performPost(REGISTER_USER_ROUTE, request).andExpect(status().isBadRequest());
            }
            @Test
            void testRegisterUserWithMissingEmailField () throws Exception {
                request.setEmail(null);
                performPost(REGISTER_USER_ROUTE, request).andExpect(status().isBadRequest());
            }
            @Test
            void testRegisterUserWithMissingPasswordField () throws Exception {
                request.setPassword(null);
                performPost(REGISTER_USER_ROUTE, request).andExpect(status().isBadRequest());
            }
            @Test
            void testRegisterUserWithMissingConfirmPasswordField () throws Exception {
                request.setConfirmPassword(null);
                performPost(REGISTER_USER_ROUTE, request).andExpect(status().isBadRequest());
            }
        }

        @Test
        void testRegisterUserWithInvalidFormatEmail() throws Exception {
            String invalidFormatEmail = "nguyentrihai.com";
            RegisterUserRequest request = new RegisterUserRequest();
            request.setName(DEFAULT_USER_NAME);
            request.setEmail(invalidFormatEmail);
            request.setMatchedPasswords(DEFAULT_PASSWORD);
            performPost(REGISTER_USER_ROUTE, request)
                    .andExpect(status().isBadRequest());
        }

        @Nested
        class RegisterUserWithInvalidPasswordTest {
            RegisterUserRequest request;
            @BeforeEach
            void setUp() {
                request = new RegisterUserRequest();
                request.setName(DEFAULT_USER_NAME);
                request.setEmail(DEFAULT_USER_EMAIL);
            }
            @Test
            void testRegisterUserWithUnmatchedPasswords() throws Exception {
                String unmatchedConfirmPassword = "Not" + DEFAULT_PASSWORD;
                request.setPassword(DEFAULT_PASSWORD);
                request.setConfirmPassword(unmatchedConfirmPassword);
                performPost(REGISTER_USER_ROUTE, request)
                        .andExpect(status().isBadRequest());
            }
            @Test
            void testRegisterUserWithPasswordViolations() throws Exception {
                String noUpperCaseLetterPassword = "string";
                request.setMatchedPasswords(noUpperCaseLetterPassword);
                performPost(REGISTER_USER_ROUTE, request)
                        .andExpect(status().isBadRequest());
                String underMinimumLengthPassword = "1";
                request.setMatchedPasswords(underMinimumLengthPassword);
                performPost(REGISTER_USER_ROUTE, request)
                        .andExpect(status().isBadRequest());
            }
        }

        @Test
        void testRegisterUserWithValidBody() throws Exception {
            String name = "usertest0";
            String email = "usertest0@gmail.com";
            String password = "Password";
            User createdUser = createUser(name, email, password, password);
            Assertions.assertNotNull(createdUser);
            Assertions.assertNotNull(createdUser.getId());
            Assertions.assertEquals(name, createdUser.getName());
            Assertions.assertEquals(email, createdUser.getEmail());
        }
    }

    @Test
    void testDeleteUser() throws Exception {
        String name = "usertest1000";
        String email = "usertest1000@gmail.com";
        String password = "Password";
        User createdUser = createUser(name, email, password, password);
        Assertions.assertNotNull(createdUser);
        performDelete(DELETE_USER_BY_ID_ROUTE, createdUser.getId()).andExpect(status().isOk());
        performGet(FIND_USER_BY_ID_ROUTE, createdUser.getId()).andExpect(status().isNotFound());
    }
//    @Test
    void testDeleteUnverifiedUsers() throws Exception {
        List<User> createdUsers = new ArrayList<>();
        for (int i = 0; i < 3; i++) {
            RegisterUserRequest request = new RegisterUserRequest();
            request.setName("usertest" + i);
            request.setEmail("usertest" + i + "@gmail.com");
            request.setPassword("Password");
            request.setConfirmPassword("Password");
            createdUsers.add(createUser(request));
        }
        performDelete(USER_ROUTE + "/test-delete").andExpect(status().isOk());
        for (User createdUser : createdUsers) {
            performGet(FIND_USER_BY_ID_ROUTE, createdUser.getId()).andExpect(status().isNotFound());
        }
    }

    private User createUser(String name, String email, String password, String confirmPassword) throws Exception {
        RegisterUserRequest request = new RegisterUserRequest();
        request.setName(name);
        request.setEmail(email);
        request.setPassword(password);
        request.setConfirmPassword(confirmPassword);
        return performPost(REGISTER_USER_ROUTE, User.class, request);
    }

    private User createUser(RegisterUserRequest request) throws Exception {
        return performPost(REGISTER_USER_ROUTE, User.class, request);
    }

    private void deleteUser(String userId) throws Exception {
        performDelete(DELETE_USER_BY_ID_ROUTE, userId).andExpect(status().isOk());
    }

    public class UserComparator<T extends User> implements Comparator<T> {

        @Override
        public int compare(T o1, T o2) {
            return o1.getName().compareTo(o2.getName());
        }
    }

    public class RegisterUserRequestComparator<T extends RegisterUserRequest> implements Comparator<T> {

        @Override
        public int compare(T o1, T o2) {
            return o1.getName().compareTo(o2.getName());
        }
    }
 }
