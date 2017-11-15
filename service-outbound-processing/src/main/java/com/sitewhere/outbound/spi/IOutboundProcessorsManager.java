/*
 * Copyright (c) SiteWhere, LLC. All rights reserved. http://www.sitewhere.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package com.sitewhere.outbound.spi;

import java.util.List;

import com.sitewhere.outbound.spi.microservice.IOutboundProcessingTenantEngine;
import com.sitewhere.spi.server.lifecycle.ITenantLifecycleComponent;

/**
 * Manages the list of outbound processors for a tenant.
 * 
 * @author Derek
 */
public interface IOutboundProcessorsManager extends ITenantLifecycleComponent {

    /**
     * Set the parent tenant engine.
     * 
     * @param tenantEngine
     */
    public void setTenantEngine(IOutboundProcessingTenantEngine tenantEngine);

    /**
     * Get list of outbound event processors.
     * 
     * @return
     */
    public List<IOutboundEventProcessor> getOutboundEventProcessors();
}