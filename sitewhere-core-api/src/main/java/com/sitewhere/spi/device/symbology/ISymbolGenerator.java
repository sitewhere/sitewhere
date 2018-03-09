/*
 * Copyright (c) SiteWhere, LLC. All rights reserved. http://www.sitewhere.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package com.sitewhere.spi.device.symbology;

import com.sitewhere.spi.SiteWhereException;
import com.sitewhere.spi.area.IArea;
import com.sitewhere.spi.area.IAreaType;
import com.sitewhere.spi.asset.IAsset;
import com.sitewhere.spi.asset.IAssetType;
import com.sitewhere.spi.device.IDevice;
import com.sitewhere.spi.device.IDeviceAssignment;
import com.sitewhere.spi.device.IDeviceType;
import com.sitewhere.spi.device.group.IDeviceGroup;
import com.sitewhere.spi.server.lifecycle.ITenantEngineLifecycleComponent;

/**
 * Generates symbols which uniquely identify SiteWhere entities.
 * 
 * @author Derek
 */
public interface ISymbolGenerator extends ITenantEngineLifecycleComponent {

    /**
     * Get unique generator id.
     * 
     * @return
     * @throws SiteWhereException
     */
    public String getId() throws SiteWhereException;

    /**
     * Get name of symbol generator.
     * 
     * @return
     * @throws SiteWhereException
     */
    public String getName() throws SiteWhereException;

    /**
     * Get symbol for an area type.
     * 
     * @param areaType
     * @param provider
     * @return
     * @throws SiteWhereException
     */
    public byte[] getAreaTypeSymbol(IAreaType areaType, IEntityUriProvider provider) throws SiteWhereException;

    /**
     * Get symbol for an area.
     * 
     * @param site
     * @param provider
     * @return
     * @throws SiteWhereException
     */
    public byte[] getAreaSymbol(IArea area, IEntityUriProvider provider) throws SiteWhereException;

    /**
     * Get symbol for a device type.
     * 
     * @param deviceType
     * @param provider
     * @return
     * @throws SiteWhereException
     */
    public byte[] getDeviceTypeSymbol(IDeviceType deviceType, IEntityUriProvider provider) throws SiteWhereException;

    /**
     * Get symbol for a device.
     * 
     * @param device
     * @param provider
     * @return
     * @throws SiteWhereException
     */
    public byte[] getDeviceSymbol(IDevice device, IEntityUriProvider provider) throws SiteWhereException;

    /**
     * Get symbol for a device group.
     * 
     * @param group
     * @param provider
     * @return
     * @throws SiteWhereException
     */
    public byte[] getDeviceGroupSymbol(IDeviceGroup group, IEntityUriProvider provider) throws SiteWhereException;

    /**
     * Get symbol for a device assignment.
     * 
     * @param assignment
     * @param provider
     * @return
     * @throws SiteWhereException
     */
    public byte[] getDeviceAssigmentSymbol(IDeviceAssignment assignment, IEntityUriProvider provider)
	    throws SiteWhereException;

    /**
     * Get symbol for an asset type.
     * 
     * @param assetType
     * @param provider
     * @return
     * @throws SiteWhereException
     */
    public byte[] getAssetTypeSymbol(IAssetType assetType, IEntityUriProvider provider) throws SiteWhereException;

    /**
     * Get symbol for an asset.
     * 
     * @param asset
     * @param provider
     * @return
     * @throws SiteWhereException
     */
    public byte[] getAssetSymbol(IAsset asset, IEntityUriProvider provider) throws SiteWhereException;
}