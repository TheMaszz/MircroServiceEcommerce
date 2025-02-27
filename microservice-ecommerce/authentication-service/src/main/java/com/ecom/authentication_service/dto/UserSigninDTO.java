package com.ecom.authentication_service.dto;

import lombok.Data;

@Data
public class UserSigninDTO {
   private Integer id;
   private String username;
   private String email;
   private String token;
}
