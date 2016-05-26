package com.sat.config;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtBuilder;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.HashMap;
import java.util.List;
import java.util.stream.Collectors;

public class TokenHandler {

    private final String secret;
    private final UserSecurityService userService;

    public TokenHandler(String secret, UserSecurityService userSecurityService) {
        this.secret = secret;
        this.userService = userSecurityService;
    }

    public User parseUserFromToken(String token) {
        Claims claims = Jwts.parser()
                .setSigningKey(secret)
                .parseClaimsJws(token)
                .getBody();
        UserDetails userDetails = userService.loadUserByUsername(claims.get("id", String.class));
        User user = new User(
                userDetails.getUsername(),
                userDetails.getPassword(),
                userDetails.getAuthorities()
        );
        return user;
    }

    public String createTokenForUser(UserDetails user) {
        JwtBuilder jwtBuilder = Jwts.builder()
                .setSubject(user.getUsername())
                .signWith(SignatureAlgorithm.HS512, secret);


        List<String> authorities = user.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority).collect(Collectors.toList());
        HashMap<String, Object> hashMap = new HashMap<>();
        hashMap.put("id", user.getUsername());
        hashMap.put("authorities", authorities);
        jwtBuilder.setClaims(hashMap);

        return jwtBuilder.compact();
    }
}
