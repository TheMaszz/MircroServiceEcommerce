package com.ecom.gateway.config;

import com.ecom.gateway.filter.JwtAuthFilter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity;
import org.springframework.security.config.web.server.SecurityWebFiltersOrder;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.web.server.SecurityWebFilterChain;
import org.springframework.security.web.server.context.NoOpServerSecurityContextRepository;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.reactive.CorsConfigurationSource;
import org.springframework.web.cors.reactive.UrlBasedCorsConfigurationSource;

import java.util.Collections;

@Configuration
@EnableWebFluxSecurity
public class SecurityConfig {

    public static final String[] PUBLIC = {
            "/apiendpoint/auth/signin",
            "/apiendpoint/auth/signup",
            "/apiendpoint/auth/signout",
            "/apiendpoint/auth/send-token-reset-password",
            "/apiendpoint/auth/reset-password",
            "/apiendpoint/auth/resend-token-reset-password",
            "/apiendpoint/payment/webhook",
            "/actuator/**",
            "/uploads/**"
    };

    @Bean
    public SecurityWebFilterChain securityWebFilterChain(
            ServerHttpSecurity http,
            JwtAuthFilter jwtAuthFilter
    ) {
        return http
                .securityContextRepository(NoOpServerSecurityContextRepository.getInstance())
                .cors(cors -> cors.configurationSource(corsConfigurationSource()))
                .csrf(ServerHttpSecurity.CsrfSpec::disable)
                .httpBasic(ServerHttpSecurity.HttpBasicSpec::disable) // Disable HTTP Basic
                .formLogin(ServerHttpSecurity.FormLoginSpec::disable) // Disable form login
                .authorizeExchange(exchanges -> exchanges
                        .pathMatchers(HttpMethod.OPTIONS).permitAll() // Allow CORS preflight
                        .pathMatchers(PUBLIC).permitAll()
                        .anyExchange().authenticated() // All other paths need auth
                )
                // Add custom filter to handle ALL authentication
                .addFilterAt(jwtAuthFilter, SecurityWebFiltersOrder.AUTHENTICATION)
                .build();
    }

    private CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration cors = new CorsConfiguration();
        cors.setAllowCredentials(true);
        cors.setAllowedOriginPatterns(Collections.singletonList("http://*")); // Allow all origins
        cors.addAllowedHeader("*"); // Allow all headers
        cors.addAllowedMethod("GET");
        cors.addAllowedMethod("POST");
        cors.addAllowedMethod("PUT");
        cors.addAllowedMethod("DELETE");
        cors.addAllowedMethod("OPTIONS");

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", cors); // Apply CORS to all paths
        return source;
    }
}