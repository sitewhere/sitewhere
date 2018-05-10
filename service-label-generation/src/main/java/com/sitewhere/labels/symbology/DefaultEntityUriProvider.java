/*
 * Copyright (c) SiteWhere, LLC. All rights reserved. http://www.sitewhere.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package com.sitewhere.labels.symbology;

import java.net.URI;
import java.net.URISyntaxException;

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
import com.sitewhere.spi.label.IEntityUriProvider;

/**
 * Default implementation of {@link IEntityUriProvider}.
 * 
 * @author Derek
 */
public class DefaultEntityUriProvider implements IEntityUriProvider {

    /** SiteWhere protocol prefix */
    public static final String SITEWHERE_PROTOCOL = "sitewhere://";

    /** Singleton instance */
    public static DefaultEntityUriProvider INSTANCE;

    protected DefaultEntityUriProvider() {
    }

    public static DefaultEntityUriProvider getInstance() {
	if (INSTANCE == null) {
	    INSTANCE = new DefaultEntityUriProvider();
	}
	return INSTANCE;
    }

    /*
     * @see
     * com.sitewhere.spi.label.IEntityUriProvider#getCustomerTypeIdentifier(com.
     * sitewhere.spi.customer.ICustomerType)
     */
    @Override
    public URI getCustomerTypeIdentifier(ICustomerType customerType) throws SiteWhereException {
	return createUri(SITEWHERE_PROTOCOL + "customertype/" + customerType.getToken());
    }

    /*
     * @see com.sitewhere.spi.label.IEntityUriProvider#getCustomerIdentifier(com.
     * sitewhere.spi.customer.ICustomer)
     */
    @Override
    public URI getCustomerIdentifier(ICustomer customer) throws SiteWhereException {
	return createUri(SITEWHERE_PROTOCOL + "customer/" + customer.getToken());
    }

    /*
     * @see
     * com.sitewhere.spi.device.symbology.IEntityUriProvider#getAreaTypeIdentifier(
     * com.sitewhere.spi.area.IAreaType)
     */
    @Override
    public URI getAreaTypeIdentifier(IAreaType areaType) throws SiteWhereException {
	return createUri(SITEWHERE_PROTOCOL + "areatype/" + areaType.getToken());
    }

    /*
     * @see
     * com.sitewhere.spi.device.symbology.IEntityUriProvider#getAreaIdentifier(com.
     * sitewhere.spi.area.IArea)
     */
    @Override
    public URI getAreaIdentifier(IArea area) throws SiteWhereException {
	return createUri(SITEWHERE_PROTOCOL + "area/" + area.getToken());
    }

    /*
     * @see
     * com.sitewhere.spi.device.symbology.IEntityUriProvider#getDeviceTypeIdentifier
     * (com.sitewhere.spi.device.IDeviceType)
     */
    @Override
    public URI getDeviceTypeIdentifier(IDeviceType deviceType) throws SiteWhereException {
	return createUri(SITEWHERE_PROTOCOL + "devicetype/" + deviceType.getToken());
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * com.sitewhere.spi.device.symbology.IEntityUriProvider#getDeviceIdentifier
     * (com.sitewhere .spi.device.IDevice)
     */
    @Override
    public URI getDeviceIdentifier(IDevice device) throws SiteWhereException {
	return createUri(SITEWHERE_PROTOCOL + "device/" + device.getToken());
    }

    /*
     * @see com.sitewhere.spi.device.symbology.IEntityUriProvider#
     * getDeviceGroupIdentifier(com.sitewhere.spi.device.group.IDeviceGroup)
     */
    @Override
    public URI getDeviceGroupIdentifier(IDeviceGroup group) throws SiteWhereException {
	return createUri(SITEWHERE_PROTOCOL + "devicegroup/" + group.getToken());
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.sitewhere.spi.device.symbology.IEntityUriProvider#
     * getDeviceAssignmentIdentifier (com.sitewhere.spi.device.IDeviceAssignment)
     */
    @Override
    public URI getDeviceAssignmentIdentifier(IDeviceAssignment assignment) throws SiteWhereException {
	return createUri(SITEWHERE_PROTOCOL + "assignment/" + assignment.getToken());
    }

    /*
     * @see
     * com.sitewhere.spi.device.symbology.IEntityUriProvider#getAssetTypeIdentifier(
     * com.sitewhere.spi.asset.IAssetType)
     */
    @Override
    public URI getAssetTypeIdentifier(IAssetType assetType) throws SiteWhereException {
	return createUri(SITEWHERE_PROTOCOL + "assettype/" + assetType.getToken());
    }

    /*
     * @see
     * com.sitewhere.spi.device.symbology.IEntityUriProvider#getAssetIdentifier(com.
     * sitewhere.spi.asset.IAsset)
     */
    @Override
    public URI getAssetIdentifier(IAsset asset) throws SiteWhereException {
	return createUri(SITEWHERE_PROTOCOL + "asset/" + asset.getToken());
    }

    /**
     * Create a URI.
     * 
     * @param uri
     * @return
     */
    protected URI createUri(String uri) {
	try {
	    return new URI(uri);
	} catch (URISyntaxException e) {
	    throw new RuntimeException(e);
	}
    }
}