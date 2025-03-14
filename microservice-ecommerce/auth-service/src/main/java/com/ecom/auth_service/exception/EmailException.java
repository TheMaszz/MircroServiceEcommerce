package com.ecom.auth_service.exception;

public class EmailException extends BaseException {
    public EmailException(String code, String message) {
        super("email." + code, message);
    }

    public EmailException(String code, String message, Throwable cause) {
        super("email." + code, message, cause);
    }
}
