package com.yjy.web;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.yjy.core.web.annotation.FormBean;
import com.yjy.domain.Role;
import com.yjy.domain.User;

@Controller
@RequestMapping("/user")
public class UserController {
	
	@RequestMapping("/addUser")
	@ResponseBody
	public User addUser(@FormBean("user") User user, @FormBean("role") Role role) {
		System.out.println(user);
		System.out.println(role);
		return user;
	}
}
