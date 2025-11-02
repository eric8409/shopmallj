package com.eric.shopmall.controller;

import com.eric.shopmall.dto.UserLoginRequest;
import com.eric.shopmall.dto.UserRegisterRequest;
import com.eric.shopmall.model.Product;
import com.eric.shopmall.model.User;
import com.eric.shopmall.service.UserService;
import com.eric.shopmall.service.impl.UserServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@RestController
public class UserController {

    @Autowired
    private UserService userService;


    @CrossOrigin(origins = "http://localhost:4200")
    @PostMapping("/users/register")
    public ResponseEntity<User> register(@RequestBody @Valid UserRegisterRequest userRegisterRequest) {

     Integer userId = userService.register(userRegisterRequest);

     User user = userService.getUserById(userId);

     return ResponseEntity.status(HttpStatus.CREATED).body(user);
    }

    @CrossOrigin(origins = "http://localhost:4200")
    @PostMapping("/users/login")
    public  ResponseEntity<User> login(@RequestBody @Valid UserLoginRequest userLoginRequest) {

        User user = userService.login(userLoginRequest);

        return ResponseEntity.status(HttpStatus.OK).body(user);






    }





}
