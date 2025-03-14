package com.ecom.gateway.filter;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.Ordered;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.ReactiveSecurityContextHolder;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextImpl;
import org.springframework.stereotype.Component;
import org.springframework.util.AntPathMatcher;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.server.WebFilter;
import org.springframework.web.server.WebFilterChain;
import reactor.core.publisher.Mono;

import javax.crypto.SecretKey;
import java.security.Key;
import java.util.Arrays;
import java.util.Collections;

import static com.ecom.gateway.config.SecurityConfig.PUBLIC;

@Component
public class JwtAuthFilter implements WebFilter, Ordered {
    private static final Logger log = LoggerFactory.getLogger(JwtAuthFilter.class);
    private final Key key ;
    private final AntPathMatcher pathMatcher = new AntPathMatcher();

    public JwtAuthFilter(@Value("${app.security.secret-key}") String secretKey){
        this.key = Keys.hmacShaKeyFor(Decoders.BASE64.decode(secretKey));
    }

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, WebFilterChain chain) {
        // Clear any existing authentication headers
        ServerHttpRequest cleanRequest = exchange.getRequest().mutate()
                .headers(headers -> {
                    headers.remove("X-User-Id");
                    headers.remove("X-Role");
                })
                .build();

        // Create a clean exchange with the headers removed
        ServerWebExchange cleanExchange = exchange.mutate().request(cleanRequest).build();

        String path = cleanRequest.getPath().toString();
        log.debug("Request path: {}", path);

        if (isPublicPath(path)) {
            log.debug("Skipping authentication for public path: {}", path);
            return chain.filter(cleanExchange);
        }

        // 1. Check Authorization Header
        if (!cleanRequest.getHeaders().containsKey(HttpHeaders.AUTHORIZATION)) {
            log.warn("Missing Authorization Header");
            return onError(cleanExchange, "Missing Authorization Header");
        }

        String authHeader = cleanRequest.getHeaders().getFirst(HttpHeaders.AUTHORIZATION);
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            log.warn("Invalid Authorization Header: {}", authHeader);
            return onError(cleanExchange, "Invalid Authorization Header");
        }

        // 2. Extract and Validate Token
        String token = authHeader.substring(7);
        log.info("Token extracted: {}", token);

        try {
            // Parse the token to extract claims
            Claims claims = Jwts.parser()
                    .verifyWith((SecretKey) key)
                    .build()
                    .parseSignedClaims(token)
                    .getPayload();

            String userId = claims.getSubject();
            String role = claims.get("role", String.class);

            log.info("Token validated. User ID: {}, Role: {}", userId, role);

            // 3. Create Authentication object
            Authentication authentication = new UsernamePasswordAuthenticationToken(
                    userId,
                    null,
                    Collections.emptyList()
            );

            // 4. Create Security Context
            SecurityContext securityContext = new SecurityContextImpl(authentication);

            // 5. Add User ID to Request Headers
            ServerHttpRequest modifiedRequest = cleanRequest.mutate()
                    .header("X-User-Id", userId)
                    .header("X-Role", role)
                    .build();

            ServerWebExchange modifiedExchange = cleanExchange.mutate()
                    .request(modifiedRequest)
                    .build();

            // 6. Use a stateless approach - don't save to session, just set in context
            return chain.filter(modifiedExchange)
                    .contextWrite(ReactiveSecurityContextHolder.withSecurityContext(
                            Mono.just(securityContext)
                    ));

        } catch (Exception e) {
            log.error("Token validation failed: {}", e.getMessage(), e);
            return onError(cleanExchange, "Invalid Token");
        }
    }

    private boolean isPublicPath(String path) {
        return Arrays.stream(PUBLIC)
                .anyMatch(pattern -> pathMatcher.match(pattern, path));
    }

    private Mono<Void> onError(ServerWebExchange exchange, String error) {
        exchange.getResponse().setStatusCode(HttpStatus.UNAUTHORIZED);
        return exchange.getResponse().setComplete();
    }

    @Override
    public int getOrder() {
        return 0;
    }
}