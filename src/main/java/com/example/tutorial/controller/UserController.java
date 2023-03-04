package com.example.tutorial.controller;

import com.example.tutorial.common.data.PageData;
import com.example.tutorial.common.data.PageParameter;
import com.example.tutorial.common.data.User;
import com.example.tutorial.service.UserService;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import static com.example.tutorial.controller.ControllerConstants.*;

@Slf4j
@RestController
@Tag(name = "User", description = "User-related APIs")
public class UserController {
    @Autowired
    private UserService userService;

    @Operation(tags = {"User"}, summary = "Returns a page of available users")
    @RequestMapping(value = "/users", method = RequestMethod.GET)
    PageData<User> getAllUsers(@Parameter(description = PAGE_NUMBER_DESCRIPTION)
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
    @Operation(tags = {"User"}, summary = "Save or Update user", description = "Create or update the User. When creating user, platform generates User Id as time-based UUID. " +
                                                                                "The newly created User Id will be present in the response. " +
                                                                                "Specify existing User Id to update user. " +
                                                                                "Referencing non-existing User Id will cause 'Not Found' error.")
    @RequestMapping(value = "/user", method = RequestMethod.POST)
    User saveUser(@io.swagger.v3.oas.annotations.parameters.RequestBody(content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE))
                  @RequestBody User user) {
        return userService.saveUser(user);
    }

}
