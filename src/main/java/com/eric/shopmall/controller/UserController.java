package com.eric.shopmall.controller;

import com.eric.shopmall.dao.UserDao;
import com.eric.shopmall.dto.UserLoginRequest;
import com.eric.shopmall.dto.UserLoginResponse;
import com.eric.shopmall.dto.UserRegisterRequest;
import com.eric.shopmall.model.Role;
import com.eric.shopmall.model.User;
import com.eric.shopmall.security.JwtTokenUtil;
import com.eric.shopmall.service.RoleService;
import com.eric.shopmall.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;

import java.util.List;
import java.util.stream.Collectors;

@RestController
public class UserController {

    @Autowired
    private UserService userService;

    @Autowired
    private RoleService roleService;

    @Autowired
    private UserDao userDao;

    @Autowired
    private JwtTokenUtil jwtTokenUtil;


//    @CrossOrigin(origins = {"https://eric8409.github.io"})
//    @CrossOrigin(origins = {"https://eric8409.github.io", "http://localhost:4200"})
    @PostMapping("/users/register")
    public ResponseEntity<User> register(@RequestBody @Valid UserRegisterRequest userRegisterRequest) {

     Integer userId = userService.register(userRegisterRequest);

     User user = userService.getUserById(userId);

        // 為 User 添加預設的 Role
        Role normalRole = roleService.getRoleByName("ROLE_NORMAL_MEMBER");
        userDao.addRoleForUserId(userId, normalRole);

     return ResponseEntity.status(HttpStatus.CREATED).body(user);
    }

//   @CrossOrigin(origins = {"https://eric8409.github.io"})
//    @CrossOrigin(origins = {"https://eric8409.github.io", "http://localhost:4200"})
    @PostMapping("/users/login")
    public  ResponseEntity<UserLoginResponse> login(@RequestBody @Valid UserLoginRequest userLoginRequest) {

        User user = userService.login(userLoginRequest);

        // 獲取使用者的角色列表
        List<Role> roles = userDao.getRolesByUserId(user.getUser_id());
        List<String> roleNames = roles.stream().map(Role::getRoleName).collect(Collectors.toList());

        // 生成 JWT Token
        String jwtToken = jwtTokenUtil.generateToken(String.valueOf(user.getUser_id()), roleNames);

        // 建立包含 Token 和使用者資訊的回應 DTO
        UserLoginResponse loginResponse = new UserLoginResponse(jwtToken, user.getUser_id(), user.getEmail());


        // 回傳包含 Token 的成功響應
        return ResponseEntity.status(HttpStatus.OK).body(loginResponse);

    }





}
