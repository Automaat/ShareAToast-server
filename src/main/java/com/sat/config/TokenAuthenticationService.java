package com.sat.config;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.User;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class TokenAuthenticationService {

    private static final String AUTH_HEADER_NAME = "X-AUTH-TOKEN";

    private final TokenHandler tokenHandler;

    public TokenAuthenticationService(String secret, UserSecurityService userSecurityService) {
        tokenHandler = new TokenHandler(secret, userSecurityService);
    }

    public void addAuthentication(HttpServletResponse response, UserAuthentication userAuthentication) {
        final User user = userAuthentication.getDetails();
        response.addHeader("Access-Control-Expose-Headers", "X-AUTH-TOKEN");
        response.addHeader(AUTH_HEADER_NAME, tokenHandler.createTokenForUser(user));
    }

    public Authentication getAuthentication(HttpServletRequest request) {
        final String token = request.getHeader(AUTH_HEADER_NAME);
        if (token != null && !"null".equals(token)) {
            final User user = tokenHandler.parseUserFromToken(token);
            if (user != null) {
                return new UserAuthentication(user);
            }
        }
        return null;
    }
}
