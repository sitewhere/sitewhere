/*
 * Copyright (c) SiteWhere, LLC. All rights reserved. http://www.sitewhere.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package com.sitewhere.microservice.management;

import java.util.HashMap;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.sitewhere.grpc.client.ApiChannelNotAvailableException;
import com.sitewhere.grpc.client.microservice.MicroserviceManagementApiDemux;
import com.sitewhere.grpc.client.spi.client.IMicroserviceManagementApiDemux;
import com.sitewhere.server.lifecycle.LifecycleComponent;
import com.sitewhere.server.lifecycle.LifecycleProgressContext;
import com.sitewhere.server.lifecycle.LifecycleProgressMonitor;
import com.sitewhere.spi.SiteWhereException;
import com.sitewhere.spi.microservice.IMicroservice;
import com.sitewhere.spi.microservice.IMicroserviceManagement;
import com.sitewhere.spi.microservice.MicroserviceIdentifier;
import com.sitewhere.spi.microservice.management.IMicroserviceManagementCoordinator;
import com.sitewhere.spi.microservice.state.IInstanceMicroservice;
import com.sitewhere.spi.microservice.state.IInstanceTopologyEntry;
import com.sitewhere.spi.microservice.state.IInstanceTopologySnapshot;
import com.sitewhere.spi.microservice.state.IInstanceTopologyUpdatesListener;
import com.sitewhere.spi.microservice.state.IMicroserviceDetails;
import com.sitewhere.spi.microservice.state.IMicroserviceState;
import com.sitewhere.spi.microservice.state.ITenantEngineState;
import com.sitewhere.spi.server.lifecycle.ILifecycleProgressMonitor;

/**
 * Handles connectivity to GRPC management interfaces for all microservices
 * running for an instance.
 * 
 * @author Derek
 */
public class MicroserviceManagementCoordinator extends LifecycleComponent
	implements IMicroserviceManagementCoordinator, IInstanceTopologyUpdatesListener {

    /** Static logger instance */
    private static Log LOGGER = LogFactory.getLog(MicroserviceManagementCoordinator.class);

    /** Parent microservice */
    private IMicroservice microservice;

    /** API demuxes by service identifier */
    private Map<MicroserviceIdentifier, IMicroserviceManagementApiDemux> demuxesByServiceIdentifier = new HashMap<>();

    public MicroserviceManagementCoordinator(IMicroservice microservice) {
	this.microservice = microservice;
    }

    /*
     * @see
     * com.sitewhere.server.lifecycle.LifecycleComponent#start(com.sitewhere.spi.
     * server.lifecycle.ILifecycleProgressMonitor)
     */
    @Override
    public void start(ILifecycleProgressMonitor monitor) throws SiteWhereException {
	getMicroservice().getTopologyStateAggregator().addInstanceTopologyUpdatesListener(this);
	bootstrapFromExistingTopology();
    }

    /**
     * Since the topology updates manager may have already captured information
     * before the listener was registered, loop through existing members and
     * register them.
     */
    protected void bootstrapFromExistingTopology() {
	IInstanceTopologySnapshot topology = getMicroservice().getTopologyStateAggregator()
		.getInstanceTopologySnapshot();
	for (IInstanceTopologyEntry entry : topology.getTopologyEntriesByIdentifier().values()) {
	    for (IInstanceMicroservice microservice : entry.getMicroservicesByHostname().values()) {
		onMicroserviceAdded(microservice.getLatestState().getMicroservice());
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
	getMicroservice().getTopologyStateAggregator().removeInstanceTopologyUpdatesListener(this);
    }

    /*
     * @see
     * com.sitewhere.spi.microservice.management.IMicroserviceManagementCoordinator#
     * getMicroserviceManagement(com.sitewhere.spi.microservice.
     * MicroserviceIdentifier)
     */
    @Override
    public IMicroserviceManagement getMicroserviceManagement(MicroserviceIdentifier identifier)
	    throws SiteWhereException {
	IMicroserviceManagementApiDemux demux = getDemuxesByServiceIdentifier().get(identifier);
	if (demux == null) {
	    throw new ApiChannelNotAvailableException(
		    "No API channel available for '" + identifier + "' microservice management interface.");
	}
	return demux.getApiChannel();
    }

    /*
     * @see com.sitewhere.spi.microservice.state.IInstanceTopologyUpdatesListener#
     * onMicroserviceAdded(com.sitewhere.spi.microservice.state.IMicroserviceState)
     */
    @Override
    public void onMicroserviceAdded(IMicroserviceState state) {
	onMicroserviceAdded(state.getMicroservice());
    }

    /**
     * Process added microservice if no demux is currently available.
     * 
     * @param microservice
     */
    protected void onMicroserviceAdded(IMicroserviceDetails microservice) {
	IMicroserviceManagementApiDemux demux = getDemuxesByServiceIdentifier().get(microservice.getIdentifier());
	if (demux == null) {
	    demux = new MicroserviceManagementApiDemux(getMicroservice(), microservice.getIdentifier());
	    getDemuxesByServiceIdentifier().put(microservice.getIdentifier(), demux);
	    startDemux(demux, microservice);
	}
    }

    /*
     * @see com.sitewhere.spi.microservice.state.IInstanceTopologyUpdatesListener#
     * onMicroserviceUpdated(com.sitewhere.spi.microservice.state.
     * IMicroserviceState, com.sitewhere.spi.microservice.state.IMicroserviceState)
     */
    @Override
    public void onMicroserviceUpdated(IMicroserviceState previous, IMicroserviceState updated) {
    }

    /*
     * @see com.sitewhere.spi.microservice.state.IInstanceTopologyUpdatesListener#
     * onMicroserviceRemoved(com.sitewhere.spi.microservice.state.
     * IMicroserviceState)
     */
    @Override
    public void onMicroserviceRemoved(IMicroserviceState state) {
	MicroserviceIdentifier identifier = state.getMicroservice().getIdentifier();
	IMicroserviceManagementApiDemux demux = getDemuxesByServiceIdentifier().get(identifier);
	if (demux != null) {
	    getDemuxesByServiceIdentifier().remove(identifier);
	    stopDemux(demux, state.getMicroservice());
	}
    }

    /*
     * @see com.sitewhere.spi.microservice.state.IInstanceTopologyUpdatesListener#
     * onTenantEngineAdded(com.sitewhere.spi.microservice.state.IMicroserviceState,
     * com.sitewhere.spi.microservice.state.ITenantEngineState)
     */
    @Override
    public void onTenantEngineAdded(IMicroserviceState microservice, ITenantEngineState tenantEngine) {
    }

    /*
     * @see com.sitewhere.spi.microservice.state.IInstanceTopologyUpdatesListener#
     * onTenantEngineUpdated(com.sitewhere.spi.microservice.state.
     * IMicroserviceState, com.sitewhere.spi.microservice.state.ITenantEngineState,
     * com.sitewhere.spi.microservice.state.ITenantEngineState)
     */
    @Override
    public void onTenantEngineUpdated(IMicroserviceState microservice, ITenantEngineState previous,
	    ITenantEngineState updated) {
    }

    /*
     * @see com.sitewhere.spi.microservice.state.IInstanceTopologyUpdatesListener#
     * onTenantEngineRemoved(com.sitewhere.spi.microservice.state.
     * IMicroserviceState, com.sitewhere.spi.microservice.state.ITenantEngineState)
     */
    @Override
    public void onTenantEngineRemoved(IMicroserviceState microservice, ITenantEngineState tenantEngine) {
    }

    /**
     * Initialize and start the demux so that it can manage GRPC connections.
     * 
     * @param demux
     * @param details
     */
    protected void startDemux(IMicroserviceManagementApiDemux demux, IMicroserviceDetails details) {
	try {
	    MicroserviceIdentifier identifier = details.getIdentifier();
	    ILifecycleProgressMonitor monitor = new LifecycleProgressMonitor(
		    new LifecycleProgressContext(2, "Create microservice mangagement demux for '" + identifier + "'."),
		    microservice);
	    initializeNestedComponent(demux, monitor, true);
	    getLogger().info("Initialized microservice management demux for '" + identifier + "'.");

	    startNestedComponent(demux, monitor, true);
	    getLogger().info("Started microservice management demux for '" + identifier + "'.");
	} catch (SiteWhereException e) {
	    getLogger().error("Unable to initialize microservice management demux.", e);
	}
    }

    /**
     * Stop and terminate the demux to release underlying resources.
     * 
     * @param demux
     * @param details
     */
    protected void stopDemux(IMicroserviceManagementApiDemux demux, IMicroserviceDetails details) {
	try {
	    MicroserviceIdentifier identifier = details.getIdentifier();
	    ILifecycleProgressMonitor monitor = new LifecycleProgressMonitor(
		    new LifecycleProgressContext(2, "Shut down demux for '" + identifier + "'."), microservice);
	    stopNestedComponent(demux, monitor);
	    getLogger().info("Stopped microservice management demux for '" + identifier + "'.");

	    demux.lifecycleTerminate(monitor);
	    getLogger().info("Terminated microservice management demux for '" + identifier + "'.");
	} catch (SiteWhereException e) {
	    getLogger().error("Unable to shut down microservice management demux.", e);
	}
    }

    /*
     * @see com.sitewhere.spi.server.lifecycle.ILifecycleComponent#getLogger()
     */
    @Override
    public Log getLogger() {
	return LOGGER;
    }

    public IMicroservice getMicroservice() {
	return microservice;
    }

    public void setMicroservice(IMicroservice microservice) {
	this.microservice = microservice;
    }

    public Map<MicroserviceIdentifier, IMicroserviceManagementApiDemux> getDemuxesByServiceIdentifier() {
	return demuxesByServiceIdentifier;
    }

    public void setDemuxesByServiceIdentifier(
	    Map<MicroserviceIdentifier, IMicroserviceManagementApiDemux> demuxesByServiceIdentifier) {
	this.demuxesByServiceIdentifier = demuxesByServiceIdentifier;
    }
}