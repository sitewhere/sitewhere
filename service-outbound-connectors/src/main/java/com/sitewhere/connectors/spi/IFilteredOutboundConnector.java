/*
 * Copyright (c) SiteWhere, LLC. All rights reserved. http://www.sitewhere.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package com.sitewhere.connectors.spi;

import java.util.List;

import com.sitewhere.spi.SiteWhereException;
import com.sitewhere.spi.device.event.kafka.IEnrichedEventPayload;

/**
 * Adds concept of filtering to outbound connectors.
 * 
 * @author Derek
 */
public interface IFilteredOutboundConnector extends IOutboundConnector {

    /**
     * Get the list of configured filters.
     * 
     * @return
     */
    public List<IDeviceEventFilter> getFilters();

    /**
     * Process a batch of event payloads after filters have been applied.
     * 
     * @param payloads
     * @throws SiteWhereException
     */
    public void processFilteredEventBatch(List<IEnrichedEventPayload> payloads) throws SiteWhereException;
}