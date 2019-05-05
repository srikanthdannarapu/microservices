package com.employee.employeeproducer;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication
@ComponentScan("com.employee")
@EnableDiscoveryClient
public class EmployeeProducerApplication {

	public static void main(String[] args) {
		SpringApplication.run(EmployeeProducerApplication.class, args);
	}

}
