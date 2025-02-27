package com.ecom.authentication_service.bean;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import lombok.Data;

@Data
public class ApiResponse {
    private String response_ref;
    private String response_datetime;
    private String status;
    private String errorCode;
    private String response_code = "20000";
    private String response_desc = "success";
    private Object data;
    private Paginate paginate = new Paginate();

    public ApiResponse() {
		this.response_datetime = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
		this.status = "success"; 
	}

    public void setError(String errorCode, String response_desc) {
        this.errorCode = errorCode;
        this.response_code = "50000";
        this.response_desc = response_desc;
        this.status = "error";
    }
}
