/*
 * Copyright (c) SiteWhere, LLC. All rights reserved. http://www.sitewhere.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package com.sitewhere.inbound.configuration;

import com.sitewhere.inbound.spi.processing.IInboundProcessingConfiguration;
import com.sitewhere.spi.microservice.multitenant.ITenantEngineConfiguration;

/**
 * Maps inbound processing tenant engine YAML configuration to objects.
 */
public class InboundProcessingTenantConfiguration
	implements ITenantEngineConfiguration, IInboundProcessingConfiguration {

    /** Default number of threads used for concurrent processing of events */
    private static final int DEFAULT_PROCESSING_THREAD_COUNT = 10;

    /** Number of threads used for concurrent processing of events */
    private int processingThreadCount = DEFAULT_PROCESSING_THREAD_COUNT;

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
