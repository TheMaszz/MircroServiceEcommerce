package com.ecom.common.dto;

import lombok.Data;

@Data
public class ChangePasswordDTO {
    private String old_password;
    private String new_password;
}
