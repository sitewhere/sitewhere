package com.sitewhere.microservice.spi.spring;

/**
 * Spring bean names for tenant management configuration components.
 * 
 * @author Derek
 */
public interface TenantManagementBeans {

    /** Bean id for tenant mangement MongoDB client */
    public static final String BEAN_MONGODB_CLIENT = "mongoClient";

    /** Bean id for tenant management in server configuration */
    public static final String BEAN_TENANT_MANAGEMENT = "tenantManagement";
}