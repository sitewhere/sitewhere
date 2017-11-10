/*
 * Copyright (c) SiteWhere, LLC. All rights reserved. http://www.sitewhere.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package com.sitewhere.outbound;

import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.sitewhere.outbound.spi.IOutboundEventProcessor;
import com.sitewhere.outbound.spi.IOutboundProcessorsManager;
import com.sitewhere.server.lifecycle.TenantLifecycleComponent;

/**
 * Manages lifecycle of the list of outbound event processors configured for a
 * tenant.
 * 
 * @author Derek
 */
public class OutboundProcessorsManager extends TenantLifecycleComponent implements IOutboundProcessorsManager {

    /** Static logger instance */
    private static Logger LOGGER = LogManager.getLogger();

    /** List of event sources */
    private List<IOutboundEventProcessor> outboundEventProcessors;

    /*
     * @see com.sitewhere.spi.device.event.processor.IOutboundProcessorsManager#
     * getOutboundEventProcessors()
     */
    @Override
    public List<IOutboundEventProcessor> getOutboundEventProcessors() {
	return outboundEventProcessors;
    }

    public void setOutboundEventProcessors(List<IOutboundEventProcessor> outboundEventProcessors) {
	this.outboundEventProcessors = outboundEventProcessors;
    }

    /*
     * @see com.sitewhere.spi.server.lifecycle.ILifecycleComponent#getLogger()
     */
    @Override
    public Logger getLogger() {
	return LOGGER;
    }
}