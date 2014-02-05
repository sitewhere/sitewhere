/*
 * DeviceCommandResponseWithAsset.java 
 * --------------------------------------------------------------------------------------
 * Copyright (c) Reveal Technologies, LLC. All rights reserved. http://www.reveal-tech.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package com.sitewhere.rest.model.device.asset;

import com.sitewhere.rest.model.device.event.DeviceCommandResponse;
import com.sitewhere.spi.SiteWhereException;
import com.sitewhere.spi.asset.IAssetModuleManager;
import com.sitewhere.spi.device.event.IDeviceCommandResponse;

/**
 * Wraps a {@link DeviceCommandResponse} so that information about the asset associated
 * with its assignment is available.
 * 
 * @author Derek
 */
public class DeviceCommandResponseWithAsset extends DeviceEventWithAsset implements IDeviceCommandResponse {

	public DeviceCommandResponseWithAsset(IDeviceCommandResponse wrapped, IAssetModuleManager assets)
			throws SiteWhereException {
		super(wrapped, assets);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.sitewhere.spi.device.event.IDeviceCommandResponse#getOriginatingEventId()
	 */
	@Override
	public String getOriginatingEventId() {
		return ((IDeviceCommandResponse) getWrapped()).getOriginatingEventId();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.sitewhere.spi.device.event.IDeviceCommandResponse#getResponseEventId()
	 */
	@Override
	public String getResponseEventId() {
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