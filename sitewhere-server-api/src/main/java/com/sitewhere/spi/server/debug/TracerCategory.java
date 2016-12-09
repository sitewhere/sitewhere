/*
 * Copyright (c) SiteWhere, LLC. All rights reserved. http://www.sitewhere.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package com.sitewhere.spi.server.debug;

/**
 * Allows hierarchy levels in {@link ITracer} stacks to be categorized.
 * 
 * @author Derek
 */
public enum TracerCategory {

    /** Category for admin user interface access */
    AdminUserInterface,

    /** Category for REST API invocations */
    RestApiCall,

    /** Category for core device management API invocations */
    DeviceManagementApiCall,

    /** Category for underlying datastore functionality */
    DataStore,

    /** Indicates no category specified */
    Unspecified
}