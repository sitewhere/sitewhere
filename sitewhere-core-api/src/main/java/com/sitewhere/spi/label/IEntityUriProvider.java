/*
 * Copyright (c) SiteWhere, LLC. All rights reserved. http://www.sitewhere.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package com.sitewhere.spi.label;

import java.net.URI;

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

/**
 * Translates a SiteWhere entity into a unique URI that identifies it.
 */
public interface IEntityUriProvider {

    /**
     * Get unique identifier for a customer type.
     * 
     * @param customerType
     * @return
     * @throws SiteWhereException
     */
    public URI getCustomerTypeIdentifier(ICustomerType customerType) throws SiteWhereException;

    /**
     * Get unique identifier for a customer.
     * 
     * @param customer
     * @return
     * @throws SiteWhereException
     */
    public URI getCustomerIdentifier(ICustomer customer) throws SiteWhereException;

    /**
     * Get unique identifier for an area type.
     * 
     * @param areaType
     * @return
     * @throws SiteWhereException
     */
    public URI getAreaTypeIdentifier(IAreaType areaType) throws SiteWhereException;

    /**
     * Get unique identifier for an area.
     * 
     * @param area
     * @return
     * @throws SiteWhereException
     */
    public URI getAreaIdentifier(IArea area) throws SiteWhereException;

    /**
     * Get unique identifier for a device type.
     * 
     * @param deviceType
     * @return
     * @throws SiteWhereException
     */
    public URI getDeviceTypeIdentifier(IDeviceType deviceType) throws SiteWhereException;

    /**
     * Get unique identifier for a device.
     * 
     * @param device
     * @return
     * @throws SiteWhereException
     */
    public URI getDeviceIdentifier(IDevice device) throws SiteWhereException;

    /**
     * Get unique identifier for a device group.
     * 
     * @param group
     * @return
     * @throws SiteWhereException
     */
    public URI getDeviceGroupIdentifier(IDeviceGroup group) throws SiteWhereException;

    /**
     * Get unique identifier for a device assignment.
     * 
     * @param assignment
     * @return
     * @throws SiteWhereException
     */
    public URI getDeviceAssignmentIdentifier(IDeviceAssignment assignment) throws SiteWhereException;

    /**
     * Get unique identifier for an asset type.
     * 
     * @param assetType
     * @return
     * @throws SiteWhereException
     */
    public URI getAssetTypeIdentifier(IAssetType assetType) throws SiteWhereException;

    /**
     * Get unique identifier for an asset.
     * 
     * @param asset
     * @return
     * @throws SiteWhereException
     */
    public URI getAssetIdentifier(IAsset asset) throws SiteWhereException;
}