/*
 * Copyright (c) SiteWhere, LLC. All rights reserved. http://www.sitewhere.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package com.sitewhere.microservice;

/**
 * Constants for interacting with Microservice environment.
 */
public class MicroserviceEnvironment {

    /** Default Docker hostname for instance management microservice */
    public static final String HOST_INSTANCE_MANAGEMENT = "instance-management";

    /** Default Docker hostname for user management microservice */
    public static final String HOST_USER_MANAGEMENT = "user-management";

    /** Default Docker hostname for tenant management microservice */
    public static final String HOST_TENANT_MANAGEMENT = "tenant-management";

    /** Default Docker hostname for device management microservice */
    public static final String HOST_DEVICE_MANAGEMENT = "device-management";

    /** Default Docker hostname for event management microservice */
    public static final String HOST_EVENT_MANAGEMENT = "event-management";

    /** Default Docker hostname for asset management microservice */
    public static final String HOST_ASSET_MANAGEMENT = "asset-management";

    /** Default Docker hostname for batch operations microservice */
    public static final String HOST_BATCH_OPERATIONS = "batch-operations";

    /** Default Docker hostname for schedule management microservice */
    public static final String HOST_SCHEDULE_MANAGEMENT = "schedule-management";
}