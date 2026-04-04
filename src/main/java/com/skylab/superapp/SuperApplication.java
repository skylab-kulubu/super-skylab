package com.skylab.superapp;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.scheduling.annotation.EnableAsync;

@SpringBootApplication
@EnableDiscoveryClient
@EnableCaching
@EnableAsync
@EnableJpaAuditing(auditorAwareRef = "auditorAwareImpl")
public class SuperApplication {

	public static void main(String[] args) {
		SpringApplication.run(SuperApplication.class, args);
	}

}
