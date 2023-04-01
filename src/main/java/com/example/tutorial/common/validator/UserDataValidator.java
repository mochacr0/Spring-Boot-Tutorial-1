package com.example.tutorial.common.validator;
import com.example.tutorial.common.data.User;
import com.example.tutorial.exception.InvalidDataException;
import com.example.tutorial.exception.ItemNotFoundException;
import com.example.tutorial.service.UserService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class UserDataValidator extends DataValidator<User>{

    @Autowired
    private UserService userService;
    @Override
    protected void validateImpl(User user) {
//        if (StringUtils.isEmpty(user.getName())) {
//            throw new InvalidDataException("User name should be specified");
//        }
        if (user.getId() == null) {
            validateOnCreate(user);
        }
        else {
            validateOnUpdate(user);
        }
        //common validation
        if (user.getName() != null) {
            User existingUserWithGivenName = userService.findUserByName(user.getName());
            if (existingUserWithGivenName != null && !existingUserWithGivenName.getId().equals(user.getId())) {
                throw new InvalidDataException("User with name " + user.getName() + " already exists");
            }
        }
        if (user.getEmail() != null) {
            User existingUserWithGivenEmail = userService.findUserByEmail(user.getEmail());
            if (existingUserWithGivenEmail != null && !existingUserWithGivenEmail.getId().equals(user.getId())) {
                throw new InvalidDataException("User with email " + user.getEmail() + " already exists");
            }
        }
    }

    @Override
    protected void validateOnCreate(User data) {
        super.validateOnCreate(data);
    }

    @Override
    protected void validateOnUpdate(User data) {
        if (data.getId() == null) {
            throw new InvalidDataException("User ID should be specified");
        }
        User existingUser = userService.findUserById(data.getId());
        if (existingUser == null) {
            throw new ItemNotFoundException("User with id [" + data.getId() + "] is not found");
        }
    }
}
