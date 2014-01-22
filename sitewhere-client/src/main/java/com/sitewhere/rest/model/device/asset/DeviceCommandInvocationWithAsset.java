/*
 * DeviceCommandInvocationWithAsset.java 
 * --------------------------------------------------------------------------------------
 * Copyright (c) Reveal Technologies, LLC. All rights reserved. http://www.reveal-tech.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package com.sitewhere.rest.model.device.asset;

import java.util.Map;

import com.sitewhere.spi.SiteWhereException;
import com.sitewhere.spi.asset.IAssetModuleManager;
import com.sitewhere.spi.device.event.CommandActor;
import com.sitewhere.spi.device.event.CommandStatus;
import com.sitewhere.spi.device.event.IDeviceCommandInvocation;

public class DeviceCommandInvocationWithAsset extends DeviceEventWithAsset implements
		IDeviceCommandInvocation {

	public DeviceCommandInvocationWithAsset(IDeviceCommandInvocation wrapped, IAssetModuleManager assets)
			throws SiteWhereException {
		super(wrapped, assets);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.sitewhere.spi.device.event.IDeviceCommandInvocation#getSourceActor()
	 */
	@Override
	public CommandActor getSourceActor() {
		return ((IDeviceCommandInvocation) getWrapped()).getSourceActor();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.sitewhere.spi.device.event.IDeviceCommandInvocation#getSourceId()
	 */
	@Override
	public String getSourceId() {
		return ((IDeviceCommandInvocation) getWrapped()).getSourceId();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.sitewhere.spi.device.event.IDeviceCommandInvocation#getTargetActor()
	 */
	@Override
	public CommandActor getTargetActor() {
		return ((IDeviceCommandInvocation) getWrapped()).getTargetActor();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.sitewhere.spi.device.event.IDeviceCommandInvocation#getTargetId()
	 */
	@Override
	public String getTargetId() {
		return ((IDeviceCommandInvocation) getWrapped()).getTargetId();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.sitewhere.spi.device.event.IDeviceCommandInvocation#getCommandToken()
	 */
	@Override
	public String getCommandToken() {
		return ((IDeviceCommandInvocation) getWrapped()).getCommandToken();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.sitewhere.spi.device.event.IDeviceCommandInvocation#getParameterValues()
	 */
	@Override
	public Map<String, String> getParameterValues() {
		return ((IDeviceCommandInvocation) getWrapped()).getParameterValues();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.sitewhere.spi.device.event.IDeviceCommandInvocation#getStatus()
	 */
	@Override
	public CommandStatus getStatus() {
		return ((IDeviceCommandInvocation) getWrapped()).getStatus();
	}
}