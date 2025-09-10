package com.skylab.superapp;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

@SpringBootApplication
@EnableDiscoveryClient
public class SuperApplication {

	public static void main(String[] args) {
		SpringApplication.run(SuperApplication.class, args);
	}

}
