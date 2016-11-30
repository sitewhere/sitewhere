package com.sitewhere.server.asset;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.sitewhere.SiteWhere;
import com.sitewhere.server.lifecycle.LifecycleProgressMonitor;
import com.sitewhere.spi.SiteWhereException;
import com.sitewhere.spi.asset.IAssetCategory;
import com.sitewhere.spi.asset.IAssetManagementCacheProvider;
import com.sitewhere.spi.cache.ICacheListener;
import com.sitewhere.spi.tenant.ITenant;

/**
 * Handles events from the {@link IAssetManagementCacheProvider}.
 * 
 * @author Derek
 */
public class AssetCacheEventHandler implements ICacheListener<IAssetCategory> {

    /** Static logger instance */
    private static Logger LOGGER = LogManager.getLogger();

    /** Tenant */
    private ITenant tenant;

    public AssetCacheEventHandler(ITenant tenant) {
	this.tenant = tenant;
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * com.sitewhere.spi.cache.ICacheListener#onEntryAdded(java.lang.Object)
     */
    @Override
    public void onEntryAdded(IAssetCategory category) {
	try {
	    SiteWhere.getServer().getAssetModuleManager(getTenant()).onAssetCategoryAdded(category,
		    new LifecycleProgressMonitor());
	} catch (SiteWhereException e) {
	    LOGGER.error("Error creating asset module for new asset category.", e);
	}
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * com.sitewhere.spi.cache.ICacheListener#onEntryUpdated(java.lang.Object)
     */
    @Override
    public void onEntryUpdated(IAssetCategory category) {
	try {
	    SiteWhere.getServer().getAssetModuleManager(getTenant()).onAssetCategoryAdded(category,
		    new LifecycleProgressMonitor());
	} catch (SiteWhereException e) {
	    LOGGER.error("Error reloading asset module for updated asset category.", e);
	}
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * com.sitewhere.spi.cache.ICacheListener#onEntryRemoved(java.lang.Object)
     */
    @Override
    public void onEntryRemoved(IAssetCategory category) {
	try {
	    SiteWhere.getServer().getAssetModuleManager(getTenant()).onAssetCategoryRemoved(category,
		    new LifecycleProgressMonitor());
	} catch (SiteWhereException e) {
	    LOGGER.error("Error removing asset module for asset category.", e);
	}
    }

    public ITenant getTenant() {
	return tenant;
    }

    public void setTenant(ITenant tenant) {
	this.tenant = tenant;
    }
}