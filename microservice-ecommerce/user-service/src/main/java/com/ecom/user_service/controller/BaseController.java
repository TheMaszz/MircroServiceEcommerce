package com.ecom.user_service.controller;

import com.ecom.user_service.bean.ApiResponse;
import com.ecom.user_service.exception.BaseException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

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

    protected HashMap<String, Object> pagination(
            int page_number,
            int page_size,
            String sorting,
            String sort_type,
            HashMap<String, Object> params
    ) throws Exception {

        List<String> allowedSortingColumns = Arrays.asList("ASC", "DESC");
        if(!allowedSortingColumns.contains(sort_type.toUpperCase())){
            throw new Exception("Sorting only supports ASC or DESC.");
        }

        int start = 0, end = 0;
        start = ((page_number - 1) * page_size);
        end = page_size;

        params.put("start", start);
        params.put("end", end);
        params.put("sorting", sorting);
        params.put("sort_type", sort_type);
        return params;
    }
}
