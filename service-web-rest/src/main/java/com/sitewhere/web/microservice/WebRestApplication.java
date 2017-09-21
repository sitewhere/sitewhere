package com.sitewhere.web.microservice;

import org.springframework.boot.SpringApplication;
import org.springframework.context.annotation.ComponentScan;

import com.sitewhere.microservice.MicroserviceApplication;
import com.sitewhere.microservice.spi.IMicroservice;

/**
 * Spring Boot application for web/REST microservice.
 * 
 * @author Derek
 */
@ComponentScan
public class WebRestApplication extends MicroserviceApplication {

    /** Web/REST microservice */
    private WebRestMicroservice service = new WebRestMicroservice();

    /**
     * Run in background thread since servlet container keeps context alive.
     */
    public WebRestApplication() {
	super(true);
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * com.sitewhere.microservice.spi.IMicroserviceApplication#getMicroservice()
     */
    @Override
    public IMicroservice getMicroservice() {
	return service;
    }

    /**
     * Entry point for Spring Boot.
     * 
     * @param args
     */
    public static void main(String[] args) {
	SpringApplication.run(WebRestApplication.class, args);
    }
}