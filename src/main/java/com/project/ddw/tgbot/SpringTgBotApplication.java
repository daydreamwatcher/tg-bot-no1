package com.project.ddw.tgbot;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;

@SpringBootApplication(exclude = {DataSourceAutoConfiguration.class})
public class SpringTgBotApplication {

	public static void main(String[] args) {
		SpringApplication.run(SpringTgBotApplication.class, args);
	}

}
