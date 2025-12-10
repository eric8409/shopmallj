package com.eric.shopmall.dao;

import com.eric.shopmall.dto.UserRegisterRequest;
import com.eric.shopmall.model.Role;
import com.eric.shopmall.model.User;

import java.util.List;

public interface UserDao {

    User getUserById(Integer userId);

    User getUserByEmail(String email);

    Integer createUser(UserRegisterRequest userRegisterRequest);

    // 權限相關
    List<Role> getRolesByUserId(Integer userId);

    // 預設權限
    void addRoleForUserId(Integer userId, Role role);

    List<User> getAllUsers();

}
