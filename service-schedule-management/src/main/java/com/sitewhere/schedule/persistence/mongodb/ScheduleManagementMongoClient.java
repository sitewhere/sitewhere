package com.sitewhere.schedule.persistence.mongodb;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.bson.Document;

import com.mongodb.client.MongoCollection;
import com.sitewhere.mongodb.BaseMongoClient;
import com.sitewhere.mongodb.MongoConfiguration;
import com.sitewhere.spi.SiteWhereException;
import com.sitewhere.spi.tenant.ITenant;

/**
 * Mongo client for interacting with schedule management object model.
 * 
 * @author Derek
 */
public class ScheduleManagementMongoClient extends BaseMongoClient implements IScheduleManagementMongoClient {

    /** Static logger instance */
    private static Logger LOGGER = LogManager.getLogger();

    /** Injected name used for schedules collection */
    private String schedulesCollectionName = IScheduleManagementMongoClient.DEFAULT_SCHEDULES_COLLECTION_NAME;

    /** Injected name used for scheduled jobs collection */
    private String scheduledJobsCollectionName = IScheduleManagementMongoClient.DEFAULT_SCHEDULED_JOBS_COLLECTION_NAME;

    public ScheduleManagementMongoClient(MongoConfiguration configuration) {
	super(configuration);
    }

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

    /*
     * (non-Javadoc)
     * 
     * @see com.sitewhere.spi.server.lifecycle.ILifecycleComponent#getLogger()
     */
    @Override
    public Logger getLogger() {
	return LOGGER;
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