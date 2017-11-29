/*
 * Copyright (c) SiteWhere, LLC. All rights reserved. http://www.sitewhere.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package com.sitewhere.sources.deduplicator;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.sitewhere.server.lifecycle.TenantEngineLifecycleComponent;
import com.sitewhere.sources.spi.IDecodedDeviceRequest;
import com.sitewhere.sources.spi.IDeviceEventDeduplicator;
import com.sitewhere.spi.SiteWhereException;
import com.sitewhere.spi.device.event.IDeviceEvent;
import com.sitewhere.spi.device.event.IDeviceEventManagement;
import com.sitewhere.spi.device.event.request.IDeviceEventCreateRequest;
import com.sitewhere.spi.server.lifecycle.LifecycleComponentType;
import com.sitewhere.spi.tenant.ITenant;

/**
 * Implementation of {@link IDeviceEventDeduplicator} that checks the alternate
 * id (if present) in an event against the index already stored in the
 * datastore. If the alternate id is already present, the event is considered a
 * duplicate.
 * 
 * @author Derek
 */
public class AlternateIdDeduplicator extends TenantEngineLifecycleComponent implements IDeviceEventDeduplicator {

    /** Static logger instance */
    private static Logger LOGGER = LogManager.getLogger();

    public AlternateIdDeduplicator() {
	super(LifecycleComponentType.DeviceEventDeduplicator);
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.sitewhere.spi.device.communication.IDeviceEventDeduplicator#
     * isDuplicate(com.sitewhere.spi.device.communication.IDecodedDeviceRequest)
     */
    @Override
    public boolean isDuplicate(IDecodedDeviceRequest<?> request) throws SiteWhereException {
	if (request.getRequest() instanceof IDeviceEventCreateRequest) {
	    String alternateId = ((IDeviceEventCreateRequest) request.getRequest()).getAlternateId();
	    if (alternateId != null) {
		IDeviceEvent existing = getDeviceEventManagement(getTenantEngine().getTenant())
			.getDeviceEventByAlternateId(alternateId);
		if (existing != null) {
		    LOGGER.info("Found event with same alternate id. Will be treated as duplicate.");
		    return true;
		}
		return false;
	    }
	}
	return false;
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.sitewhere.spi.server.lifecycle.ILifecycleComponent#getLogger()
     */
    @Override
    public Logger getLogger() {
	return LOGGER;
    }

    private IDeviceEventManagement getDeviceEventManagement(ITenant tenant) {
	return null;
    }
}