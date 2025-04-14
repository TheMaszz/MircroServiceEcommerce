package com.ecom.auth_service.service;

import ch.qos.logback.core.util.StringUtil;
import com.ecom.auth_service.exception.AuthException;
import com.ecom.auth_service.repository.UserRepository;
import com.ecom.auth_service.util.JwtUtil;
import com.ecom.common.bean.ApiResponse;
import com.ecom.common.bean.UserBean;
import com.ecom.common.controller.BaseController;
import com.ecom.common.dto.ChangePasswordDTO;
import com.ecom.common.dto.ResetPasswordDTO;
import com.ecom.common.dto.UserSigninDTO;
import com.ecom.common.exception.BaseException;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;


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

            if (user == null) {
                throw new AuthException("not.found", "User Not Found");
            }

            if (!matchPassword(userBean.getPassword(), user.getPassword())) {
                throw new AuthException("password.not.match", "Password incorrect");
            }

            userRepository.updateLastLogin(user.getId());

            UserSigninDTO dto = new UserSigninDTO();
            dto.setToken(jwtUtil.generateToken(checkRole(user.getRole()), user.getId()));
            dto.setId(user.getId());
            dto.setUsername(user.getUsername());
            dto.setEmail(user.getEmail());
            dto.setRole(user.getRole());
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
            res.setData(user.getToken_reset_password());
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

    public ApiResponse resendTokenResetPassword(UserBean userBean) throws BaseException {
        ApiResponse res = new ApiResponse();
        try {
            UserBean user = userRepository.findByEmail(userBean.getEmail());
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

    public ApiResponse changePassword(HttpServletRequest req, ChangePasswordDTO changePasswordDTO) throws BaseException {
        ApiResponse res = new ApiResponse();
        try {
            String userId = req.getHeader("X-User-Id");

            UserBean user = userRepository.findById(Long.valueOf(userId));
            if (user == null) {
                throw new AuthException("not.found.user", "not found user");
            }

            if (!matchPassword(changePasswordDTO.getOld_password(), user.getPassword())) {
                throw new AuthException("password.not.match", "Password incorrect");
            }

            userRepository.changePassword(passwordEncoder.encode(changePasswordDTO.getNew_password()));


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

    private String checkRole(Integer role) {
        return switch (role) {
            case 0 -> "USER";
            case 1 -> "ADMIN";
            case 2 -> "SELLER";
            default -> "INVALID";
        };
    }

}
