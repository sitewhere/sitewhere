/*
 * Copyright (c) SiteWhere, LLC. All rights reserved. http://www.sitewhere.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package com.sitewhere.spi.label;

import com.sitewhere.spi.SiteWhereException;
import com.sitewhere.spi.area.IArea;
import com.sitewhere.spi.area.IAreaType;
import com.sitewhere.spi.asset.IAsset;
import com.sitewhere.spi.asset.IAssetType;
import com.sitewhere.spi.customer.ICustomer;
import com.sitewhere.spi.customer.ICustomerType;
import com.sitewhere.spi.device.IDevice;
import com.sitewhere.spi.device.IDeviceAssignment;
import com.sitewhere.spi.device.IDeviceType;
import com.sitewhere.spi.device.group.IDeviceGroup;
import com.sitewhere.spi.server.lifecycle.ITenantEngineLifecycleComponent;

/**
 * Generates label which correspond to SiteWhere entities.
 */
public interface ILabelGenerator extends ITenantEngineLifecycleComponent {

    /**
     * Get unique generator id.
     * 
     * @return
     * @throws SiteWhereException
     */
    public String getId() throws SiteWhereException;

    /**
     * Get name of generator.
     * 
     * @return
     * @throws SiteWhereException
     */
    public String getName() throws SiteWhereException;

    /**
     * Get label for a customer type.
     * 
     * @param customerType
     * @param provider
     * @return
     * @throws SiteWhereException
     */
    public byte[] getCustomerTypeLabel(ICustomerType customerType, IEntityUriProvider provider)
	    throws SiteWhereException;

    /**
     * Get label for a customer.
     * 
     * @param customer
     * @param provider
     * @return
     * @throws SiteWhereException
     */
    public byte[] getCustomerLabel(ICustomer customer, IEntityUriProvider provider) throws SiteWhereException;

    /**
     * Get label for an area type.
     * 
     * @param areaType
     * @param provider
     * @return
     * @throws SiteWhereException
     */
    public byte[] getAreaTypeLabel(IAreaType areaType, IEntityUriProvider provider) throws SiteWhereException;

    /**
     * Get label for an area.
     * 
     * @param area
     * @param provider
     * @return
     * @throws SiteWhereException
     */
    public byte[] getAreaLabel(IArea area, IEntityUriProvider provider) throws SiteWhereException;

    /**
     * Get label for a device type.
     * 
     * @param deviceType
     * @param provider
     * @return
     * @throws SiteWhereException
     */
    public byte[] getDeviceTypeLabel(IDeviceType deviceType, IEntityUriProvider provider) throws SiteWhereException;

    /**
     * Get label for a device.
     * 
     * @param device
     * @param provider
     * @return
     * @throws SiteWhereException
     */
    public byte[] getDeviceLabel(IDevice device, IEntityUriProvider provider) throws SiteWhereException;

    /**
     * Get label for a device group.
     * 
     * @param group
     * @param provider
     * @return
     * @throws SiteWhereException
     */
    public byte[] getDeviceGroupLabel(IDeviceGroup group, IEntityUriProvider provider) throws SiteWhereException;

    /**
     * Get label for a device assignment.
     * 
     * @param assignment
     * @param provider
     * @return
     * @throws SiteWhereException
     */
    public byte[] getDeviceAssignmentLabel(IDeviceAssignment assignment, IEntityUriProvider provider)
	    throws SiteWhereException;

    /**
     * Get label for an asset type.
     * 
     * @param assetType
     * @param provider
     * @return
     * @throws SiteWhereException
     */
    public byte[] getAssetTypeLabel(IAssetType assetType, IEntityUriProvider provider) throws SiteWhereException;

    /**
     * Get label for an asset.
     * 
     * @param asset
     * @param provider
     * @return
     * @throws SiteWhereException
     */
    public byte[] getAssetLabel(IAsset asset, IEntityUriProvider provider) throws SiteWhereException;
}