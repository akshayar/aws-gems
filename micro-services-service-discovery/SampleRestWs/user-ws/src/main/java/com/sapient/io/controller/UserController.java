package com.sapient.io.controller;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import com.sapient.io.bean.User;

@RestController
public class UserController {
	
	@Value("#{${app.code:#null}}")
	private Map<String,String> map;
	
	@Value("${user.list}")
	private List<String> listAttrs;
	
	@PostConstruct
	public void init() {
		System.out.println(map);
		System.out.println(listAttrs.size());
		listAttrs.stream().forEach(System.out::println);
		System.out.println(listAttrs);
	}

    @GetMapping(value = "/")
    public List<User> getUsers(){
        return Arrays.asList(User.create());
    }
    

    @GetMapping(value = "/ping")
    public String ping(){
        return "User OK"+map;
    }

}
