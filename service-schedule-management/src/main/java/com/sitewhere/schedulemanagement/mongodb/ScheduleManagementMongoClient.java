package com.sitewhere.schedulemanagement.mongodb;

import org.bson.Document;

import com.mongodb.client.MongoCollection;
import com.sitewhere.mongodb.BaseMongoClient;
import com.sitewhere.spi.SiteWhereException;
import com.sitewhere.spi.tenant.ITenant;

/**
 * Mongo client for interacting with schedule management object model.
 * 
 * @author Derek
 */
public class ScheduleManagementMongoClient extends BaseMongoClient implements IScheduleManagementMongoClient {

    /** Injected name used for schedules collection */
    private String schedulesCollectionName = IScheduleManagementMongoClient.DEFAULT_SCHEDULES_COLLECTION_NAME;

    /** Injected name used for scheduled jobs collection */
    private String scheduledJobsCollectionName = IScheduleManagementMongoClient.DEFAULT_SCHEDULED_JOBS_COLLECTION_NAME;

    /*
     * (non-Javadoc)
     * 
     * @see com.sitewhere.mongodb.IScheduleManagementMongoClient#
     * getSchedulesCollection(com .sitewhere.spi.user.ITenant)
     */
    @Override
    public MongoCollection<Document> getSchedulesCollection(ITenant tenant) throws SiteWhereException {
	return getTenantDatabase(tenant).getCollection(getSchedulesCollectionName());
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.sitewhere.mongodb.IScheduleManagementMongoClient#
     * getScheduledJobsCollection (com.sitewhere.spi.user.ITenant)
     */
    @Override
    public MongoCollection<Document> getScheduledJobsCollection(ITenant tenant) throws SiteWhereException {
	return getTenantDatabase(tenant).getCollection(getScheduledJobsCollectionName());
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