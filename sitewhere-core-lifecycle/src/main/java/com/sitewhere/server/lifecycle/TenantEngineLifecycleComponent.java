/*
 * Copyright (c) SiteWhere, LLC. All rights reserved. http://www.sitewhere.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package com.sitewhere.server.lifecycle;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import com.sitewhere.spi.SiteWhereException;
import com.sitewhere.spi.microservice.multitenant.IMicroserviceTenantEngine;
import com.sitewhere.spi.server.lifecycle.ILifecycleComponent;
import com.sitewhere.spi.server.lifecycle.ILifecycleProgressMonitor;
import com.sitewhere.spi.server.lifecycle.ITenantEngineLifecycleComponent;
import com.sitewhere.spi.server.lifecycle.LifecycleComponentType;

import io.prometheus.client.Counter;
import io.prometheus.client.Gauge;
import io.prometheus.client.Histogram;

/**
 * Base class for implementing {@link ITenantEngineLifecycleComponent}.
 */
public abstract class TenantEngineLifecycleComponent extends LifecycleComponent
	implements ITenantEngineLifecycleComponent {

    /** Namespace prefix added for metrics */
    private static final String METRIC_PREFIX = "sitewhere_";

    /** Metrics label for microservice type */
    private static final String LABEL_MICROSERVICE = "microservice";

    /** Metrics label for microservice pod IP */
    private static final String LABEL_POD_IP = "pod";

    /** Metrics label for microservice tenant id */
    private static final String LABEL_TENANT_ID = "tenant";

    /** Tenant engine associated with component */
    private IMicroserviceTenantEngine tenantEngine;

    public TenantEngineLifecycleComponent() {
	super(LifecycleComponentType.Other);
    }

    public TenantEngineLifecycleComponent(LifecycleComponentType type) {
	super(type);
    }

    /**
     * Creates a gauge metric with labels for slicing by microservice and tenant.
     * 
     * @param name
     * @param description
     * @param labelNames
     * @return
     */
    public static Gauge createGaugeMetric(String name, String description, String... labelNames) {
	return Gauge.build().name(METRIC_PREFIX + name).help(description).labelNames(mergeLabels(labelNames))
		.register();
    }

    /**
     * Creates a counter metric with labels for slicing by microservice and tenant.
     * 
     * @param name
     * @param description
     * @param labelNames
     * @return
     */
    public static Counter createCounterMetric(String name, String description, String... labelNames) {
	return Counter.build().name(METRIC_PREFIX + name).help(description).labelNames(mergeLabels(labelNames))
		.register();
    }

    /**
     * Creates a histogram metric with labels for slicing by microservice and
     * tenant.
     * 
     * @param name
     * @param description
     * @param labelNames
     * @return
     */
    public static Histogram createHistogramMetric(String name, String description, String... labelNames) {
	return Histogram.build().name(METRIC_PREFIX + name).help(description).labelNames(mergeLabels(labelNames))
		.register();
    }

    /**
     * Merge standard SiteWhere labels before extras.
     * 
     * @param labelNames
     * @return
     */
    protected static String[] mergeLabels(String... labelNames) {
	List<String> all = new ArrayList<>();
	all.addAll(Arrays.asList(labelNames));
	all.add(0, LABEL_TENANT_ID);
	all.add(0, LABEL_POD_IP);
	all.add(0, LABEL_MICROSERVICE);
	return all.toArray(new String[all.size()]);
    }

    /*
     * @see com.sitewhere.spi.server.lifecycle.ITenantEngineLifecycleComponent#
     * buildLabels(java.lang.String[])
     */
    @Override
    public String[] buildLabels(String... labels) {
	List<String> all = new ArrayList<>();
	all.addAll(Arrays.asList(labels));
	all.add(0, getTenantEngine().getTenant().getToken());
	all.add(0, getMicroservice().getInstanceSettings().getKubernetesPodAddress().get());
	all.add(0, getMicroservice().getIdentifier().getPath());
	return all.toArray(new String[all.size()]);
    }

    /*
     * @see
     * com.sitewhere.server.lifecycle.LifecycleComponent#initializeNestedComponent(
     * com.sitewhere.spi.server.lifecycle.ILifecycleComponent,
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