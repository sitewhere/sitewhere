package com.sitewhere.spi.asset;

/**
 * Provides functionality required to resolve an asset reference.
 * 
 * @author Derek
 */
public interface IAssetResolver {

    /**
     * Get underlying asset management implementation.
     * 
     * @return
     */
    public IAssetManagement getAssetManagement();

    /**
     * Get underlying asset module management implementation.
     * 
     * @return
     */
    public IAssetModuleManagement getAssetModuleManagement();
}