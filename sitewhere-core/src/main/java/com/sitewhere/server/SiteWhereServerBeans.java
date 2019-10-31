/*
 * Copyright (c) SiteWhere, LLC. All rights reserved. http://www.sitewhere.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package com.sitewhere.server;

/**
 * Constants for Spring beans needed by the SiteWhere server.
 */
public interface SiteWhereServerBeans {

    /*************************
     * MANAGEMENT INTERFACES *
     *************************/

    /** Bean id for user management in server configuration */
    public static final String BEAN_USER_MANAGEMENT = "userManagement";

    /** Bean id for device management in server configuration */
    public static final String BEAN_DEVICE_MANAGEMENT = "deviceManagement";

    /** Bean id for device event management in server configuration */
    public static final String BEAN_DEVICE_EVENT_MANAGEMENT = "deviceEventManagement";

    /** Bean id for asset management in server configuration */
    public static final String BEAN_ASSET_MANAGEMENT = "assetManagement";

    /** Bean id for schedule management in server configuration */
    public static final String BEAN_SCHEDULE_MANAGEMENT = "scheduleManagement";

    /** Bean id for device communication subsystem in server configuration */
    public static final String BEAN_DEVICE_COMMUNICATION = "deviceCommunication";

    /** Bean id for event processing subsystem in server configuration */
    public static final String BEAN_EVENT_PROCESSING = "eventProcessing";

    /*****************************
     * SEARCH PROVIDER MANAGMENT *
     *****************************/

    /** Bean id for search provider manager */
    public static final String BEAN_SEARCH_PROVIDER_MANAGER = "searchProviderManager";

    /*********************
     * DATA INITIALIZERS *
     *********************/

    /** Bean id for user management data initializer in server configuration */
    public static final String BEAN_USER_MODEL_INITIALIZER = "userModelInitializer";

    /**
     * Bean id for tenant management data initializer in server configuration
     */
    public static final String BEAN_TENANT_MODEL_INITIALIZER = "tenantModelInitializer";
}