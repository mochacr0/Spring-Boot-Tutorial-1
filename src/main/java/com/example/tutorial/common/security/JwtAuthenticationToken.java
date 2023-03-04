package com.example.tutorial.common.security;

import lombok.Getter;
import lombok.Setter;
import org.springframework.security.authentication.AbstractAuthenticationToken;

@Getter
@Setter
public class JwtAuthenticationToken extends AbstractAuthenticationToken {
    private String unsafeToken;
    private SecurityUser securityUser;

    public JwtAuthenticationToken(String unsafeToken) {
        super(null);
        setUnsafeToken(unsafeToken);
    }

    public JwtAuthenticationToken(SecurityUser securityUser) {
        super(securityUser.getAuthorities());
        this.securityUser = securityUser;
        setAuthenticated(true);
    }

    @Override
    public Object getCredentials() {
        return this.unsafeToken;
    }

    @Override
    public Object getPrincipal() {
        return this.securityUser;
    }
}
