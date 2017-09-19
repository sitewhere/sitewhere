package com.sitewhere.microservice.spi.spring;

/**
 * Spring bean names for user management configuration components.
 * 
 * @author Derek
 */
public class UserManagementBeans {

    /** Bean id for user mangement MongoDB client */
    public static final String BEAN_MONGODB_CLIENT = "mongoClient";

    /** Bean id for user management in server configuration */
    public static final String BEAN_USER_MANAGEMENT = "userManagement";
}