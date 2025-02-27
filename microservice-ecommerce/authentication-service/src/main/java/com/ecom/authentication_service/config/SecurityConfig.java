package com.ecom.authentication_service.config;

import java.util.Collections;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import com.ecom.authentication_service.config.token.TokenFilter;
import com.ecom.authentication_service.service.TokenService;

@Configuration
@EnableWebSecurity
public class SecurityConfig {
    private final TokenService tokenService;
    
    public SecurityConfig(TokenService tokenService) {
        this.tokenService = tokenService;
    }
    
    private final String[] PUBLIC = {
        "/apienpoint/auth/signin",
        "/apienpoint/auth/signup",
        "/apienpoint/auth/signout",
        "/apienpoint/auth/send-token-reset-password",
        "/apienpoint/auth/resend-token-reset-password",
        "/apienpoint/auth/reset-password",
        "/actuator/**",
        "/images/**",
        "/socket/**"
    };
    
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http.cors(config -> {
                CorsConfiguration cors = new CorsConfiguration();
                cors.setAllowCredentials(true);
                cors.setAllowedOriginPatterns(Collections.singletonList("http://*"));
                cors.addAllowedHeader("*");
                cors.addAllowedMethod("GET");
                cors.addAllowedMethod("POST");
                cors.addAllowedMethod("PUT");
                cors.addAllowedMethod("DELETE");
                cors.addAllowedMethod("OPTIONS");
                UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
                source.registerCorsConfiguration("/**", cors);
                
                config.configurationSource(source);
            })
            .csrf(AbstractHttpConfigurer::disable)
            .authorizeHttpRequests(auth -> auth
                .requestMatchers(PUBLIC)
                .permitAll()
                .anyRequest()
                .authenticated())
            .addFilterBefore(new TokenFilter(tokenService), UsernamePasswordAuthenticationFilter.class);
        
        return http.build();
    }
    
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}