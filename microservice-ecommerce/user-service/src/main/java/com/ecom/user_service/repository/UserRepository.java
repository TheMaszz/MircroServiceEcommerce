package com.ecom.user_service.repository;

import com.ecom.user_service.bean.AddressBean;
import com.ecom.user_service.bean.UserBean;
import com.ecom.user_service.exception.BaseException;
import org.apache.ibatis.annotations.*;

import java.util.HashMap;
import java.util.List;

@Mapper
public interface UserRepository {

    @Select({
            "<script>",
            "SELECT username, email, profile_url, role, created_at, updated_at, last_login",
            "FROM user",
            "WHERE 1=1",
            "<if test='search != null and search != \"\"'>",
            " AND (username LIKE CONCAT('%', #{search}, '%') OR email LIKE CONCAT('%', #{search}, '%'))",
            "</if>",
            "<if test='isCount == false'>",
            " Order by ${sorting} ${sort_type} limit #{start}, #{end}",
            "</if>",
            "</script>"
    })
    public List<UserBean> findAllUser(HashMap<String, Object> params) throws BaseException;

    @Select({
            "SELECT id, username, email, profile_url, role, created_at, updated_at, last_login",
            "FROM user",
            "WHERE id = #{id}"
    })
    public UserBean findUserById(Long id) throws BaseException;

    @Update({
            "UPDATE user SET",
            "username = #{username},",
            "profile_url = #{profile_url},",
            "updated_at = now()",
            "WHERE id = #{id}"
    })
    public void updateUser(UserBean userBean) throws BaseException;

    @Delete({
            "DELETE FROM user WHERE id = #{id}"
    })
    public void deleteUser(Long id) throws BaseException;

    @Update({
            "UPDATE user SET",
            "role = #{role}",
            "WHERE id = #{id}"
    })
    public void changeRole(Long id, Long role) throws BaseException;

    @Select({
            "SELECT * FROM address WHERE id = #{id}"
    })
    public AddressBean getAddressById(Long id) throws BaseException;

    @Select({
            "SELECT * FROM address WHERE user_id = #{userId}"
    })
    public List<AddressBean> getAddressByUserId(Long userId) throws BaseException;

    @Insert({
            "INSERT INTO address",
            "(name, address, phone, description, user_id)",
            "VALUES",
            "(#{name}, #{address}, #{phone}, #{description}, #{user_id})"
    })
    public void createAddress(AddressBean addressBean) throws BaseException;

    @Update({
            "UPDATE address SET",
            "name = #{name},",
            "address = #{address},",
            "phone = #{phone},",
            "description = #{description}",
            "WHERE id = #{id}"
    })
    public void updateAddress(AddressBean addressBean) throws BaseException;


}
