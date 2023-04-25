package com.example.tutorial.controller;

import com.example.tutorial.common.data.RegisterUserRequest;
import com.example.tutorial.common.data.User;
import com.example.tutorial.common.data.UserCredentials;
import com.example.tutorial.service.MailService;
import com.example.tutorial.service.UserCredentialsService;
import com.example.tutorial.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/test")
public class TestController {
    @Autowired
    MailService mailService;
    @Autowired
    UserCredentialsService userCredentialsService;
    @Autowired
    UserService userService;

    @Operation(tags = {"Testing"}, summary = "Test send email", description = "Test send email")
    @RequestMapping(value = "/sendMail", method = RequestMethod.GET)
    String testSendMail () {
        try {
            mailService.sendMail("mochacr0@gmail.com", "nthai2001cr@gmail.com", "Cúc cu", "Cu cúc");
        }
        catch(Exception e) {
            throw e;
        }
        return "OK";
    }

    @Operation(tags = {"Testing"}, summary = "Test send template email", description = "Test send email")
    @RequestMapping(value = "/sendTemplate", method = RequestMethod.GET)
    String testSendTemplateMail () {
        try {
            mailService.sendTemplateMail();
        }
        catch(Exception e) {
            throw e;
        }
        return "OK";
    }

    @RequestMapping(value = "/find-unverified-users", method = RequestMethod.GET)
    List<UserCredentials> testFindUnverifiedUserCredentials () {
        return userCredentialsService.findUnverifiedUserCredentials();
    }

    @RequestMapping(value = "/delete-unverified-users", method = RequestMethod.DELETE)
    void testDeleteUnverifiedUserCredentials () {
        userService.deleteUnverifiedUsers();
    }
}
