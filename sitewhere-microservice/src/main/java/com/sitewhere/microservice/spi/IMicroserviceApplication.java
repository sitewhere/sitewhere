package com.sitewhere.microservice.spi;

/**
 * Common interface for Spring Boot applications that wrap SiteWhere
 * microservices.
 * 
 * @author Derek
 */
public interface IMicroserviceApplication {

    /**
     * Get wrapped microservice.
     * 
     * @return
     */
    public IMicroservice getMicroservice();
}