package com.sitewhere.rest.model.asset.request.scripting;

import java.util.List;

import com.sitewhere.rest.model.asset.request.AssetCategoryCreateRequest;
import com.sitewhere.rest.model.asset.request.HardwareAssetCreateRequest;
import com.sitewhere.rest.model.asset.request.LocationAssetCreateRequest;
import com.sitewhere.rest.model.asset.request.PersonAssetCreateRequest;
import com.sitewhere.rest.model.search.SearchCriteria;
import com.sitewhere.spi.SiteWhereException;
import com.sitewhere.spi.asset.IAsset;
import com.sitewhere.spi.asset.IAssetCategory;
import com.sitewhere.spi.asset.IAssetManagement;
import com.sitewhere.spi.asset.IAssetModuleManager;
import com.sitewhere.spi.asset.IHardwareAsset;
import com.sitewhere.spi.asset.ILocationAsset;
import com.sitewhere.spi.asset.IPersonAsset;

/**
 * Builder that supports creating asset management entities.
 * 
 * @author Derek
 */
public class AssetManagementRequestBuilder {

    /** Asset management implementation */
    private IAssetManagement assetManagement;

    /** Asset module manager */
    private IAssetModuleManager assetModuleManager;

    public AssetManagementRequestBuilder(IAssetManagement assetManagement, IAssetModuleManager assetModuleManager) {
	this.assetManagement = assetManagement;
	this.assetModuleManager = assetModuleManager;
    }

    public AssetCategoryCreateRequest.Builder newAssetCategory(String id, String name) {
	return new AssetCategoryCreateRequest.Builder(id, name);
    }

    public IAssetCategory persist(AssetCategoryCreateRequest.Builder builder) throws SiteWhereException {
	return getAssetManagement().createAssetCategory(builder.build());
    }

    public HardwareAssetCreateRequest.Builder newHardwareAsset(String id, String name, String imageUrl) {
	return new HardwareAssetCreateRequest.Builder(id, name, imageUrl);
    }

    public IHardwareAsset persist(String categoryId, HardwareAssetCreateRequest.Builder builder)
	    throws SiteWhereException {
	return getAssetManagement().createHardwareAsset(categoryId, builder.build());
    }

    public LocationAssetCreateRequest.Builder newLocationAsset(String id, String name, String imageUrl) {
	return new LocationAssetCreateRequest.Builder(id, name, imageUrl);
    }

    public ILocationAsset persist(String categoryId, LocationAssetCreateRequest.Builder builder)
	    throws SiteWhereException {
	return getAssetManagement().createLocationAsset(categoryId, builder.build());
    }

    public PersonAssetCreateRequest.Builder newPersonAsset(String id, String name, String imageUrl) {
	return new PersonAssetCreateRequest.Builder(id, name, imageUrl);
    }

    public IPersonAsset persist(String categoryId, PersonAssetCreateRequest.Builder builder) throws SiteWhereException {
	return getAssetManagement().createPersonAsset(categoryId, builder.build());
    }

    public List<? extends IAsset> allAssetsInModule(String moduleId) throws SiteWhereException {
	return getAssetManagement().listAssets(moduleId, SearchCriteria.ALL).getResults();
    }

    public IAssetManagement getAssetManagement() {
	return assetManagement;
    }

    public void setAssetManagement(IAssetManagement assetManagement) {
	this.assetManagement = assetManagement;
    }

    public IAssetModuleManager getAssetModuleManager() {
	return assetModuleManager;
    }

    public void setAssetModuleManager(IAssetModuleManager assetModuleManager) {
	this.assetModuleManager = assetModuleManager;
    }
}