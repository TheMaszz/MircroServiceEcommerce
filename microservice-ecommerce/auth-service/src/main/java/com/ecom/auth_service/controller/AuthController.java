package com.ecom.auth_service.controller;


import com.ecom.auth_service.service.AuthService;
import com.ecom.common.bean.ApiResponse;
import com.ecom.common.bean.UserBean;
import com.ecom.common.dto.ResetPasswordDTO;
import com.ecom.common.exception.BaseException;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/apiendpoint/auth")
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

    @PostMapping("/send-token-reset-password")
    public ApiResponse sendTokenResetPassword(@RequestBody UserBean userBean) throws BaseException {
        ApiResponse res = authService.sendTokenResetPassword(userBean);
        return res;
    }

    @PutMapping("/reset-password")
    public ApiResponse resetPassword(@RequestBody ResetPasswordDTO resetPasswordDTO) throws BaseException {
        ApiResponse res = authService.resetPassword(resetPasswordDTO);
        return res;
    }

    @PostMapping("/resend-token-reset-password")
    public ApiResponse resendTokenResetPassword(@RequestBody ResetPasswordDTO resetPasswordDTO) throws BaseException {
        ApiResponse res = authService.resendTokenResetPassword(resetPasswordDTO);
        return res;
    }

    @GetMapping("/test")
    public ApiResponse test(@RequestHeader("X-User-Id") String userId, @RequestHeader("X-Role") String role) throws BaseException {
        ApiResponse res = new ApiResponse();

        res.setData("User ID: " + userId + ", Role: " + (role != null ? role : "No role provided"));

        return res;
    }


}
