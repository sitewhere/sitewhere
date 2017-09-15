package com.sitewhere.event.persistence.mongodb;

import org.bson.Document;

import com.mongodb.client.MongoCollection;
import com.sitewhere.mongodb.BaseMongoClient;
import com.sitewhere.mongodb.MongoConfiguration;
import com.sitewhere.spi.SiteWhereException;
import com.sitewhere.spi.tenant.ITenant;

/**
 * Mongo client for interacting with device managment object model.
 * 
 * @author Derek
 */
public class DeviceEventManagementMongoClient extends BaseMongoClient implements IDeviceEventManagementMongoClient {

    /** Injected name used for events collection */
    private String eventsCollectionName = IDeviceEventManagementMongoClient.DEFAULT_EVENTS_COLLECTION_NAME;

    public DeviceEventManagementMongoClient(MongoConfiguration configuration) {
	super(configuration);
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * com.sitewhere.mongodb.IDeviceManagementMongoClient#getEventsCollection(
     * com. sitewhere .spi.user.ITenant)
     */
    public MongoCollection<Document> getEventsCollection(ITenant tenant) throws SiteWhereException {
	return getTenantDatabase(tenant).getCollection(getEventsCollectionName());
    }

    public String getEventsCollectionName() {
	return eventsCollectionName;
    }

    public void setEventsCollectionName(String eventsCollectionName) {
	this.eventsCollectionName = eventsCollectionName;
    }
}