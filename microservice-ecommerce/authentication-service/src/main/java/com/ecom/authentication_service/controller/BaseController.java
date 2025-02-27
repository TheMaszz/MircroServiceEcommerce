package com.ecom.authentication_service.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import com.ecom.authentication_service.bean.ApiResponse;
import com.ecom.authentication_service.exception.BaseException;

@RestControllerAdvice
public class BaseController {
    protected void checkException(Exception e, ApiResponse res) throws BaseException {
        if (e instanceof BaseException) {
            BaseException be = (BaseException) e;
            res.setError(be.getCode(), be.getMessage());
            throw be; // Re-throw specific BaseException
        } else {
            res.setError("INTERNAL_SERVER_ERROR", e.getMessage());
            throw new BaseException("INTERNAL_SERVER_ERROR", e.getMessage()) {
            };
        }
    }

    @ExceptionHandler(BaseException.class)
    public ResponseEntity<ApiResponse> handleBaseException(BaseException e) {
        ApiResponse response = new ApiResponse();
        response.setError(e.getCode(), e.getMessage());
        return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
    }
}
