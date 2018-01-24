/*
 * Copyright (c) SiteWhere, LLC. All rights reserved. http://www.sitewhere.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package com.sitewhere.microservice.hazelcast.server;

import com.sitewhere.asset.AssetManagementDecorator;
import com.sitewhere.spi.asset.IAssetManagement;

/**
 * Wraps {@link IAssetManagement} implementation with cache support.
 * 
 * @author Derek
 */
public class CacheAwareAssetManagement extends AssetManagementDecorator {

    public CacheAwareAssetManagement(IAssetManagement delegate) {
	super(delegate);
    }
}