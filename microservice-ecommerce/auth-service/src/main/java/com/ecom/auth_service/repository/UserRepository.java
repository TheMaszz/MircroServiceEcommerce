package com.ecom.auth_service.repository;

import com.ecom.common.bean.UserBean;
import com.ecom.common.exception.BaseException;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

@Mapper
public interface UserRepository {

    @Select({
            "SELECT id, email, username, password, role FROM user WHERE email = #{email}"
    })
    public UserBean findByEmail(String email) throws BaseException;

    @Select({
            "SELECT id, email, username, password, role FROM user WHERE username = #{username}"
    })
    public UserBean findByUsername(String username) throws BaseException;

    @Insert({
            "INSERT INTO user (username, email, password, created_at)",
            "VALUES(#{username}, #{email}, #{password}, now())"
    })
    public void createUser(UserBean userBean) throws BaseException;

    @Update({
            "<script>",
            "UPDATE user SET",
            "username = #{username},",
            "profile_url = #{profile_url},",
            "<if test='password != null and password != \"\"'> password = #{password}, </if>",
            "token_reset_password = #{token_reset_password},",
            "token_reset_password_expired = #{token_reset_password_expired},",
            "updated_at = now()",
            "WHERE id = #{id}",
            "</script>"
    })
    public void updateUser(UserBean userBean) throws BaseException;

    @Select({
            "SELECT id, username, email, token_reset_password_expired",
            "FROM user WHERE token_reset_password = #{token_reset_password}"
    })
    public UserBean findUserByTokenResetPassword(String token_reset_password) throws BaseException;


    @Select({
            "SELECT id, email, username, password, last_login, token_reset_password, token_reset_password_expired ",
            "FROM user WHERE id = #{id}"
    })
    public UserBean findById(Long id) throws BaseException;

    @Update("UPDATE user SET last_login = now() WHERE id = #{userId}")
    public void updateLastLogin(Long userId) throws BaseException;

    @Update("UPDATE user SET password = #{newPassword}")
    public void changePassword(String newPassword) throws BaseException;

}
