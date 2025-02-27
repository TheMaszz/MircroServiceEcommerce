package com.ecom.authentication_service.util;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.security.Key;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

@Component
public class JwtUtil {
    private static final String SECRET_KEY = "your-very-secure-secret-key-your-very-secure";
    private static final long EXPIRATION_TIME = 1000 * 60 * 60;
    private final Set<String> tokenBlacklist = new HashSet<>();

    private final Key key = Keys.hmacShaKeyFor(SECRET_KEY.getBytes(StandardCharsets.UTF_8));

    public String generateToken(String username) {
        return Jwts.builder()
                .subject(username)
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

    public Claims parseToken(String token) {
        return Jwts.parser()
                .verifyWith((SecretKey) key)
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }

    public boolean validateToken(String token) {
        try {
            parseToken(token);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    public boolean isTokenBlacklisted(String token) {
        return tokenBlacklist.contains(token);
    }

    public void blacklistToken(String token) {
        tokenBlacklist.add(token);
    }
}
