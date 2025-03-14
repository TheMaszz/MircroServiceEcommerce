package com.ecom.auth_service.bean;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import lombok.Data;

import java.util.Date;

@Data
@JsonInclude(Include.NON_NULL)
public class UserBean {
    private Long id;
    private String username;
    private String email;
    private String password;
    private String profile_url;
    private Integer role;
    private Date created_at;
    private Date updated_at;
    private Date last_login;
    private String token_reset_password;
    private Date token_reset_password_expired;

    private String usernameOrEmail;
}