package com.sitewhere.spi.asset;

import com.sitewhere.spi.SiteWhereException;

/**
 * Implemented by {@link IAssetManagement} implementations that support caching.
 * 
 * @author Derek
 */
public interface ICachingAssetManagement {

    /**
     * Set the cache provider to be used by the {@link IAssetManagement}
     * implementation.
     * 
     * @param provider
     * @throws SiteWhereException
     */
    public void setCacheProvider(IAssetManagementCacheProvider provider) throws SiteWhereException;
}