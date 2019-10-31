/*
 * Copyright (c) SiteWhere, LLC. All rights reserved. http://www.sitewhere.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package com.sitewhere.spi.microservice.spring;

/**
 * Spring bean names for asset management configuration components.
 */
public class AssetManagementBeans {

    /** Bean id for asset mangement MongoDB client */
    public static final String BEAN_MONGODB_CLIENT = "mongoClient";

    /** Bean id for asset management in server configuration */
    public static final String BEAN_ASSET_MANAGEMENT = "assetManagement";

    /** Bean id for asset module manager */
    public static final String BEAN_ASSET_MODULE_MANAGER = "assetModuleManager";
}