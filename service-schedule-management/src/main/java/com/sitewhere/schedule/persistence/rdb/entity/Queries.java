/*
 * Copyright (c) SiteWhere, LLC. All rights reserved. http://www.sitewhere.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package com.sitewhere.schedule.persistence.rdb.entity;

public class Queries {

    /** Get schedule by token */
    public static final String QUERY_SCHEDULE_BY_TOKEN = "schedules_findByToken";

    /** Get scheduled job by token */
    public static final String QUERY_SCHEDULED_JOB_BY_TOKEN = "scheduledJobs_findByToken";
}
