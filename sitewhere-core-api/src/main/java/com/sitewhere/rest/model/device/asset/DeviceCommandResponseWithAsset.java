/*
 * Copyright (c) SiteWhere, LLC. All rights reserved. http://www.sitewhere.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package com.sitewhere.rest.model.device.asset;

import java.util.UUID;

import com.sitewhere.rest.model.device.event.DeviceCommandResponse;
import com.sitewhere.spi.SiteWhereException;
import com.sitewhere.spi.asset.IAssetManagement;
import com.sitewhere.spi.device.event.IDeviceCommandResponse;

/**
 * Wraps a {@link DeviceCommandResponse} so that information about the asset
 * associated with its assignment is available.
 */
public class DeviceCommandResponseWithAsset extends DeviceEventWithAsset implements IDeviceCommandResponse {

    /** Serial version UID */
    private static final long serialVersionUID = 6946071189269318157L;

    public DeviceCommandResponseWithAsset(IDeviceCommandResponse wrapped, IAssetManagement assetManagement)
	    throws SiteWhereException {
	super(wrapped, assetManagement);
    }

    /*
     * @see
     * com.sitewhere.spi.device.event.IDeviceCommandResponse#getOriginatingEventId()
     */
    @Override
    public UUID getOriginatingEventId() {
	return ((IDeviceCommandResponse) getWrapped()).getOriginatingEventId();
    }

    /*
     * @see
     * com.sitewhere.spi.device.event.IDeviceCommandResponse#getResponseEventId()
     */
    @Override
    public UUID getResponseEventId() {
	return ((IDeviceCommandResponse) getWrapped()).getResponseEventId();
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.sitewhere.spi.device.event.IDeviceCommandResponse#getResponse()
     */
    @Override
    public String getResponse() {
	return ((IDeviceCommandResponse) getWrapped()).getResponse();
    }
}