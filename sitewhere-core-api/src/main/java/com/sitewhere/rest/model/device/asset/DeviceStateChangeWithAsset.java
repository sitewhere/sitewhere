/*
 * Copyright (c) SiteWhere, LLC. All rights reserved. http://www.sitewhere.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package com.sitewhere.rest.model.device.asset;

import java.util.Map;

import com.sitewhere.spi.SiteWhereException;
import com.sitewhere.spi.asset.IAssetModuleManager;
import com.sitewhere.spi.device.event.IDeviceStateChange;
import com.sitewhere.spi.device.event.state.StateChangeCategory;
import com.sitewhere.spi.device.event.state.StateChangeType;

/**
 * Wraps a {@link IDeviceStateChange} so that information about the asset
 * associated with its assignment is available.
 * 
 * @author Derek
 */
public class DeviceStateChangeWithAsset extends DeviceEventWithAsset implements IDeviceStateChange {

    /** Serial version UID */
    private static final long serialVersionUID = -8012486373686574551L;

    public DeviceStateChangeWithAsset(IDeviceStateChange wrapped, IAssetModuleManager assets)
	    throws SiteWhereException {
	super(wrapped, assets);
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.sitewhere.spi.device.event.IDeviceStateChange#getCategory()
     */
    @Override
    public StateChangeCategory getCategory() {
	return ((IDeviceStateChange) getWrapped()).getCategory();
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.sitewhere.spi.device.event.IDeviceStateChange#getType()
     */
    @Override
    public StateChangeType getType() {
	return ((IDeviceStateChange) getWrapped()).getType();
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.sitewhere.spi.device.event.IDeviceStateChange#getPreviousState()
     */
    @Override
    public String getPreviousState() {
	return ((IDeviceStateChange) getWrapped()).getPreviousState();
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.sitewhere.spi.device.event.IDeviceStateChange#getNewState()
     */
    @Override
    public String getNewState() {
	return ((IDeviceStateChange) getWrapped()).getNewState();
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.sitewhere.spi.device.event.IDeviceStateChange#getData()
     */
    @Override
    public Map<String, String> getData() {
	return ((IDeviceStateChange) getWrapped()).getData();
    }
}