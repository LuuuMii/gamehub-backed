package com.cmc;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableAsync;

@MapperScan("com.cmc.mapper")
@EnableAsync
@SpringBootApplication
public class BlogBackedApplication {

	public static void main(String[] args) {
		SpringApplication.run(BlogBackedApplication.class, args);
	}

}
