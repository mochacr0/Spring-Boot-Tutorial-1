package com.example.tutorial.common.validator;
import com.example.tutorial.common.data.User;
import com.example.tutorial.exception.InvalidDataException;
import com.example.tutorial.exception.ItemNotFoundException;
import com.example.tutorial.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class UserDataValidator extends DataValidator<User>{

    @Autowired
    private UserService userService;

    @Override
    protected void validateOnCreateImpl(User data) {
        if (data.getId() != null) {
            throw new InvalidDataException(String.format("Cannot create user with id [%s]", data.getId()));
        }
    }

    @Override
    protected void validateOnUpdateImpl(User data) {
        if (data.getId() == null) {
            throw new InvalidDataException("User ID should be specified");
        }
        User existingUser = userService.findById(data.getId());
        if (existingUser == null) {
            throw new ItemNotFoundException(String.format("User with id [%s] is not found", data.getId()));
        }
    }

    @Override
    protected void validateCommon(User data) {
        if (data.getName() != null) {
            User existingUserWithGivenName = userService.findByName(data.getName());
            if (existingUserWithGivenName != null && !existingUserWithGivenName.getId().equals(data.getId())) {
                throw new InvalidDataException(String.format("User with name [%s] already exists", data.getName()));
            }
        }
        if (data.getEmail() != null) {
            User existingUserWithGivenEmail = userService.findByEmail(data.getEmail());
            if (existingUserWithGivenEmail != null && !existingUserWithGivenEmail.getId().equals(data.getId())) {
                throw new InvalidDataException(String.format("User with email [%s] already exists", data.getEmail()));
            }
        }
    }
}
