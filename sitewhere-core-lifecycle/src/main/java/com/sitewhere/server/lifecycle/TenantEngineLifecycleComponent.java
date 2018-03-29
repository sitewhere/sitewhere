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
import com.sitewhere.spi.SiteWhereException;
import com.sitewhere.spi.microservice.multitenant.IMicroserviceTenantEngine;
import com.sitewhere.spi.server.lifecycle.ILifecycleComponent;
import com.sitewhere.spi.server.lifecycle.ILifecycleProgressMonitor;
import com.sitewhere.spi.server.lifecycle.ITenantEngineLifecycleComponent;
import com.sitewhere.spi.server.lifecycle.LifecycleComponentType;

/**
 * Base class for implementing {@link ITenantEngineLifecycleComponent}.
 * 
 * @author Derek
 */
public abstract class TenantEngineLifecycleComponent extends LifecycleComponent
	implements ITenantEngineLifecycleComponent {

    /** Tenant engine associated with component */
    private IMicroserviceTenantEngine tenantEngine;

    public TenantEngineLifecycleComponent() {
	super(LifecycleComponentType.Other);
    }

    public TenantEngineLifecycleComponent(LifecycleComponentType type) {
	super(type);
    }

    /*
     * @see com.sitewhere.spi.server.lifecycle.ITenantEngineLifecycleComponent#
     * createMeterMetric(java.lang.String)
     */
    @Override
    public Meter createMeterMetric(String name) {
	return getTenantEngine().getMicroservice().getMetricRegistry().meter(name);
    }

    /*
     * @see com.sitewhere.spi.server.lifecycle.ITenantEngineLifecycleComponent#
     * createTimerMetric(java.lang.String)
     */
    @Override
    public Timer createTimerMetric(String name) {
	return getTenantEngine().getMicroservice().getMetricRegistry().timer(name);
    }

    /*
     * @see com.sitewhere.server.lifecycle.LifecycleComponent#
     * initializeNestedComponent(com.sitewhere.spi.server.lifecycle.
     * ILifecycleComponent,
     * com.sitewhere.spi.server.lifecycle.ILifecycleProgressMonitor, boolean)
     */
    @Override
    public void initializeNestedComponent(ILifecycleComponent component, ILifecycleProgressMonitor monitor,
	    boolean require) throws SiteWhereException {
	if (component instanceof ITenantEngineLifecycleComponent) {
	    ((ITenantEngineLifecycleComponent) component).setTenantEngine(getTenantEngine());
	}
	super.initializeNestedComponent(component, monitor, require);
    }

    /*
     * @see com.sitewhere.server.lifecycle.LifecycleComponent#startNestedComponent(
     * com.sitewhere.spi.server.lifecycle.ILifecycleComponent,
     * com.sitewhere.spi.server.lifecycle.ILifecycleProgressMonitor, boolean)
     */
    @Override
    public void startNestedComponent(ILifecycleComponent component, ILifecycleProgressMonitor monitor, boolean require)
	    throws SiteWhereException {
	if (component instanceof ITenantEngineLifecycleComponent) {
	    ((ITenantEngineLifecycleComponent) component).setTenantEngine(getTenantEngine());
	}
	super.startNestedComponent(component, monitor, require);
    }

    /*
     * @see com.sitewhere.spi.server.lifecycle.ITenantEngineLifecycleComponent#
     * getTenantEngine()
     */
    @Override
    public IMicroserviceTenantEngine getTenantEngine() {
	return tenantEngine;
    }

    /*
     * @see com.sitewhere.spi.server.lifecycle.ITenantEngineLifecycleComponent#
     * setTenantEngine(com.sitewhere.spi.microservice.multitenant.
     * IMicroserviceTenantEngine)
     */
    @Override
    public void setTenantEngine(IMicroserviceTenantEngine tenantEngine) {
	this.tenantEngine = tenantEngine;
    }
}