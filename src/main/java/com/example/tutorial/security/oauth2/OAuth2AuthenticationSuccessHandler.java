package com.example.tutorial.security.oauth2;

import com.example.tutorial.common.data.User;
import com.example.tutorial.common.security.SecurityUser;
import com.example.tutorial.security.JwtAccessToken;
import com.example.tutorial.security.JwtToken;
import com.example.tutorial.security.JwtTokenFactory;
import com.example.tutorial.service.UserService;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.client.OAuth2AuthorizeRequest;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClient;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.security.oauth2.client.web.AuthorizationRequestRepository;
import org.springframework.security.oauth2.client.web.OAuth2AuthorizedClientRepository;
import org.springframework.security.oauth2.core.endpoint.OAuth2AuthorizationRequest;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.Map;

@Slf4j
@Component(value = "OAuth2AuthenticationSuccessHandler")
public class OAuth2AuthenticationSuccessHandler extends SimpleUrlAuthenticationSuccessHandler {

    @Autowired
    private OAuth2AuthorizedClientRepository authorizedClientRepository;
    @Autowired
    private JwtTokenFactory jwtTokenFactory;
    @Autowired
    private UserService userService;
    @Autowired
    private HttpCookieOAuth2AuthorizationRequestRepository authorizationRequestRepository;

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException, ServletException {
        try {
            OAuth2AuthenticationToken authenticationToken = (OAuth2AuthenticationToken)authentication;
            OAuth2User oauth2User = authenticationToken.getPrincipal();
            SecurityUser securityUser = getOrCreateSecurityUserFromOAuth2User(oauth2User, authenticationToken.getAuthorizedClientRegistrationId());
            JwtToken jwtAccessToken = this.jwtTokenFactory.createAccessToken(securityUser);
            this.getRedirectStrategy().sendRedirect(request, response,  "/?accessToken=" + jwtAccessToken.getValue());
        }
        catch(Exception e) {
            log.debug("Error occurred during processing authentication success result. " +
                    "request [{}], response [{}], authentication [{}]", request, response, authentication, e);
            clearAuthenticationAttributes(request, response);
            String errorPath = "/oauth2?error=" + URLEncoder.encode(e.getMessage(), StandardCharsets.UTF_8);
            this.getRedirectStrategy().sendRedirect(request, response, errorPath);
        }
    }

    private SecurityUser getOrCreateSecurityUserFromOAuth2User(OAuth2User oauth2User, String registrationId) {
        OAuth2UserInfo oauth2UserInfo = OAuth2UserInfoMapper.getOAuth2UserInfo(oauth2User.getAttributes(), registrationId);
        User user = userService.findUserByEmail(oauth2UserInfo.getEmail());
        if (user == null) {
            user = new User(oauth2UserInfo);
            userService.saveUser(user);
        }
        SecurityUser securityUser = new SecurityUser(user);
        return securityUser;
    }
    protected void clearAuthenticationAttributes(HttpServletRequest request, HttpServletResponse response) {
        super.clearAuthenticationAttributes(request);
    }

}
