package com.example.tutorial.security;

import com.example.tutorial.common.data.User;
import com.example.tutorial.common.security.SecurityUser;
import com.example.tutorial.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.util.Assert;

@Component
@Slf4j
public class RestAuthenticationProvider implements AuthenticationProvider {
    @Autowired
    private UserService userService;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Override
    public Authentication authenticate(Authentication authentication) throws AuthenticationException {
        Assert.notNull(authentication, "No authentication provided");
        String username = authentication.getPrincipal().toString();
        String password = authentication.getCredentials().toString();
        User existingUser = userService.findUserByName(username);
        if (existingUser == null) {
            throw new UsernameNotFoundException("User not found: " + username);
        }
        //authenticate with password
        boolean isPasswordMatched = passwordEncoder.matches(password, existingUser.getPassword());
        if (!isPasswordMatched) {
            throw new BadCredentialsException("Authentication failed: Username or password not valid");
        }
        //password matched
        SecurityUser securityUser = new SecurityUser(existingUser);
        return new UsernamePasswordAuthenticationToken(securityUser, null, securityUser.getAuthorities());
    }

    @Override
    public boolean supports(Class<?> authentication) {
        return UsernamePasswordAuthenticationToken.class.isAssignableFrom(authentication);
    }
}
