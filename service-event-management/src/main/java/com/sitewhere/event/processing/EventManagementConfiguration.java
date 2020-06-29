/*
 * Copyright (c) SiteWhere, LLC. All rights reserved. http://www.sitewhere.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package com.sitewhere.event.processing;

import com.sitewhere.event.spi.processing.IEventManagementConfiguration;

/**
 * Model object for event management configuration.
 * 
 * @author Derek
 */
public class EventManagementConfiguration implements IEventManagementConfiguration {

    /** Number of threads used for processing decoded events */
    private int processingThreadCount = 25;

    /*
     * @see com.sitewhere.inbound.spi.processing.IInboundProcessingConfiguration#
     * getProcessingThreadCount()
     */
    @Override
    public int getProcessingThreadCount() {
	return processingThreadCount;
    }

    public void setProcessingThreadCount(int processingThreadCount) {
	this.processingThreadCount = processingThreadCount;
    }
}