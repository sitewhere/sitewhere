/*
 * Copyright (c) SiteWhere, LLC. All rights reserved. http://www.sitewhere.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package com.sitewhere.rest.model.device.asset;

import com.sitewhere.rest.model.device.event.DeviceAlert;
import com.sitewhere.spi.SiteWhereException;
import com.sitewhere.spi.asset.IAssetManagement;
import com.sitewhere.spi.device.event.AlertLevel;
import com.sitewhere.spi.device.event.AlertSource;
import com.sitewhere.spi.device.event.IDeviceAlert;

/**
 * Wraps a {@link DeviceAlert} so that information about the asset associated
 * with its assignment is available.
 */
public class DeviceAlertWithAsset extends DeviceEventWithAsset implements IDeviceAlert {

    /** Serial version UID */
    private static final long serialVersionUID = -8737823382691759826L;

    public DeviceAlertWithAsset(IDeviceAlert wrapped, IAssetManagement assetManagement) throws SiteWhereException {
	super(wrapped, assetManagement);
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.sitewhere.spi.device.IDeviceAlert#getSource()
     */
    @Override
    public AlertSource getSource() {
	return ((IDeviceAlert) getWrapped()).getSource();
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.sitewhere.spi.device.IDeviceAlert#getLevel()
     */
    @Override
    public AlertLevel getLevel() {
	return ((IDeviceAlert) getWrapped()).getLevel();
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.sitewhere.spi.device.IDeviceAlert#getType()
     */
    @Override
    public String getType() {
	return ((IDeviceAlert) getWrapped()).getType();
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.sitewhere.spi.device.IDeviceAlert#getMessage()
     */
    @Override
    public String getMessage() {
	return ((IDeviceAlert) getWrapped()).getMessage();
    }
}