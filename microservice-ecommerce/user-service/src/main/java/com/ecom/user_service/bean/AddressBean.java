package com.ecom.user_service.bean;

import lombok.Data;

@Data
public class AddressBean {
    private Long id;
    private String name;
    private String address;
    private String phone;
    private String description;
    private Long user_id;
}
