package com.sitewhere.web.microservice;

import org.springframework.boot.SpringApplication;
import org.springframework.context.annotation.ComponentScan;

import com.sitewhere.microservice.MicroserviceApplication;
import com.sitewhere.microservice.spi.MicroserviceNotAvailableException;
import com.sitewhere.web.spi.microservice.IWebRestMicroservice;

/**
 * Spring Boot application for web/REST microservice.
 * 
 * @author Derek
 */
@ComponentScan
public class WebRestApplication extends MicroserviceApplication<IWebRestMicroservice> {

    /** Web/REST microservice */
    public static IWebRestMicroservice SERVICE = new WebRestMicroservice();

    /** Get microservice instance after verifying it is available */
    public static IWebRestMicroservice getWebRestMicroservice() throws MicroserviceNotAvailableException {
	return assureAvailable(SERVICE);
    }

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
    public IWebRestMicroservice getMicroservice() {
	return SERVICE;
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