package com.ecom.authentication_service.exception;

public class AuthException extends BaseException {

    public AuthException(String code, String message) {
        super("auth." + code, message);
    }

    public AuthException(String code, String message, Throwable cause) {
        super("auth." + code, message, cause);
    }
}
