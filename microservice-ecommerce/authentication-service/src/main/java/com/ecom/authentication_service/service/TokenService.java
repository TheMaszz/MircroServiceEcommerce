package com.ecom.authentication_service.service;

import java.util.Calendar;
import java.util.Date;
import java.util.concurrent.ConcurrentHashMap;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.ecom.authentication_service.bean.UserBean;

@Service
public class TokenService {

    private final ConcurrentHashMap<String, Date> blacklistedTokens = new ConcurrentHashMap<>();

    @Value("${token.secret}")
    private String secret;

    @Value("${token.issuer}")
    private String issuer;

    public Algorithm algorithm() {
        return Algorithm.HMAC256(secret);
    }

    public String tokenize(UserBean userBean) {

        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.MINUTE, 60);
        Date expireAt = calendar.getTime();

        return JWT.create()
                .withIssuer(issuer)
                .withClaim("principal", userBean.getId())
                .withClaim("role", "USER")
                .withExpiresAt(expireAt)
                .sign(algorithm());
    }

    public DecodedJWT verify(String token) {
        try {
            if (isTokenBlacklisted(token)) {
                throw new RuntimeException("Token is blacklisted");
            }
            Algorithm algorithm = algorithm();
            JWTVerifier verifier = JWT.require(algorithm).withIssuer(issuer).build();
            return verifier.verify(token);
        } catch (Exception e) {
            return null;
        }
    }

    private boolean isTokenBlacklisted(String token) {
        Date expiryDate = blacklistedTokens.get(token);
        if (expiryDate != null) {
            if (expiryDate.before(new Date())) {
                blacklistedTokens.remove(token);
                return false;
            }
            return true;
        }
        return false;
    }

    public void invalidateToken(String token) {
        DecodedJWT decodedJWT = verify(token);
        if (decodedJWT != null) {
            blacklistedTokens.put(token, decodedJWT.getExpiresAt());
        }
    }

}
