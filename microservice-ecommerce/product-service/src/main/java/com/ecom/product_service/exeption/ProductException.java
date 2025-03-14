package com.ecom.product_service.exeption;

public class ProductException extends BaseException {

    public ProductException(String code, String message) {
        super("product." + code, message);
    }

    public ProductException(String code, String message, Throwable cause) {
        super("product." + code, message, cause);
    }
}
