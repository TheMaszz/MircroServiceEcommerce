package com.ecom.auth_service.util;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.security.Key;
import java.util.Date;
import java.util.UUID;

@Component
public class JwtUtil {

    private static final long EXPIRATION_TIME = 1000 * 60 * 60;
    private final Key key;

    public JwtUtil(@Value("${app.security.secret-key}") String secretKey){
        this.key = Keys.hmacShaKeyFor(Decoders.BASE64.decode(secretKey));
    }

    public String generateToken(String role, Long userId) {
        return Jwts.builder()
                .subject(String.valueOf(userId))
                .claim("role", role)
                .issuedAt(new Date())
                .expiration(new Date(System.currentTimeMillis() + EXPIRATION_TIME))
                .signWith(key)
                .compact();
    }

    public String generatePasswordResetToken(String userId) {
        String tokenId = UUID.randomUUID().toString();

        return Jwts.builder()
                .id(tokenId)
                .subject(userId)
                .issuedAt(new Date())
                .claim("purpose", "reset_password")
                .signWith(key)
                .compact();
    }

}
