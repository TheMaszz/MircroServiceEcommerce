package com.ecom.order_service.exception;

import com.ecom.common.exception.BaseException;

public class OrderException extends BaseException {

    public OrderException(String code, String message) {
        super("order." + code, message);
    }

    public OrderException(String code, String message, Throwable cause) {
        super("order." + code, message, cause);
    }
}
