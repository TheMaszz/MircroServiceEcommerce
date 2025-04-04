package com.ecom.common.controller;

import com.ecom.common.bean.ApiResponse;
import com.ecom.common.exception.BaseException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

@RestControllerAdvice
public class BaseController {

    protected ApiResponse checkException(Exception e, ApiResponse res)  {
        if (e.getMessage().equals("Unauthorized")) {
            res.setError("UNAUTHORIZED", "Authentication required", "40100", 401);
        } else if (e.getMessage().contains("Duplicate")) {
            res.setError("DUPLICATE_ENTRY", "Duplicated, " + e.getCause(), "40900", 409);
        } else {
            res.setError("INTERNAL_SERVER_ERROR", e.getMessage());
        }
        return res;
    }

//    @ExceptionHandler(BaseException.class)
//    public ResponseEntity<ApiResponse> handleBaseException(BaseException e) {
//        ApiResponse response = new ApiResponse();
//        response.setError(e.getCode(), e.getMessage());
//
//        // Return appropriate HTTP status based on error code
//        HttpStatus status = switch (e.getCode()) {
//            case "UNAUTHORIZED" -> HttpStatus.UNAUTHORIZED;
//            case "DUPLICATE_ENTRY" -> HttpStatus.CONFLICT;
//            case "NOT_FOUND" -> HttpStatus.NOT_FOUND;
//            default -> HttpStatus.INTERNAL_SERVER_ERROR;
//        };
//
//        return new ResponseEntity<>(response, status);
//    }

    protected HashMap<String, Object> pagination(
            int page_number,
            int page_size,
            String sorting,
            String sort_type,
            HashMap<String, Object> params
    ) throws Exception {

        List<String> allowedSortingColumns = Arrays.asList("ASC", "DESC");
        if (!allowedSortingColumns.contains(sort_type.toUpperCase())) {
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
