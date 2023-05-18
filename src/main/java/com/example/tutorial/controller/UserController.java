package com.example.tutorial.controller;

import com.example.tutorial.common.data.PageData;
import com.example.tutorial.common.data.PageParameter;
import com.example.tutorial.common.data.RegisterUserRequest;
import com.example.tutorial.common.data.User;
import com.example.tutorial.exception.InvalidDataException;
import com.example.tutorial.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;
import org.springframework.security.core.parameters.P;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

import static com.example.tutorial.controller.ControllerConstants.*;

@Slf4j
@RestController
@RequestMapping("/users")
@Tag(name = "User", description = "User-related APIs")
public class UserController {
    @Autowired
    private UserService userService;

    @Operation(tags = {"User"}, summary = "Returns a page of available users")
    @GetMapping(value = "")
    PageData<User> getUsers (@Parameter(description = PAGE_NUMBER_DESCRIPTION)
                               @RequestParam(defaultValue = PAGE_NUMBER_DEFAULT_STRING_VALUE) int page,
                               @Parameter(description = PAGE_SIZE_DESCRIPTION)
                               @RequestParam(defaultValue = PAGE_SIZE_DEFAULT_STRING_VALUE) int pageSize,
                               @Parameter(description = SORT_ORDER_DESCRIPTION,
                                          examples = {@ExampleObject(name = "asc (Ascending)", value = "asc"),
                                                      @ExampleObject(name = "desc (Descending)", value = "desc")})
                               @RequestParam(defaultValue = SORT_DIRECTION_DEFAULT_VALUE) String sortDirection,
                               @Parameter(description = SORT_PROPERTY_DESCRIPTION)
                               @RequestParam(defaultValue = SORT_PROPERTY_DEFAULT_VALUE) String sortProperty)  {
        //no validate sortOrder
        return userService.findUsers(new PageParameter(page, pageSize, sortDirection, sortProperty, ""));
    }

    @Operation(tags = {"User"}, summary = "Fetch the User object based on the provided userId")
    @GetMapping(value = "/{userId}")
    User getUserById (@Parameter(description = "A string value representing the user id", required = true)
                      @PathVariable(name = "userId") String userId) {
        if (StringUtils.isEmpty(userId)) {
            throw new InvalidDataException("User ID should be specified");
        }
        return userService.findById(UUID.fromString(userId));
    }

    @Operation(tags = {"User"}, summary = "Update user", description = "Update the User. " +
                                                                        "Specify existing User Id to update user. " +
                                                                        "Referencing non-existing User Id will cause 'Not Found' error.")
    @PostMapping(value = "/update")
    User saveUser(@io.swagger.v3.oas.annotations.parameters.RequestBody(content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE), description = "User payload to update")
                  @RequestBody User user) {
        return userService.save(user);
    }

    @Operation(tags = {"User"}, summary = "Register new user", description = "Register new user. " +
                                                                             "When creating user, platform generates User Id as time-based UUID. " +
                                                                             "The newly created User Id will be present in the response. ")
    @PostMapping(value = "/register")
    User regsiterUser(@io.swagger.v3.oas.annotations.parameters.RequestBody(description = "User registration payload",
                                                                            content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE))
                      @RequestBody RegisterUserRequest registerUserRequest,
                      @Parameter(description = "A boolean indicates whether or not mail verification is required.")
                      @RequestParam(defaultValue = "false") boolean isMailRequired,
                      HttpServletRequest request) {
        return userService.register(registerUserRequest, request, isMailRequired);
    }

    @Operation(tags = {"User"}, summary = "Delete the User specified by userId and its credentials. A non-existent User Id will result in an error.")
    @DeleteMapping(value = "/{userId}")
    void deleteUser(@Parameter(description = "A string value representing the user id", required = true)
                    @PathVariable(name = "userId") String userId) {
        if (StringUtils.isEmpty(userId)) {
            throw new InvalidDataException("User ID should be specified");
        }
        userService.deleteById(UUID.fromString(userId));
    }

    @DeleteMapping(value = "/test-delete")
    void deleteUnverifiedUsers() {
        userService.deleteUnverifiedUsers();
    }

    @PostMapping(value = "/{userId}/activate")
    void activateUserCredentialsByUserId(@PathVariable(name = "userId") String userId) {
        userService.activateUserCredentialsByUserId(UUID.fromString(userId));
    }

}
