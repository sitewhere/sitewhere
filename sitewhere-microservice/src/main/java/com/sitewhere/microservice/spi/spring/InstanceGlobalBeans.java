package com.sitewhere.microservice.spi.spring;

/**
 * Spring bean names for global instance configuration components.
 * 
 * @author Derek
 */
public interface InstanceGlobalBeans {

    /** Suffix used for default configuration */
    public static final String BEAN_SUFFIX_DEFAULT = "_default";

    /** Bean id base for MongoDB configuration data */
    public static final String BEAN_MONGO_CONFIGURATION_BASE = "mongodb_";

    /** Bean id for default MongoDB configuration data */
    public static final String BEAN_MONGO_CONFIGURATION_DEFAULT = BEAN_MONGO_CONFIGURATION_BASE + BEAN_SUFFIX_DEFAULT;
}