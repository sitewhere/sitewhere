package com.sitewhere.microservice.spi;

/**
 * Common interface for Spring Boot applications that wrap SiteWhere
 * microservices.
 * 
 * @author Derek
 *
 * @param <T>
 */
public interface IMicroserviceApplication<T extends IMicroservice> {

    /**
     * Get wrapped microservice.
     * 
     * @return
     */
    public T getMicroservice();
}