package com.eric.shopmall.service;

import com.eric.shopmall.dto.UserLoginRequest;
import com.eric.shopmall.dto.UserRegisterRequest;
import com.eric.shopmall.model.User;

public interface UserService {

    User getUserById(Integer userId);

    Integer register(UserRegisterRequest userRegisterRequest);

    User login(UserLoginRequest userLoginRequest);





}
