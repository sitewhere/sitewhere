package com.sitewhere.web.microservice;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.sitewhere.web.spi.microservice.IWebRestMicroservice;

@Configuration
public class WebRestMicroserviceConfiguration {

    @Bean
    public IWebRestMicroservice webRestMicroservice() {
	return new WebRestMicroservice();
    }
}