package com.ecom.common.dto;

import lombok.Data;

@Data
public class UserSigninDTO {
   private Long id;
   private String username;
   private String email;
   private Integer role;
   private String token;
}
