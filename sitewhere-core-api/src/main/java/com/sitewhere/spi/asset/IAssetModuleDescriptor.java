package com.sitewhere.spi.asset;

/**
 * Provides information about an asset module.
 * 
 * @author Derek
 */
public interface IAssetModuleDescriptor {

    /**
     * Get the unique module identifier.
     * 
     * @return
     */
    public String getId();

    /**
     * Get the module name.
     * 
     * @return
     */
    public String getName();

    /**
     * Indicates the type of assets provided.
     * 
     * @return
     */
    public AssetType getAssetType();
}
