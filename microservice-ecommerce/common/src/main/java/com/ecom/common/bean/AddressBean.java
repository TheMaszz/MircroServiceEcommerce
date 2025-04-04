package com.ecom.common.bean;

import lombok.Data;

@Data
public class AddressBean {
    private Long id;
    private Long user_id;
    private String name;
    private String fullname;
    private String address;
    private String phone;
    private String description;
    private Long default_address;
}
