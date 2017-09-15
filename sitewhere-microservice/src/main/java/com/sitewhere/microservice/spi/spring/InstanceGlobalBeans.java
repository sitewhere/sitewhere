package com.sitewhere.microservice.spi.spring;

/**
 * Spring bean names for global instance configuration components.
 * 
 * @author Derek
 */
public interface InstanceGlobalBeans {

    /** Bean id for common MongoDB client */
    public static final String BEAN_MONGO_CLIENT = "mongo";
}