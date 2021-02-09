/**
 * Copyright © 2014-2021 The SiteWhere Authors
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
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
