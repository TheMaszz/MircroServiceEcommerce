package com.ecom.authentication_service.service;

import ch.qos.logback.core.util.StringUtil;
import com.ecom.authentication_service.bean.ApiResponse;
import com.ecom.authentication_service.bean.UserBean;
import com.ecom.authentication_service.controller.BaseController;
import com.ecom.authentication_service.dto.ResetPasswordDTO;
import com.ecom.authentication_service.dto.UserSigninDTO;
import com.ecom.authentication_service.exception.AuthException;
import com.ecom.authentication_service.exception.BaseException;
import com.ecom.authentication_service.repository.UserRepository;
import com.ecom.authentication_service.util.JwtUtil;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestHeader;

import java.util.*;


@Service
public class AuthService extends BaseController {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final EmailService emailService;
    private final JwtUtil jwtUtil;


    public AuthService(UserRepository userRepository, PasswordEncoder passwordEncoder, EmailService emailService, JwtUtil jwtUtil) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.emailService = emailService;
        this.jwtUtil = jwtUtil;
    }

    public ApiResponse signup(UserBean userBean) throws BaseException {
        ApiResponse res = new ApiResponse();
        try {
            UserBean existingUserByUsername = userRepository.findByUsername(userBean.getUsername());
            if (existingUserByUsername != null) {
                throw new AuthException("duplicate.username", "username has already been taken");
            }

            UserBean existingUserByEmail = userRepository.findByEmail(userBean.getEmail());
            if (existingUserByEmail != null) {
                throw new AuthException("duplicate.email", "email has already been taken");
            }

            String encodePassword = passwordEncoder.encode(userBean.getPassword());
            userBean.setPassword(encodePassword);
            userRepository.createUser(userBean);
        } catch (Exception e) {
            this.checkException(e, res);
        }
        return res;
    }

    public ApiResponse signin(UserBean userBean) throws BaseException {
        ApiResponse res = new ApiResponse();
        try {
            UserBean user;
            String usernameOrEmail = userBean.getUsernameOrEmail();
            boolean isEmail = isEmail(usernameOrEmail);

            if (isEmail) {
                user = userRepository.findByEmail(usernameOrEmail);
            } else {
                user = userRepository.findByUsername(usernameOrEmail);
            }

            if (!matchPassword(userBean.getPassword(), user.getPassword())) {
                throw new Exception();
            }

            UserSigninDTO dto = new UserSigninDTO();
            dto.setToken(jwtUtil.generateToken(user.getUsername()));
            dto.setId(user.getId());
            dto.setUsername(user.getUsername());
            dto.setEmail(user.getEmail());
            res.setData(dto);
        } catch (Exception e) {
            this.checkException(e, res);
        }

        return res;
    }

    public ApiResponse signout(HttpServletRequest req) throws BaseException {
        ApiResponse res = new ApiResponse();
        try {
            String token = req.getHeader("Authorization");
            if (token != null && token.startsWith("Bearer ")) {
                token = token.substring(7);
                if (jwtUtil.validateToken(token)) {
                    jwtUtil.blacklistToken(token);
                }
            }
        } catch (Exception e) {
            this.checkException(e, res);
        }
        return res;
    }

    public ApiResponse sendTokenResetPassword(UserBean userBean) throws BaseException {
        ApiResponse res = new ApiResponse();
        try {
            String email = userBean.getEmail();
            if (StringUtil.isNullOrEmpty(email)) {
                throw new AuthException("required.email", "email field can't be null");
            }

            UserBean user = userRepository.findByEmail(email);
            if (user == null) {
                throw new AuthException("not.found.user", "not found user");
            }

            user.setToken_reset_password(jwtUtil.generatePasswordResetToken(String.valueOf(user.getId())));
            user.setToken_reset_password_expired(nextXMinute(30));

            userRepository.updateUser(user);

            user = userRepository.findById(user.getId());

            sendResetPasswordEmail(user);
        } catch (Exception e) {
            this.checkException(e, res);
        }
        return res;
    }

    public ApiResponse validate(@RequestHeader("Authorization") String token) throws BaseException{
        ApiResponse res = new ApiResponse();
        try {
            if (token.startsWith("Bearer ")) {
                token = token.substring(7);
            }
            boolean isValid = jwtUtil.validateToken(token);
            if(!isValid){
                throw new AuthException("invalid.token", "Invalid Token");
            }
        } catch (Exception e) {
            this.checkException(e, res);
        }
        return res;
    }

    public ApiResponse resetPassword(ResetPasswordDTO resetPasswordDTO) throws BaseException {
        ApiResponse res = new ApiResponse();
        try {
            UserBean user = userRepository.findUserByTokenResetPassword(resetPasswordDTO.getToken_reset_password());
            if (user == null) {
                throw new AuthException("not.found.user", "not found user");
            }
            Date now = new Date();
            Date expireDate = user.getToken_reset_password_expired();
            if (now.after(expireDate)) {
                throw new AuthException("reset.token.password.expire", "token password expire");
            }

            user.setPassword(passwordEncoder.encode(resetPasswordDTO.getNew_password()));
            user.setToken_reset_password(null);
            user.setToken_reset_password_expired(null);
            userRepository.updateUser(user);
        } catch (Exception e) {
            this.checkException(e, res);
        }
        return res;
    }

    public ApiResponse resendTokenResetPassword(ResetPasswordDTO resetPasswordDTO) throws BaseException{
        ApiResponse res = new ApiResponse();
        try {
            UserBean user = userRepository.findUserByTokenResetPassword(resetPasswordDTO.getToken_reset_password());
            if(user == null){
                throw new AuthException("not.found.user", "not found user");
            }

            user.setToken_reset_password(jwtUtil.generatePasswordResetToken(String.valueOf(user.getId())));
            user.setToken_reset_password_expired(nextXMinute(30));

            userRepository.updateUser(user);

            user = userRepository.findById(user.getId());

            sendResetPasswordEmail(user);
        } catch (Exception e) {
            this.checkException(e, res);
        }
        return res;
    }

    private boolean isEmail(String input) {
        String emailRegex = "^[A-Za-z0-9+_.-]+@(.+)$";
        return input.matches(emailRegex);
    }

    public boolean matchPassword(String rawPassword, String encodedPassword) {
        return passwordEncoder.matches(rawPassword, encodedPassword);
    }

    private Date nextXMinute(int minute) {
        Calendar calendar = Calendar.getInstance(TimeZone.getTimeZone("UTC"));
        calendar.add(Calendar.MINUTE, minute);
        return calendar.getTime();
    }

    private void sendResetPasswordEmail(UserBean userBean) {
        String resetToken = userBean.getToken_reset_password();
        try {
            emailService.sendResetPassword(userBean.getEmail(), userBean.getUsername(), resetToken);
        } catch (BaseException e) {
            throw new RuntimeException(e);
        }
    }



}
