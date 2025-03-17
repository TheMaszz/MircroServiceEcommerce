package com.ecom.user_service.bean;

import lombok.Data;

@Data
public class UserBean {
    private Long id;
    private String username;
    private String email;
    private String profile_url;
    private Long role;
    private String created_at;
    private String updated_at;
    private Long updated_by;
    private String last_login;
}
