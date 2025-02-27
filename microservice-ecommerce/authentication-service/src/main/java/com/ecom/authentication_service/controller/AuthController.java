package com.ecom.authentication_service.controller;

import com.ecom.authentication_service.bean.ApiResponse;
import com.ecom.authentication_service.bean.UserBean;
import com.ecom.authentication_service.dto.ResetPasswordDTO;
import com.ecom.authentication_service.exception.BaseException;
import com.ecom.authentication_service.service.AuthService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/apienpoint/auth")
public class AuthController {

    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @PostMapping("/signup")
    public ApiResponse signup(@RequestBody UserBean userBean) throws BaseException {
        ApiResponse res = authService.signup(userBean);
        return res;
    }

    @PostMapping("/signin")
    public ApiResponse signin(@RequestBody UserBean userBean) throws BaseException {
        ApiResponse res = authService.signin(userBean);
        return res;
    }

    @PostMapping("/signout")
    public ApiResponse signout(HttpServletRequest req) throws BaseException {
        ApiResponse res = authService.signout(req);
        return res;
    }

    @GetMapping("/validate")
    public ApiResponse validate(@RequestHeader("Authorization") String token) throws  BaseException{
       ApiResponse res = authService.validate(token);
       return res;
    }

    @PostMapping("/send-token-reset-password")
    public ApiResponse sendTokenResetPassword(@RequestBody UserBean userBean) throws BaseException {
        ApiResponse res = authService.sendTokenResetPassword(userBean);
        return res;
    }

    @PutMapping("/reset-password")
    public ApiResponse resetPassword(@RequestBody ResetPasswordDTO resetPasswordDTO) throws BaseException{
        ApiResponse res = authService.resetPassword(resetPasswordDTO);
        return res;
    }

    @PostMapping("/resend-token-reset-password")
    public ApiResponse resendTokenResetPassword(@RequestBody ResetPasswordDTO resetPasswordDTO) throws BaseException{
        ApiResponse res = authService.resendTokenResetPassword(resetPasswordDTO);
        return res;
    }




}
