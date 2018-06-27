/*
 * Copyright (c) SiteWhere, LLC. All rights reserved. http://www.sitewhere.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package com.sitewhere.connectors;

import java.util.ArrayList;
import java.util.List;

import com.sitewhere.connectors.kafka.KafkaOutboundConnectorHost;
import com.sitewhere.connectors.spi.IOutboundConnector;
import com.sitewhere.connectors.spi.IOutboundConnectorsManager;
import com.sitewhere.server.lifecycle.TenantEngineLifecycleComponent;
import com.sitewhere.spi.SiteWhereException;
import com.sitewhere.spi.server.lifecycle.ILifecycleProgressMonitor;

/**
 * Manages lifecycle of the list of outbound event connectors configured for a
 * tenant.
 * 
 * @author Derek
 */
public class OutboundConnectorsManager extends TenantEngineLifecycleComponent implements IOutboundConnectorsManager {

    /** List of connectors */
    private List<IOutboundConnector> outboundConnectors;

    /** List of host wrappers for outbound connectors */
    private List<KafkaOutboundConnectorHost> connectorHosts = new ArrayList<KafkaOutboundConnectorHost>();

    /*
     * @see
     * com.sitewhere.server.lifecycle.LifecycleComponent#initialize(com.sitewhere.
     * spi.server.lifecycle.ILifecycleProgressMonitor)
     */
    @Override
    public void initialize(ILifecycleProgressMonitor monitor) throws SiteWhereException {
	getConnectorHosts().clear();
	for (IOutboundConnector processor : getOutboundConnectors()) {
	    try {
		// Create host for managing outbound connector.
		KafkaOutboundConnectorHost host = new KafkaOutboundConnectorHost(processor);
		initializeNestedComponent(host, monitor, true);
		getConnectorHosts().add(host);
	    } catch (SiteWhereException e) {
		getLogger().error("Error initializing outbound connector.", e);
	    }
	}
    }

    /*
     * @see
     * com.sitewhere.server.lifecycle.LifecycleComponent#start(com.sitewhere.spi.
     * server.lifecycle.ILifecycleProgressMonitor)
     */
    @Override
    public void start(ILifecycleProgressMonitor monitor) throws SiteWhereException {
	for (KafkaOutboundConnectorHost host : getConnectorHosts()) {
	    try {
		startNestedComponent(host, monitor, true);
	    } catch (SiteWhereException e) {
		getLogger().error("Error starting outbound connector.", e);
	    }
	}
    }

    /*
     * @see
     * com.sitewhere.server.lifecycle.LifecycleComponent#stop(com.sitewhere.spi.
     * server.lifecycle.ILifecycleProgressMonitor)
     */
    @Override
    public void stop(ILifecycleProgressMonitor monitor) throws SiteWhereException {
	for (KafkaOutboundConnectorHost host : getConnectorHosts()) {
	    try {
		stopNestedComponent(host, monitor);
	    } catch (SiteWhereException e) {
		getLogger().error("Error stopping outbound connector.", e);
	    }
	}
    }

    /*
     * @see
     * com.sitewhere.connectors.spi.IOutboundConnectorsManager#getOutboundConnectors
     * ()
     */
    @Override
    public List<IOutboundConnector> getOutboundConnectors() {
	return outboundConnectors;
    }

    public void setOutboundConnectors(List<IOutboundConnector> outboundConnectors) {
	this.outboundConnectors = outboundConnectors;
    }

    public List<KafkaOutboundConnectorHost> getConnectorHosts() {
	return connectorHosts;
    }

    public void setConnectorHosts(List<KafkaOutboundConnectorHost> connectorHosts) {
	this.connectorHosts = connectorHosts;
    }
}