package com.example.tutorial.controller;

import com.example.tutorial.service.AuthService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/auth")
@Tag(name = "Auth", description = "Authentication/Authorization APIs")
public class AuthController {
    @Autowired
    private AuthService authService;

    @Operation(tags = {"Auth"}, summary = "Activate user")
    @RequestMapping(value = "/activate", method = RequestMethod.POST)
    void activateToken(
            @Parameter(description = "Activation token retrieved from email")
            @RequestParam String activationToken) {
        authService.activateEmail(activationToken);
    }

    @Operation(tags = {"Auth"}, summary = "Resend activation token")
    @RequestMapping(value = "/activate/resend", method = RequestMethod.POST)
    void resendActivationToken(@io.swagger.v3.oas.annotations.parameters.RequestBody(content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE))
                               @RequestBody String email, HttpServletRequest request) {
        authService.resendActivationTokenByEmail(email, request);
    }


}
