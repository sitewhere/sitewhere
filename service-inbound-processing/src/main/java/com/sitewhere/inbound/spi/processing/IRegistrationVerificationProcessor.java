/*
 * Copyright (c) SiteWhere, LLC. All rights reserved. http://www.sitewhere.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package com.sitewhere.inbound.spi.processing;

import com.sitewhere.grpc.kafka.model.KafkaModel.GInboundEventPayload;
import com.sitewhere.spi.SiteWhereException;
import com.sitewhere.spi.server.lifecycle.ITenantLifecycleComponent;

/**
 * Processing node which verifies that an incoming event belongs to a registered
 * device. If the event does not belong to a registered device, it is added to a
 * Kafka topic that can be processed by other services.
 * 
 * @author Derek
 */
public interface IRegistrationVerificationProcessor extends ITenantLifecycleComponent {

    /**
     * Process a single inbound event payload.
     * 
     * @param payload
     * @throws SiteWhereException
     */
    public void process(GInboundEventPayload payload) throws SiteWhereException;
}