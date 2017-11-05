/*
 * Copyright (c) SiteWhere, LLC. All rights reserved. http://www.sitewhere.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package com.sitewhere.inbound.spi.processing;

import com.sitewhere.spi.SiteWhereException;
import com.sitewhere.spi.microservice.kafka.payload.IInboundEventPayload;
import com.sitewhere.spi.server.lifecycle.ITenantLifecycleComponent;

/**
 * Processing node that receives events from the inbound processing stream and
 * attempts to enrich the event data by making the necessary requests to the
 * device management subsystem.
 * 
 * In cases where the hardware id for a device can not be found, the device is
 * considered unregistered and is put on a topic for the registration service.
 * Otherwise, details such as device and assignment information are added to the
 * payload and the enriched data is passed to another queue for further
 * processing.
 * 
 * @author Derek
 */
public interface IEventEnrichmentProcessor extends ITenantLifecycleComponent {

    /**
     * Process a single inbound event payload.
     * 
     * @param payload
     * @throws SiteWhereException
     */
    public void process(IInboundEventPayload payload) throws SiteWhereException;
}