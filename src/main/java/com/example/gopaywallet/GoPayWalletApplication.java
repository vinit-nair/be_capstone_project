package com.example.gopaywallet;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication
@ComponentScan(basePackages = "com.example.gopaywallet")
public class GoPayWalletApplication {

	public static void main(String[] args) {
		SpringApplication.run(GoPayWalletApplication.class, args);
	}

}
