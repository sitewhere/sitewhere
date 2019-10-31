/*
 * Copyright (c) SiteWhere, LLC. All rights reserved. http://www.sitewhere.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package com.sitewhere.rest.model.device.asset;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.sitewhere.rest.model.device.event.DeviceMeasurement;
import com.sitewhere.spi.SiteWhereException;
import com.sitewhere.spi.asset.IAssetManagement;
import com.sitewhere.spi.device.event.IDeviceMeasurement;

/**
 * Wraps a {@link DeviceMeasurement} so that information about the asset
 * associated with its assignment is available.
 */
@JsonIgnoreProperties
@JsonInclude(Include.NON_NULL)
public class DeviceMeasurementsWithAsset extends DeviceEventWithAsset implements IDeviceMeasurement {

    /** Serial version UID */
    private static final long serialVersionUID = -732056996257170342L;

    public DeviceMeasurementsWithAsset(IDeviceMeasurement wrapped, IAssetManagement assetManagement)
	    throws SiteWhereException {
	super(wrapped, assetManagement);
    }

    /*
     * @see com.sitewhere.spi.device.event.IDeviceMeasurement#getName()
     */
    @Override
    public String getName() {
	return ((IDeviceMeasurement) getWrapped()).getName();
    }

    /*
     * @see com.sitewhere.spi.device.event.IDeviceMeasurement#getValue()
     */
    @Override
    public Double getValue() {
	return ((IDeviceMeasurement) getWrapped()).getValue();
    }
}