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

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.sitewhere.grpc.client.ApiChannelNotAvailableException;
import com.sitewhere.grpc.client.microservice.MicroserviceManagementApiDemux;
import com.sitewhere.grpc.client.spi.client.IMicroserviceManagementApiDemux;
import com.sitewhere.server.lifecycle.LifecycleComponent;
import com.sitewhere.server.lifecycle.LifecycleProgressContext;
import com.sitewhere.server.lifecycle.LifecycleProgressMonitor;
import com.sitewhere.spi.SiteWhereException;
import com.sitewhere.spi.microservice.IMicroservice;
import com.sitewhere.spi.microservice.IMicroserviceManagement;
import com.sitewhere.spi.microservice.management.IMicroserviceManagementCoordinator;
import com.sitewhere.spi.microservice.state.IInstanceTopologyEntry;
import com.sitewhere.spi.microservice.state.IInstanceTopologySnapshot;
import com.sitewhere.spi.microservice.state.IInstanceTopologySnapshotsListener;
import com.sitewhere.spi.microservice.state.IMicroserviceDetails;
import com.sitewhere.spi.server.lifecycle.ILifecycleProgressMonitor;

/**
 * Handles connectivity to GRPC management interfaces for all microservices
 * running for an instance.
 * 
 * @author Derek
 */
public class MicroserviceManagementCoordinator extends LifecycleComponent
	implements IMicroserviceManagementCoordinator, IInstanceTopologySnapshotsListener {

    /** Static logger instance */
    private static Logger LOGGER = LogManager.getLogger();

    /** Parent microservice */
    private IMicroservice microservice;

    /** API demuxes by service identifier */
    private Map<String, IMicroserviceManagementApiDemux> demuxesByServiceIdentifier = new HashMap<>();

    /** Latest instance topology snapshot */
    private IInstanceTopologySnapshot instanceTopologySnapshot;

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
	getMicroservice().getInstanceTopologyUpdatesManager().addListener(this);
    }

    /*
     * @see
     * com.sitewhere.server.lifecycle.LifecycleComponent#stop(com.sitewhere.spi.
     * server.lifecycle.ILifecycleProgressMonitor)
     */
    @Override
    public void stop(ILifecycleProgressMonitor monitor) throws SiteWhereException {
	getMicroservice().getInstanceTopologyUpdatesManager().removeListener(this);
    }

    /*
     * @see com.sitewhere.spi.microservice.management.IMicroserviceManager#
     * getMicroserviceManagement(java.lang.String)
     */
    @Override
    public IMicroserviceManagement getMicroserviceManagement(String identifier) throws SiteWhereException {
	IMicroserviceManagementApiDemux demux = getDemuxesByServiceIdentifier().get(identifier);
	if (demux == null) {
	    throw new ApiChannelNotAvailableException(
		    "No API channel available for '" + identifier + "' microservice management interface.");
	}
	return demux.getApiChannel();
    }

    /*
     * @see com.sitewhere.spi.microservice.state.IInstanceTopologySnapshotsListener#
     * onInstanceTopologySnapshot(com.sitewhere.spi.microservice.state.
     * IInstanceTopologySnapshot)
     */
    @Override
    public void onInstanceTopologySnapshot(IInstanceTopologySnapshot snapshot) {
	this.instanceTopologySnapshot = snapshot;
	for (IInstanceTopologyEntry entry : snapshot.getTopologyEntries()) {
	    IMicroserviceDetails microservice = entry.getMicroserviceDetails();
	    IMicroserviceManagementApiDemux demux = getDemuxesByServiceIdentifier().get(microservice.getIdentifier());
	    if (demux == null) {
		demux = new MicroserviceManagementApiDemux(getMicroservice(), microservice.getIdentifier());
		getDemuxesByServiceIdentifier().put(microservice.getIdentifier(), demux);
		startDemux(demux, entry);
	    }
	}
    }

    /**
     * Initialize and start the demux so that it can manage GRPC connections.
     * 
     * @param demux
     * @param entry
     */
    protected void startDemux(IMicroserviceManagementApiDemux demux, IInstanceTopologyEntry entry) {
	try {
	    String identifier = entry.getMicroserviceDetails().getIdentifier();
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

    /*
     * @see
     * com.sitewhere.spi.microservice.management.IMicroserviceManagementCoordinator#
     * getInstanceTopologySnapshot()
     */
    @Override
    public IInstanceTopologySnapshot getInstanceTopologySnapshot() {
	return instanceTopologySnapshot;
    }

    public void setInstanceTopologySnapshot(IInstanceTopologySnapshot instanceTopologySnapshot) {
	this.instanceTopologySnapshot = instanceTopologySnapshot;
    }

    /*
     * @see com.sitewhere.spi.server.lifecycle.ILifecycleComponent#getLogger()
     */
    @Override
    public Logger getLogger() {
	return LOGGER;
    }

    public IMicroservice getMicroservice() {
	return microservice;
    }

    public void setMicroservice(IMicroservice microservice) {
	this.microservice = microservice;
    }

    public Map<String, IMicroserviceManagementApiDemux> getDemuxesByServiceIdentifier() {
	return demuxesByServiceIdentifier;
    }

    public void setDemuxesByServiceIdentifier(Map<String, IMicroserviceManagementApiDemux> demuxesByServiceIdentifier) {
	this.demuxesByServiceIdentifier = demuxesByServiceIdentifier;
    }
}