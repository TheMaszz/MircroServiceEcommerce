package com.ecom.authentication_service.util;

import org.passay.CharacterRule;
import org.passay.EnglishCharacterData;
import org.passay.PasswordGenerator;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.Arrays;
import java.util.List;

public class TokenUtil {

    private TokenUtil() {}

    public static String generateToken() {
        List<CharacterRule> rules = Arrays.asList(
                new CharacterRule(EnglishCharacterData.UpperCase, 10),
                new CharacterRule(EnglishCharacterData.LowerCase, 10),
                new CharacterRule(EnglishCharacterData.Digit, 10)
        );
        PasswordGenerator generator = new PasswordGenerator();

        return generator.generatePassword(30, rules);
    }

    public static String getCurrentUserId() {
        SecurityContext context = SecurityContextHolder.getContext();
        if (context == null) {
            return null;
        }
        Authentication authentication = context.getAuthentication();
        if (authentication == null) {
            return null;
        }
        Object principal = authentication.getPrincipal();
        if (principal == null) {
            return null;
        }
        return (String) principal;
    }


}
