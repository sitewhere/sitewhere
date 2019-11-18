/*
 * Copyright (c) SiteWhere, LLC. All rights reserved. http://www.sitewhere.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package com.sitewhere.device.spi.initializer;

import com.sitewhere.microservice.api.asset.IAssetManagement;
import com.sitewhere.microservice.api.device.IDeviceManagement;
import com.sitewhere.spi.SiteWhereException;
import com.sitewhere.spi.microservice.model.IModelInitializer;

/**
 * Initializes the device model with data needed to bootstrap the system.
 */
public interface IDeviceModelInitializer extends IModelInitializer {

    /**
     * Initialize the device model.
     * 
     * @param deviceManagement
     * @param assetManagement
     * @throws SiteWhereException
     */
    public void initialize(IDeviceManagement deviceManagement, IAssetManagement assetManagement)
	    throws SiteWhereException;
}