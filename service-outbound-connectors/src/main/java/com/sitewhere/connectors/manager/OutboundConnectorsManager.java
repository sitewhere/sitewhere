/**
 * Copyright © 2014-2021 The SiteWhere Authors
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.sitewhere.connectors.manager;

import java.util.ArrayList;
import java.util.List;

import com.google.inject.Inject;
import com.sitewhere.connectors.configuration.OutboundConnectorsTenantConfiguration;
import com.sitewhere.connectors.kafka.KafkaOutboundConnectorHost;
import com.sitewhere.connectors.spi.IOutboundConnector;
import com.sitewhere.connectors.spi.IOutboundConnectorsManager;
import com.sitewhere.microservice.lifecycle.TenantEngineLifecycleComponent;
import com.sitewhere.microservice.util.MarshalUtils;
import com.sitewhere.spi.SiteWhereException;
import com.sitewhere.spi.microservice.lifecycle.ILifecycleProgressMonitor;

/**
 * Manages lifecycle of the list of outbound event connectors configured for a
 * tenant.
 */
public class OutboundConnectorsManager extends TenantEngineLifecycleComponent implements IOutboundConnectorsManager {

    /** Outbound connectors configuration */
    private OutboundConnectorsTenantConfiguration configuration;

    /** List of connectors */
    private List<IOutboundConnector> outboundConnectors;

    /** List of host wrappers for outbound connectors */
    private List<KafkaOutboundConnectorHost> connectorHosts = new ArrayList<KafkaOutboundConnectorHost>();

    @Inject
    public OutboundConnectorsManager(OutboundConnectorsTenantConfiguration configuration) {
	this.configuration = configuration;
    }

    /*
     * @see
     * com.sitewhere.server.lifecycle.LifecycleComponent#initialize(com.sitewhere.
     * spi.server.lifecycle.ILifecycleProgressMonitor)
     */
    @Override
    public void initialize(ILifecycleProgressMonitor monitor) throws SiteWhereException {
	getLogger().info(String.format("About to initialize outbound connectors manager with configuration:\n%s\n\n",
		MarshalUtils.marshalJsonAsPrettyString(getConfiguration())));
	this.outboundConnectors = OutboundConnectorsParser.parse(this, getConfiguration());

	getConnectorHosts().clear();
	getLogger().info(String.format("Initializing %d outbound connectors...", getOutboundConnectors().size()));
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
	getLogger().info(String.format("Starting %d outbound connectors...", getConnectorHosts().size()));
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
	getLogger().info(String.format("Stopping %d outbound connectors...", getConnectorHosts().size()));
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

    protected List<KafkaOutboundConnectorHost> getConnectorHosts() {
	return connectorHosts;
    }

    protected OutboundConnectorsTenantConfiguration getConfiguration() {
	return configuration;
    }
}