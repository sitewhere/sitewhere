/**
 * Copyright © 2014-2021 The SiteWhere Authors
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.sitewhere.labels.manager;

import java.net.URI;
import java.net.URISyntaxException;

import com.sitewhere.labels.spi.IEntityUriProvider;
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
 * Default implementation of {@link IEntityUriProvider}.
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