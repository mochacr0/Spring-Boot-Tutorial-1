package com.example.tutorial.security;

import com.example.tutorial.common.security.Authority;
import com.example.tutorial.common.security.JwtAuthenticationToken;
import com.example.tutorial.common.security.SecurityUser;
import com.example.tutorial.exception.ExpiredJwtTokenException;
import io.jsonwebtoken.*;
import io.jsonwebtoken.security.SignatureException;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.stereotype.Component;

import java.sql.Date;
import java.time.ZonedDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Component
@Slf4j
public class JwtTokenFactory {
    @Autowired
    private JwtSettings jwtSettings;

    @Autowired
    private JwtTokenExtractor jwtTokenExtractor;
    private String SCOPES = "scopes";

    public JwtToken createAccessToken(SecurityUser securityUser) {
        JwtBuilder jwtBuilder = setUpToken(securityUser);
        String jwtToken = jwtBuilder.compact();
        return new JwtAccessToken(jwtToken);
    }

    private JwtBuilder setUpToken(SecurityUser securityUser) {
        if (StringUtils.isEmpty(securityUser.getName())) {
            throw new IllegalArgumentException("Cannot create JWT token with empty username");
        }
        //scopes ?
        List<String> scopes = securityUser.getAuthorities().stream().map(GrantedAuthority::getAuthority).collect(Collectors.toList());
        Claims claims = Jwts.claims().setSubject(securityUser.getName());
        claims.put(SCOPES, scopes);
        ZonedDateTime currentTime = ZonedDateTime.now();
        return Jwts.builder()
                .setClaims(claims)
                .setIssuedAt(Date.from(currentTime.toInstant()))
                .setExpiration(Date.from(currentTime.plusSeconds(jwtSettings.getTokenExpiryTime()).toInstant()))
                .signWith(jwtSettings.getTokenSigningKey(), SignatureAlgorithm.HS256);
    }

    public SecurityUser parseJwtToken(String jwtToken) {
        Claims claims = parseJwtTokenClaims(jwtToken);
        //set up new SecurityUser;
        String subject = claims.getSubject();
        List<String> scopes = claims.get(SCOPES, List.class);
        SecurityUser securityUser = new SecurityUser();
        securityUser.setName(subject);
        securityUser.setAuthority(Authority.parseFromString(scopes.get(0)));
        return securityUser;
    }

    private Claims parseJwtTokenClaims(String jwtToken) {
        log.info(this.jwtSettings.getTokenSigningKeyString());
        log.info(this.jwtSettings.getTokenSigningKey().toString());
        try {
            return Jwts
                    .parserBuilder()
                    .setSigningKey(this.jwtSettings.getSigningKey())
                    .build()
                    .parseClaimsJws(jwtToken)
                    .getBody();
        }
        catch (UnsupportedJwtException | MalformedJwtException | SignatureException | IllegalArgumentException exception) {
            throw new BadCredentialsException("Invalid JWT token: " + exception.getMessage());
        }
        catch (ExpiredJwtException exception) {
            throw new ExpiredJwtTokenException("Expired JWT token");
        }
    }

}
