package com.yjy;

import java.util.List;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;

import com.yjy.core.web.annotation.FormBeanArgumentResolver;

@SpringBootApplication
public class Application extends WebMvcConfigurerAdapter{
	
	public static void main(String[] args) {
		SpringApplication.run(Application.class, args);
	}
	
	@Override
    public void addArgumentResolvers(List<HandlerMethodArgumentResolver> argumentResolvers) {
        super.addArgumentResolvers(argumentResolvers);
        argumentResolvers.add(new FormBeanArgumentResolver());
    }
}
