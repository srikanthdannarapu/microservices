package com.employee.employeeconsumer;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.circuitbreaker.EnableCircuitBreaker;
import org.springframework.cloud.netflix.eureka.EnableEurekaClient;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication
@ComponentScan("com.employee")
@EnableEurekaClient
@EnableCircuitBreaker
public class EmployeeConsumerApplication {

	public static void main(String[] args) {
		SpringApplication.run(EmployeeConsumerApplication.class, args);
	}

}
