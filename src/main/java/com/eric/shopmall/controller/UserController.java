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
import jakarta.servlet.http.Cookie;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import org.springframework.core.env.Environment;
import jakarta.servlet.http.HttpServletResponse;


import jakarta.validation.Valid;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
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

    @Autowired
    private Environment environment;


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
    public  ResponseEntity<UserLoginResponse> login(@RequestBody @Valid UserLoginRequest userLoginRequest, HttpServletResponse response) {

        User user = userService.login(userLoginRequest);

        // 獲取使用者的角色列表
        List<Role> roles = userDao.getRolesByUserId(user.getUser_id());
        List<String> roleNames = roles.stream().map(Role::getRoleName).collect(Collectors.toList());

        // 生成 JWT Token
        String jwtToken = jwtTokenUtil.generateToken(String.valueOf(user.getUser_id()), roleNames);



        // --- 設定 HttpOnly Cookie ---
        Cookie cookie = new Cookie("accessToken", jwtToken);
        cookie.setPath("/");            // 全站路徑可用
        cookie.setMaxAge(7 * 24 * 60 * 60); // 設定 Cookie 過期時間
        cookie.setAttribute("HttpOnly","true");       // JS 無法讀取此 Cookie
//        cookie.setAttribute("SameSite", "None");
//        cookie.setSecure(true);


        response.addCookie(cookie); // 將 Cookie 添加到 HTTP 回應中
        // --------------------------

        // DTO 只包含非敏感資訊
        UserLoginResponse loginResponse = new UserLoginResponse(user.getUser_id(), user.getEmail());

        // 回傳包含 Token 的成功響應
        return ResponseEntity.status(HttpStatus.OK).body(loginResponse);

    }

    @PostMapping("/users/logout")
    public ResponseEntity<String> logout(HttpServletResponse response) {
        // 建立一個同名但 MaxAge 設為 0 的 Cookie
        Cookie cookie = new Cookie("accessToken", null); // 值設為 null
        cookie.setPath("/");            // 必須與登入時設置的路徑一致
        cookie.setMaxAge(0);            // 立即過期
        cookie.setAttribute("HttpOnly", "true");
        cookie.setAttribute("SameSite", "None");
        cookie.setSecure(true);

        response.addCookie(cookie);

        return ResponseEntity.ok("已成功登出並清除 Cookie");
    }


    @GetMapping("/users/status")
    public ResponseEntity<?> checkStatus() {
        // 從 Spring Security 上下文中獲取當前認證訊息
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        // 檢查使用者是否已經過認證 (通常由 JWT Filter 負責設置)
        if (authentication != null && authentication.isAuthenticated() &&
                !"anonymousUser".equals(authentication.getPrincipal())) {

            // authentication.getName() 通常包含您的 User ID (根據您 generateToken 的方式)
            String userIdStr = authentication.getName();

            // 查詢使用者詳細資料
            User user = userService.getUserById(Integer.parseInt(userIdStr));

            if(user != null) {
                // 回傳前端需要的 JSON 資訊
                Map<String, Object> response = new HashMap<>();
                response.put("userId", user.getUser_id());
                response.put("email", user.getEmail());
                response.put("isLoggedIn", true);

                // 獲取角色並加入回應 (可選)
                List<Role> roles = userDao.getRolesByUserId(user.getUser_id());
                List<String> roleNames = roles.stream().map(Role::getRoleName).collect(Collectors.toList());
                response.put("roles", roleNames);

                return ResponseEntity.ok(response);
            }
        }

        // 如果未認證，返回 401 Unauthorized
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("User not authenticated");
    }



}
