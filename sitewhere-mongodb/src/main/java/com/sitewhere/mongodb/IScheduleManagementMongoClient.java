/*
 * Copyright (c) SiteWhere, LLC. All rights reserved. http://www.sitewhere.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package com.sitewhere.mongodb;

import com.mongodb.DBCollection;
import com.sitewhere.spi.tenant.ITenant;

/**
 * Mongo client that provides schedule management collections.
 * 
 * @author Derek
 */
public interface IScheduleManagementMongoClient {

    /** Default collection name for SiteWhere schedules */
    public static final String DEFAULT_SCHEDULES_COLLECTION_NAME = "schedules";

    /** Default collection name for SiteWhere scheduled jobs */
    public static final String DEFAULT_SCHEDULED_JOBS_COLLECTION_NAME = "scheduledjobs";

    public DBCollection getSchedulesCollection(ITenant tenant);

    public DBCollection getScheduledJobsCollection(ITenant tenant);
}