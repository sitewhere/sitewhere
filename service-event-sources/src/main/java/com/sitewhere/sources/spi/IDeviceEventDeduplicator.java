/*
 * Copyright (c) SiteWhere, LLC. All rights reserved. http://www.sitewhere.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package com.sitewhere.sources.spi;

import com.sitewhere.spi.SiteWhereException;
import com.sitewhere.spi.server.lifecycle.ITenantEngineLifecycleComponent;

/**
 * Provides support for filtering events that are duplicates of events already
 * in the system. The deduplication logic is implemented in subclasses.
 * 
 * @author Derek
 */
public interface IDeviceEventDeduplicator extends ITenantEngineLifecycleComponent {

    /**
     * Detects whether the given device event is a duplicate of another event in
     * the system.
     * 
     * @param request
     * @return
     * @throws SiteWhereException
     */
    public boolean isDuplicate(IDecodedDeviceRequest<?> request) throws SiteWhereException;
}