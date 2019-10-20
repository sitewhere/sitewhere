/*
 * Copyright (c) SiteWhere, LLC. All rights reserved. http://www.sitewhere.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package com.sitewhere.microservice.configuration;

import java.util.ArrayList;
import java.util.List;

import com.sitewhere.server.lifecycle.LifecycleComponent;
import com.sitewhere.spi.SiteWhereException;
import com.sitewhere.spi.microservice.configuration.IConfigurationListener;
import com.sitewhere.spi.microservice.configuration.IConfigurationMonitor;
import com.sitewhere.spi.server.lifecycle.LifecycleComponentType;

/**
 * Monitors Kubernetes resources for changes in microservice configuration.
 */
public class KubernetesConfigurationMonitor extends LifecycleComponent implements IConfigurationMonitor {

    /** Listeners */
    private List<IConfigurationListener> listeners = new ArrayList<>();

    public KubernetesConfigurationMonitor() {
	super(LifecycleComponentType.Other);
    }

    /*
     * @see com.sitewhere.spi.microservice.configuration.IConfigurationMonitor#
     * getConfigurationDataFor(java.lang.String)
     */
    @Override
    public byte[] getConfigurationDataFor(String path) throws SiteWhereException {
	return null;
    }

    /*
     * @see com.sitewhere.spi.microservice.configuration.IConfigurationMonitor#
     * getListeners()
     */
    @Override
    public List<IConfigurationListener> getListeners() {
	return listeners;
    }
}
