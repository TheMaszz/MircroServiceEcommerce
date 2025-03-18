package com.ecom.common.bean;

import lombok.Data;

@Data
public class EmailRequest {
    private String to;
    private String content;
    private String subject;
}
