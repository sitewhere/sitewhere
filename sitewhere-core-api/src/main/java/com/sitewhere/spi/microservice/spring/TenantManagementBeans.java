/*
 * Copyright (c) SiteWhere, LLC. All rights reserved. http://www.sitewhere.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package com.sitewhere.spi.microservice.spring;

/**
 * Spring bean names for tenant management configuration components.
 */
public interface TenantManagementBeans {

    /** Bean id for tenant mangement MongoDB client */
    public static final String BEAN_MONGODB_CLIENT = "mongoClient";

    /** Bean id for tenant management in server configuration */
    public static final String BEAN_TENANT_MANAGEMENT = "tenantManagement";
}