/*
 * Copyright (c) SiteWhere, LLC. All rights reserved. http://www.sitewhere.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package com.sitewhere.spi.microservice.spring;

/**
 * Spring bean names for batch management configuration components.
 */
public class BatchManagementBeans {

    /** Bean id for batch management MongoDB client */
    public static final String BEAN_MONGODB_CLIENT = "mongoClient";

    /** Bean id for batch management in server configuration */
    public static final String BEAN_BATCH_MANAGEMENT = "batchManagement";

    /** Bean id for batch operation manager */
    public static final String BEAN_BATCH_OPERATION_MANAGER = "batchOperationManager";
}