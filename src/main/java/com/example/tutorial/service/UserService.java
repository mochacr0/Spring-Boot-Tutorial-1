package com.example.tutorial.service;

import com.example.tutorial.common.data.PageData;
import com.example.tutorial.common.data.PageParameter;
import com.example.tutorial.common.data.RegisterUserRequest;
import com.example.tutorial.common.data.User;
import jakarta.servlet.http.HttpServletRequest;

import java.util.UUID;

public interface UserService {
    PageData<User> findUsers(PageParameter pageParameter);
    User save(User user);
    User findByName(String name);
    User findById(UUID id);
    User findByEmail(String email);
    User register(RegisterUserRequest user, HttpServletRequest request, boolean isMailRequired);
    void deleteById(UUID id);
    void deleteUnverifiedUsers();
}
