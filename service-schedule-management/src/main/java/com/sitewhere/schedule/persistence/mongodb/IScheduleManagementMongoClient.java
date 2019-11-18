/*
 * Copyright (c) SiteWhere, LLC. All rights reserved. http://www.sitewhere.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package com.sitewhere.schedule.persistence.mongodb;

import org.bson.Document;

import com.mongodb.client.MongoCollection;
import com.sitewhere.spi.SiteWhereException;

/**
 * Mongo client that provides schedule management collections.
 */
public interface IScheduleManagementMongoClient {

    /** Default collection name for SiteWhere schedules */
    public static final String DEFAULT_SCHEDULES_COLLECTION_NAME = "schedules";

    /** Default collection name for SiteWhere scheduled jobs */
    public static final String DEFAULT_SCHEDULED_JOBS_COLLECTION_NAME = "scheduledjobs";

    /**
     * Get collection for schedules.
     * 
     * @return
     * @throws SiteWhereException
     */
    public MongoCollection<Document> getSchedulesCollection() throws SiteWhereException;

    /**
     * Get collection for scheduled jobs.
     * 
     * @return
     * @throws SiteWhereException
     */
    public MongoCollection<Document> getScheduledJobsCollection() throws SiteWhereException;
}