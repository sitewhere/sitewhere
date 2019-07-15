/*
 * Copyright (c) SiteWhere, LLC. All rights reserved. http://www.sitewhere.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package com.sitewhere.rest.model.device.asset;

import java.util.Map;
import java.util.UUID;

import com.sitewhere.spi.SiteWhereException;
import com.sitewhere.spi.asset.IAssetManagement;
import com.sitewhere.spi.device.event.CommandInitiator;
import com.sitewhere.spi.device.event.CommandTarget;
import com.sitewhere.spi.device.event.IDeviceCommandInvocation;

public class DeviceCommandInvocationWithAsset extends DeviceEventWithAsset implements IDeviceCommandInvocation {

    /** Serial version UID */
    private static final long serialVersionUID = 5274138683101218581L;

    public DeviceCommandInvocationWithAsset(IDeviceCommandInvocation wrapped, IAssetManagement assetManagement)
	    throws SiteWhereException {
	super(wrapped, assetManagement);
    }

    /*
     * @see com.sitewhere.spi.device.event.IDeviceCommandInvocation#getInitiator()
     */
    @Override
    public CommandInitiator getInitiator() {
	return ((IDeviceCommandInvocation) getWrapped()).getInitiator();
    }

    /*
     * @see com.sitewhere.spi.device.event.IDeviceCommandInvocation#getInitiatorId()
     */
    @Override
    public String getInitiatorId() {
	return ((IDeviceCommandInvocation) getWrapped()).getInitiatorId();
    }

    /*
     * @see com.sitewhere.spi.device.event.IDeviceCommandInvocation#getTarget()
     */
    @Override
    public CommandTarget getTarget() {
	return ((IDeviceCommandInvocation) getWrapped()).getTarget();
    }

    /*
     * @see com.sitewhere.spi.device.event.IDeviceCommandInvocation#getTargetId()
     */
    @Override
    public String getTargetId() {
	return ((IDeviceCommandInvocation) getWrapped()).getTargetId();
    }

    /*
     * @see
     * com.sitewhere.spi.device.event.IDeviceCommandInvocation#getDeviceCommandId()
     */
    @Override
    public UUID getDeviceCommandId() {
	return ((IDeviceCommandInvocation) getWrapped()).getDeviceCommandId();
    }

    /*
     * @see
     * com.sitewhere.spi.device.event.IDeviceCommandInvocation#getParameterValues()
     */
    @Override
    public Map<String, String> getParameterValues() {
	return ((IDeviceCommandInvocation) getWrapped()).getParameterValues();
    }
}