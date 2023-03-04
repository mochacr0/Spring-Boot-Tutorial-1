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
        if (user.getId() != null) {
            User existingUser = userService.findUserById(user.getId());
            if (existingUser == null) {
                throw new ItemNotFoundException("User with id [" + user.getId() + "] is not found");
            }
        }
        else {
            if (user.getPassword() == null) {
                throw new InvalidDataException("Password should be specified");
            }
        }
        if (user.getName() != null) {
            User existingUserWithGivenName = userService.findUserByName(user.getName());
            if (existingUserWithGivenName != null
                    && !existingUserWithGivenName.getId().equals(user.getId())
                    && StringUtils.equals(user.getName(), existingUserWithGivenName.getName())) {
                throw new InvalidDataException("User with name " + user.getName() + " already exists");
            }
        }
    }
}
