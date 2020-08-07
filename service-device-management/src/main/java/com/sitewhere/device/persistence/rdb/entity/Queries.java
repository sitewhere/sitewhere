/*
 * Copyright (c) SiteWhere, LLC. All rights reserved. http://www.sitewhere.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package com.sitewhere.device.persistence.rdb.entity;

public class Queries {

    /** Get device type by token */
    public static final String QUERY_DEVICE_TYPE_BY_TOKEN = "deviceTypes_findByToken";

    /** Get device command by token */
    public static final String QUERY_DEVICE_COMMAND_BY_TOKEN = "deviceCommands_findByToken";

    /** Get device status by token */
    public static final String QUERY_DEVICE_STATUS_BY_TOKEN = "deviceStatuses_findByToken";

    /** Get device by token */
    public static final String QUERY_DEVICE_BY_TOKEN = "devices_findByToken";

    /** Get device assignment by token */
    public static final String QUERY_DEVICE_ASSIGNMENT_BY_TOKEN = "deviceAssignments_findByToken";

    /** Get device assignment by device id and status */
    public static final String QUERY_DEVICE_ASSIGNMENT_BY_DEVICE_AND_STATUS = "deviceAssignments_findByDeviceAndStatus";

    /** Get customer type by token */
    public static final String QUERY_CUSTOMER_TYPE_BY_TOKEN = "customerTypes_findByToken";

    /** Get customers by token */
    public static final String QUERY_CUSTOMER_BY_TOKEN = "customers_findByToken";

    /** Get customers by parent id */
    public static final String QUERY_CUSTOMER_BY_PARENT_ID = "customers_findByParentId";

    /** Get area type by token */
    public static final String QUERY_AREA_TYPE_BY_TOKEN = "areaTypes_findByToken";

    /** Get area by token */
    public static final String QUERY_AREA_BY_TOKEN = "areas_findByToken";

    /** Get areas by parent id */
    public static final String QUERY_AREA_BY_PARENT_ID = "areas_findByParentId";

    /** Get zone by token */
    public static final String QUERY_ZONE_BY_TOKEN = "zones_findByToken";

    /** Get device group by token */
    public static final String QUERY_DEVICE_GROUP_BY_TOKEN = "deviceGroups_findByToken";
}
