package com.example.tutorial.security;

import io.micrometer.common.util.StringUtils;
import jakarta.servlet.http.HttpServletRequest;
import lombok.Data;
import org.springframework.http.HttpHeaders;


@Data
public class RestAuthenticationDetails {
    private String clientIpAddress;
    private String getClientIpAddressFromRequest (HttpServletRequest request) {
        String forwardedHeader = request.getHeader("X-Forwarded-For");
        if (StringUtils.isNotEmpty(forwardedHeader)) {
            return forwardedHeader.split(",")[0];
        }
        return request.getRemoteAddr();
    }
    public RestAuthenticationDetails(HttpServletRequest request) {
        this.clientIpAddress = getClientIpAddressFromRequest(request);
    }
}
