package com.sitewhere.asset.persistence.mongodb;

import org.bson.Document;

import com.mongodb.client.MongoCollection;
import com.sitewhere.mongodb.BaseMongoClient;
import com.sitewhere.mongodb.MongoConfiguration;
import com.sitewhere.spi.SiteWhereException;
import com.sitewhere.spi.tenant.ITenant;

/**
 * Mongo client that provides access to asset management resources.
 * 
 * @author Derek
 */
public class AssetManagementMongoClient extends BaseMongoClient implements IAssetManagementMongoClient {

    /** Injected name used for asset categories collection */
    private String assetCategoriesCollectionName = IAssetManagementMongoClient.DEFAULT_ASSET_CATEGORIES_COLLECTION_NAME;

    /** Injected name used for assets collection */
    private String assetsCollectionName = IAssetManagementMongoClient.DEFAULT_ASSETS_COLLECTION_NAME;

    public AssetManagementMongoClient(MongoConfiguration configuration) {
	super(configuration);
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.sitewhere.mongodb.IAssetManagementMongoClient#
     * getAssetCategoriesCollection( com.sitewhere.spi.user.ITenant)
     */
    public MongoCollection<Document> getAssetCategoriesCollection(ITenant tenant) throws SiteWhereException {
	return getTenantDatabase(tenant).getCollection(getAssetCategoriesCollectionName());
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * com.sitewhere.mongodb.IAssetManagementMongoClient#getAssetsCollection(com
     * .sitewhere .spi.user.ITenant)
     */
    public MongoCollection<Document> getAssetsCollection(ITenant tenant) throws SiteWhereException {
	return getTenantDatabase(tenant).getCollection(getAssetsCollectionName());
    }

    public String getAssetCategoriesCollectionName() {
	return assetCategoriesCollectionName;
    }

    public void setAssetCategoriesCollectionName(String assetCategoriesCollectionName) {
	this.assetCategoriesCollectionName = assetCategoriesCollectionName;
    }

    public String getAssetsCollectionName() {
	return assetsCollectionName;
    }

    public void setAssetsCollectionName(String assetsCollectionName) {
	this.assetsCollectionName = assetsCollectionName;
    }
}