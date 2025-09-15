package com.cmc;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableAsync;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;

@MapperScan("com.cmc.mapper")
@EnableAsync
@SpringBootApplication
public class BlogBackedApplication {

	public static void main(String[] args) {
		loadEnvFile();
		SpringApplication.run(BlogBackedApplication.class, args);
	}

	private static void loadEnvFile() {
		try {
			Properties props = new Properties();
			FileInputStream fis = new FileInputStream(".env");
			props.load(fis);

			// 设置到系统环境变量
			props.forEach((key, value) -> System.setProperty(key.toString(), value.toString()));

			fis.close();
		} catch (IOException e) {
			System.out.println(".env 文件未找到，使用系统环境变量");
		}
	}

}
