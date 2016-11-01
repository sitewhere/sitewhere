package com.sitewhere.rest.model.asset.request.scripting;

import com.sitewhere.rest.model.asset.request.AssetCategoryCreateRequest;
import com.sitewhere.spi.SiteWhereException;
import com.sitewhere.spi.asset.IAssetCategory;
import com.sitewhere.spi.asset.IAssetManagement;

/**
 * Builder that supports creating asset management entities.
 * 
 * @author Derek
 */
public class AssetManagementRequestBuilder {

    /** Asset management implementation */
    private IAssetManagement assetMangement;

    public AssetCategoryCreateRequest.Builder newAssetCategory(String id, String name) {
	return new AssetCategoryCreateRequest.Builder(id, name);
    }

    public IAssetCategory persist(AssetCategoryCreateRequest.Builder builder) throws SiteWhereException {
	return getAssetMangement().createAssetCategory(builder.build());
    }

    public IAssetManagement getAssetMangement() {
	return assetMangement;
    }

    public void setAssetMangement(IAssetManagement assetMangement) {
	this.assetMangement = assetMangement;
    }
}