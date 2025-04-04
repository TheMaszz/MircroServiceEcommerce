package com.ecom.auth_service.controller;


import com.ecom.auth_service.service.AuthService;
import com.ecom.common.bean.ApiResponse;
import com.ecom.common.bean.UserBean;
import com.ecom.common.dto.ResetPasswordDTO;
import com.ecom.common.exception.BaseException;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/apiendpoint/auth")
public class AuthController {

    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @PostMapping("/signup")
    public ResponseEntity<ApiResponse> signup(@RequestBody UserBean userBean) throws BaseException {
        ApiResponse res = authService.signup(userBean);
        return ResponseEntity.status(res.getStatus()).body(res);
    }

    @PostMapping("/signin")
    public ResponseEntity<ApiResponse> signin(@RequestBody UserBean userBean) throws BaseException {
        ApiResponse res = authService.signin(userBean);
        return ResponseEntity.status(res.getStatus()).body(res);
    }

    @PostMapping("/signout")
    public ResponseEntity<ApiResponse> signout(HttpServletRequest req) throws BaseException {
        ApiResponse res = authService.signout(req);
        return ResponseEntity.status(res.getStatus()).body(res);
    }

    @PostMapping("/send-token-reset-password")
    public ResponseEntity<ApiResponse> sendTokenResetPassword(@RequestBody UserBean userBean) throws BaseException {
        ApiResponse res = authService.sendTokenResetPassword(userBean);
        return ResponseEntity.status(res.getStatus()).body(res);
    }

    @PutMapping("/reset-password")
    public ResponseEntity<ApiResponse> resetPassword(@RequestBody ResetPasswordDTO resetPasswordDTO) throws BaseException {
        ApiResponse res = authService.resetPassword(resetPasswordDTO);
        return ResponseEntity.status(res.getStatus()).body(res);
    }

    @PostMapping("/resend-token-reset-password")
    public ResponseEntity<ApiResponse> resendTokenResetPassword(@RequestBody ResetPasswordDTO resetPasswordDTO) throws BaseException {
        ApiResponse res = authService.resendTokenResetPassword(resetPasswordDTO);
        return ResponseEntity.status(res.getStatus()).body(res);
    }

    @GetMapping("/test")
    public ResponseEntity<ApiResponse> test(@RequestHeader("X-User-Id") String userId, @RequestHeader("X-Role") String role)  {
        ApiResponse res = new ApiResponse();

        res.setData("User ID: " + userId + ", Role: " + (role != null ? role : "No role provided"));

        return ResponseEntity.status(res.getStatus()).body(res);
    }


}
