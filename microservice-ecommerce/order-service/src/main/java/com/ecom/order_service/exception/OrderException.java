package com.ecom.order_service.exception;

public class OrderException extends BaseException {

    public OrderException(String code, String message) {
        super(code, message);
    }

    public OrderException(String code, String message, Throwable cause) {
        super(code, message, cause);
    }
}
