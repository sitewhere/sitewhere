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
import com.sitewhere.configuration.instance.mongodb.MongoConfiguration;
import com.sitewhere.mongodb.MongoDbClient;
import com.sitewhere.spi.SiteWhereException;

/**
 * Mongo client for interacting with schedule management object model.
 * 
 * @author Derek
 */
public class ScheduleManagementMongoClient extends MongoDbClient implements IScheduleManagementMongoClient {

    /** Injected name used for schedules collection */
    private String schedulesCollectionName = IScheduleManagementMongoClient.DEFAULT_SCHEDULES_COLLECTION_NAME;

    /** Injected name used for scheduled jobs collection */
    private String scheduledJobsCollectionName = IScheduleManagementMongoClient.DEFAULT_SCHEDULED_JOBS_COLLECTION_NAME;

    public ScheduleManagementMongoClient(MongoConfiguration configuration) {
	super(configuration);
    }

    /*
     * @see
     * com.sitewhere.schedule.persistence.mongodb.IScheduleManagementMongoClient#
     * getSchedulesCollection()
     */
    @Override
    public MongoCollection<Document> getSchedulesCollection() throws SiteWhereException {
	return getDatabase().getCollection(getSchedulesCollectionName());
    }

    /*
     * @see
     * com.sitewhere.schedule.persistence.mongodb.IScheduleManagementMongoClient#
     * getScheduledJobsCollection()
     */
    @Override
    public MongoCollection<Document> getScheduledJobsCollection() throws SiteWhereException {
	return getDatabase().getCollection(getScheduledJobsCollectionName());
    }

    public String getSchedulesCollectionName() {
	return schedulesCollectionName;
    }

    public void setSchedulesCollectionName(String schedulesCollectionName) {
	this.schedulesCollectionName = schedulesCollectionName;
    }

    public String getScheduledJobsCollectionName() {
	return scheduledJobsCollectionName;
    }

    public void setScheduledJobsCollectionName(String scheduledJobsCollectionName) {
	this.scheduledJobsCollectionName = scheduledJobsCollectionName;
    }
}