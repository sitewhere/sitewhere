/*
 * Copyright (c) SiteWhere, LLC. All rights reserved. http://www.sitewhere.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package com.sitewhere.server.lifecycle;

import com.codahale.metrics.Meter;
import com.codahale.metrics.Timer;
import com.sitewhere.spi.microservice.multitenant.IMicroserviceTenantEngine;
import com.sitewhere.spi.server.lifecycle.ITenantEngineLifecycleComponent;

public class TenantEngineLifecycleComponentDecorator<T extends ITenantEngineLifecycleComponent>
	extends LifecycleComponentDecorator<T> implements ITenantEngineLifecycleComponent {

    public TenantEngineLifecycleComponentDecorator(T delegate) {
	super(delegate);
    }

    /*
     * @see com.sitewhere.spi.server.lifecycle.ITenantEngineLifecycleComponent#
     * createMeterMetric(java.lang.String)
     */
    @Override
    public Meter createMeterMetric(String name) {
	return getDelegate().createMeterMetric(name);
    }

    /*
     * @see com.sitewhere.spi.server.lifecycle.ITenantEngineLifecycleComponent#
     * createTimerMetric(java.lang.String)
     */
    @Override
    public Timer createTimerMetric(String name) {
	return getDelegate().createTimerMetric(name);
    }

    /*
     * @see com.sitewhere.spi.server.lifecycle.ITenantEngineLifecycleComponent#
     * setTenantEngine(com.sitewhere.spi.microservice.multitenant.
     * IMicroserviceTenantEngine)
     */
    @Override
    public void setTenantEngine(IMicroserviceTenantEngine tenantEngine) {
	getDelegate().setTenantEngine(tenantEngine);
    }

    /*
     * @see com.sitewhere.spi.server.lifecycle.ITenantEngineLifecycleComponent#
     * getTenantEngine()
     */
    @Override
    public IMicroserviceTenantEngine getTenantEngine() {
	return getDelegate().getTenantEngine();
    }
}