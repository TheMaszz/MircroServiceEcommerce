package com.ecom.payment_service.exception;

import com.ecom.common.exception.BaseException;

public class PaymentException extends BaseException {
    public PaymentException(String code, String message) {
        super("payment." + code, message);
    }

    public PaymentException(String code, String message, Throwable cause) {
        super("payment." + code, message, cause);
    }
}
