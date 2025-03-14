package com.ecom.auth_service.dto;

import lombok.Data;

@Data
public class UserSigninDTO {
   private Long id;
   private String username;
   private String email;
   private String token;
}
