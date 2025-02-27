package com.ecom.authentication_service.bean;

import java.util.Date;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

import lombok.Data;

@Data
@JsonInclude(Include.NON_NULL)
public class UserBean {
    private Integer id;
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