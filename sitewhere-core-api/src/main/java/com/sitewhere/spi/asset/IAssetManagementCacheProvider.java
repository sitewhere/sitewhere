package com.sitewhere.spi.asset;

import com.sitewhere.spi.SiteWhereException;
import com.sitewhere.spi.cache.ICache;
import com.sitewhere.spi.server.lifecycle.ITenantLifecycleComponent;

/**
 * Interface for entity that provides caching for asset management objects.
 * 
 * @author Derek
 */
public interface IAssetManagementCacheProvider extends ITenantLifecycleComponent {

    /**
     * Gets cache mapping asset category ids to {@link IAssetCategory} objects.
     * 
     * @return
     * @throws SiteWhereException
     */
    public ICache<String, IAssetCategory> getAssetCategoryCache() throws SiteWhereException;
}