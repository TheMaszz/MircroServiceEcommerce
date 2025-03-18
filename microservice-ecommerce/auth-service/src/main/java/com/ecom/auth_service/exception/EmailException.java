package com.ecom.auth_service.exception;

import com.ecom.common.exception.BaseException;

public class EmailException extends BaseException {
    public EmailException(String code, String message) {
        super("email." + code, message);
    }

    public EmailException(String code, String message, Throwable cause) {
        super("email." + code, message, cause);
    }
}
