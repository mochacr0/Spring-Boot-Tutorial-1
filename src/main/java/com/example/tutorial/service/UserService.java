package com.example.tutorial.service;

import com.example.tutorial.common.data.PageData;
import com.example.tutorial.common.data.PageParameter;
import com.example.tutorial.common.data.User;
import org.springframework.data.domain.PageRequest;

import java.util.Optional;
import java.util.UUID;

public interface UserService {
    PageData<User> findUsers(PageParameter pageParameter);
    User saveUser(User user);
    User findUserByName(String name);

    User findUserById(UUID id);
}
