/*
 * Copyright (c) SiteWhere, LLC. All rights reserved. http://www.sitewhere.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package com.sitewhere.inbound.spi.microservice;

import com.sitewhere.inbound.spi.kafka.IDecodedEventsConsumer;
import com.sitewhere.spi.microservice.multitenant.IMicroserviceTenantEngine;

/**
 * Extends {@link IMicroserviceTenantEngine} with features specific to inbound
 * event processing.
 * 
 * @author Derek
 */
public interface IInboundProcessingTenantEngine extends IMicroserviceTenantEngine {

    /**
     * Get Kafka consumer that receives inbound decoded events for processing.
     * 
     * @return
     */
    public IDecodedEventsConsumer getDecodedEventsConsumer();
}