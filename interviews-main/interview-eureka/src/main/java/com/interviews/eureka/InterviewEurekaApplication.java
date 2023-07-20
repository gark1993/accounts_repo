package com.interviews.eureka;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.netflix.eureka.server.EnableEurekaServer;

@EnableEurekaServer
@SpringBootApplication
public class InterviewEurekaApplication {

	public static void main(String[] args) {
		SpringApplication.run(InterviewEurekaApplication.class, args);
	}

}
