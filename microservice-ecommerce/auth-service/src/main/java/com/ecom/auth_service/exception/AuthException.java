package com.ecom.auth_service.exception;

import com.ecom.common.exception.BaseException;

public class AuthException extends BaseException {

    public AuthException(String code, String message) {
        super("auth." + code, message);
    }

    public AuthException(String code, String message, Throwable cause) {
        super("auth." + code, message, cause);
    }
}
