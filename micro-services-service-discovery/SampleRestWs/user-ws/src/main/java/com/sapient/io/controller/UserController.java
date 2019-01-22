package com.sapient.io.controller;

import java.util.Arrays;
import java.util.List;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import com.sapient.io.bean.User;

@RestController
public class UserController {

    @GetMapping(value = "/")
    public List<User> getUsers(){
        return Arrays.asList(User.create());
    }
    

    @GetMapping(value = "/ping")
    public String ping(){
        return "User OK";
    }

}
