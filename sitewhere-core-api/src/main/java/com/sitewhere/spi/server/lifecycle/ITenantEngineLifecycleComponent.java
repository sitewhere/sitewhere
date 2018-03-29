/*
 * Copyright (c) SiteWhere, LLC. All rights reserved. http://www.sitewhere.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package com.sitewhere.spi.server.lifecycle;

import com.codahale.metrics.Meter;
import com.codahale.metrics.Timer;
import com.sitewhere.spi.microservice.multitenant.IMicroserviceTenantEngine;

/**
 * Extends {@link ILifecycleComponent} with ability to access tenant engine.
 * 
 * @author Derek
 */
public interface ITenantEngineLifecycleComponent extends ILifecycleComponent {

    /**
     * Create a meter metric for the component (registered with the metric registry
     * for the microservice).
     * 
     * @param name
     * @return
     */
    public Meter createMeterMetric(String name);

    /**
     * Create a timer metric for the component (registered with the metric registry
     * for the microservice).
     * 
     * @param name
     * @return
     */
    public Timer createTimerMetric(String name);

    /**
     * Set tenant engine for component.
     * 
     * @param tenant
     */
    public void setTenantEngine(IMicroserviceTenantEngine tenantEngine);

    /**
     * Get tenant engine for component.
     * 
     * @return
     */
    public IMicroserviceTenantEngine getTenantEngine();
}