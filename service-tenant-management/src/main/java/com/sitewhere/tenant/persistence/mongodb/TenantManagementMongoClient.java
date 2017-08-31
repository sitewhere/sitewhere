package com.sitewhere.tenant.persistence.mongodb;

import org.bson.Document;

import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.sitewhere.mongodb.BaseMongoClient;
import com.sitewhere.spi.SiteWhereException;

/**
 * Mongo client for interacting with tenant management object model.
 * 
 * @author Derek
 */
public class TenantManagementMongoClient extends BaseMongoClient implements ITenantManagementMongoClient {

    /** Injected name used for tenants collection */
    private String tenantsCollectionName = ITenantManagementMongoClient.DEFAULT_TENANTS_COLLECTION_NAME;

    /*
     * (non-Javadoc)
     * 
     * @see
     * com.sitewhere.mongodb.IGlobalManagementMongoClient#deleteTenantData(java.
     * lang.String)
     */
    @Override
    public void deleteTenantData(String tenantId) throws SiteWhereException {
	MongoDatabase tenantDatabase = getTenantDatabase(tenantId);
	if (tenantDatabase != null) {
	    tenantDatabase.drop();
	}
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * com.sitewhere.mongodb.IGlobalManagementMongoClient#getTenantsCollection()
     */
    @Override
    public MongoCollection<Document> getTenantsCollection() {
	return getGlobalDatabase().getCollection(getTenantsCollectionName());
    }

    public String getTenantsCollectionName() {
	return tenantsCollectionName;
    }

    public void setTenantsCollectionName(String tenantsCollectionName) {
	this.tenantsCollectionName = tenantsCollectionName;
    }
}