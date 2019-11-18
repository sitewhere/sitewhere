/*
 * Copyright (c) SiteWhere, LLC. All rights reserved. http://www.sitewhere.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package com.sitewhere.asset.spi;

import com.sitewhere.microservice.api.asset.IAssetManagement;
import com.sitewhere.spi.SiteWhereException;
import com.sitewhere.spi.microservice.model.IModelInitializer;

/**
 * Class that initializes the asset model with data needed to bootstrap the
 * system.
 */
public interface IAssetModelInitializer extends IModelInitializer {

    /**
     * Initialize the asset model.
     * 
     * @param assetManagement
     * @throws SiteWhereException
     */
    public void initialize(IAssetManagement assetManagement) throws SiteWhereException;
}