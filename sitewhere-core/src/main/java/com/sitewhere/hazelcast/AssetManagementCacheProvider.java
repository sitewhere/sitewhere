package com.sitewhere.hazelcast;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.sitewhere.server.lifecycle.TenantLifecycleComponent;
import com.sitewhere.spi.SiteWhereException;
import com.sitewhere.spi.asset.IAssetCategory;
import com.sitewhere.spi.asset.IAssetManagementCacheProvider;
import com.sitewhere.spi.cache.CacheType;
import com.sitewhere.spi.server.lifecycle.ILifecycleProgressMonitor;
import com.sitewhere.spi.server.lifecycle.LifecycleComponentType;

/**
 * Implementation of {@link IAssetManagementCacheProvider} that caches data in
 * Hazelcast.
 * 
 * @author Derek
 */
public class AssetManagementCacheProvider extends TenantLifecycleComponent implements IAssetManagementCacheProvider {

    /** Static logger instance */
    private static Logger LOGGER = LogManager.getLogger();

    /** Name of asset category cache */
    private static final String ASSET_CATEGORY_CACHE = "assetCategoryCache";

    /** Cache for sites */
    private HazelcastCache<IAssetCategory> assetCategoryCache;

    public AssetManagementCacheProvider() {
	super(LifecycleComponentType.CacheProvider);
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.sitewhere.server.lifecycle.LifecycleComponent#initialize(com.
     * sitewhere.spi.server.lifecycle.ILifecycleProgressMonitor)
     */
    @Override
    public void initialize(ILifecycleProgressMonitor monitor) throws SiteWhereException {
	this.assetCategoryCache = new HazelcastCache<IAssetCategory>(this,
		HazelcastCache.getNameForTenantCache(getTenant(), ASSET_CATEGORY_CACHE), CacheType.AssetCategoryCache,
		true);
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * com.sitewhere.server.lifecycle.LifecycleComponent#start(com.sitewhere.spi
     * .server.lifecycle.ILifecycleProgressMonitor)
     */
    @Override
    public void start(ILifecycleProgressMonitor monitor) throws SiteWhereException {
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.sitewhere.spi.asset.IAssetManagementCacheProvider#
     * getAssetCategoryCache()
     */
    @Override
    public HazelcastCache<IAssetCategory> getAssetCategoryCache() {
	return assetCategoryCache;
    }

    public void setAssetCategoryCache(HazelcastCache<IAssetCategory> assetCategoryCache) {
	this.assetCategoryCache = assetCategoryCache;
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
}