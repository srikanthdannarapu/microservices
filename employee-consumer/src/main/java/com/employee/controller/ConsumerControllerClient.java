package com.employee.controller;

import java.io.IOException;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.client.ServiceInstance;
import org.springframework.cloud.client.discovery.DiscoveryClient;
import org.springframework.cloud.client.loadbalancer.LoadBalancerClient;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import com.employee.model.Employee;
import com.netflix.hystrix.contrib.javanica.annotation.HystrixCommand;

@RestController
public class ConsumerControllerClient {
	
	@Autowired
	private DiscoveryClient discoveryClient;
	
	@Autowired
	private LoadBalancerClient loadBalancer;
	
	private static final Logger log = LoggerFactory.getLogger(ConsumerControllerClient.class);

	@RequestMapping(value = "/employeeConsumer", method = RequestMethod.GET)
	public Employee getEmployee() throws RestClientException, IOException {

		List<ServiceInstance> serviceInstances = discoveryClient.getInstances("employee-producer");
		ServiceInstance serviceInstance = serviceInstances.get(0);
		String baseUrl = serviceInstance.getUri().toString();
		baseUrl=baseUrl+"/employee";
		log.info(String.format("endPoint Url: %s", baseUrl));
		RestTemplate restTemplate = new RestTemplate();
		ResponseEntity<Employee> response = null;
		try {
			response = restTemplate.exchange(baseUrl, HttpMethod.GET, getHeaders(), Employee.class);
		} catch (Exception ex) {
			log.error(ex.getMessage());
		}
		return response.getBody();
	}

	@RequestMapping(value = "/employeeConsumerLoadBalance", method = RequestMethod.GET)
	@HystrixCommand(fallbackMethod = "reliable")
	public Employee getEmployeeWithLoadBalancing() throws RestClientException, IOException {

		log.info("calling employeeConsumerLoadBalance");
		List<ServiceInstance> serviceInstances = discoveryClient.getInstances("employee-producer");
		log.info("no. of instances %d", serviceInstances.size());
		ServiceInstance serviceInstance = loadBalancer.choose("employee-producer");
		log.info("serviceInstance: %s ",serviceInstance.getUri().toString());
		String baseUrl = serviceInstance.getUri().toString();
		
		baseUrl = baseUrl + "/employee";
		log.info(String.format("endPoint Url: %s", baseUrl));
		RestTemplate restTemplate = new RestTemplate();
		ResponseEntity<Employee> response = null;
		try {
			response = restTemplate.exchange(baseUrl, HttpMethod.GET, getHeaders(), Employee.class);
		} catch (Exception ex) {
			log.error(ex.getMessage());
		}
		return response.getBody();
	}
	private static HttpEntity<?> getHeaders() throws IOException {
		HttpHeaders headers = new HttpHeaders();
		headers.set("Accept", MediaType.APPLICATION_JSON_VALUE);
		return new HttpEntity<>(headers);
	}
	
	public Employee reliable() {
		log.info("calling reliable method");
	    return new Employee("1545", "fallback-emp1", "fallback-emp1", 20000.0);
	  }
}