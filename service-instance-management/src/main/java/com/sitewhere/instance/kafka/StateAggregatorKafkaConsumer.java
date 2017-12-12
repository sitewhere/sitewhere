/*
 * Copyright (c) SiteWhere, LLC. All rights reserved. http://www.sitewhere.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package com.sitewhere.instance.kafka;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.sitewhere.instance.spi.kafka.IStateAggregatorKafkaConsumer;
import com.sitewhere.instance.spi.microservice.IInstanceManagementMicroservice;
import com.sitewhere.microservice.state.MicroserviceStateUpdatesKafkaConsumer;
import com.sitewhere.rest.model.microservice.state.InstanceMicroservice;
import com.sitewhere.rest.model.microservice.state.InstanceTenantEngine;
import com.sitewhere.rest.model.microservice.state.InstanceTopologyEntry;
import com.sitewhere.rest.model.microservice.state.InstanceTopologySnapshot;
import com.sitewhere.rest.model.microservice.state.MicroserviceState;
import com.sitewhere.spi.SiteWhereException;
import com.sitewhere.spi.microservice.IMicroservice;
import com.sitewhere.spi.microservice.state.IInstanceMicroservice;
import com.sitewhere.spi.microservice.state.IInstanceTenantEngine;
import com.sitewhere.spi.microservice.state.IMicroserviceDetails;
import com.sitewhere.spi.microservice.state.IMicroserviceState;
import com.sitewhere.spi.microservice.state.ITenantEngineState;
import com.sitewhere.spi.server.lifecycle.ILifecycleProgressMonitor;
import com.sitewhere.spi.server.lifecycle.LifecycleStatus;

/**
 * Kafka consumer that listens for state updates and aggregates them to produce
 * an estimated topology of the SiteWhere instance.
 * 
 * @author Derek
 */
public class StateAggregatorKafkaConsumer extends MicroserviceStateUpdatesKafkaConsumer
	implements IStateAggregatorKafkaConsumer {

    /** Static logger instance */
    private static Logger LOGGER = LogManager.getLogger();

    /** Interval at which topology snapshots are sent */
    private static final int AUTO_SNAPSHOT_INTERVAL = 15 * 1000;

    /** Current inferred topology */
    private Map<String, Map<String, IInstanceMicroservice>> topology = new HashMap<String, Map<String, IInstanceMicroservice>>();

    /** Executor service */
    private ExecutorService executor;

    public StateAggregatorKafkaConsumer(IMicroservice microservice) {
	super(microservice);
    }

    /*
     * @see com.sitewhere.microservice.kafka.MicroserviceKafkaConsumer#start(com.
     * sitewhere.spi.server.lifecycle.ILifecycleProgressMonitor)
     */
    @Override
    public void start(ILifecycleProgressMonitor monitor) throws SiteWhereException {
	super.start(monitor);
	executor = Executors.newSingleThreadExecutor();
	executor.execute(new TopologySnapshotUpdate());
    }

    /*
     * @see
     * com.sitewhere.microservice.kafka.MicroserviceKafkaConsumer#stop(com.sitewhere
     * .spi.server.lifecycle.ILifecycleProgressMonitor)
     */
    @Override
    public void stop(ILifecycleProgressMonitor monitor) throws SiteWhereException {
	super.stop(monitor);
	if (executor != null) {
	    executor.shutdownNow();
	}
    }

    /*
     * @see
     * com.sitewhere.spi.microservice.state.IMicroserviceStateUpdatesKafkaConsumer#
     * onMicroserviceStateUpdate(com.sitewhere.spi.microservice.state.
     * IMicroserviceState)
     */
    @Override
    public void onMicroserviceStateUpdate(IMicroserviceState state) {
	getOrCreateMicroservice(state);
    }

    /*
     * @see
     * com.sitewhere.spi.microservice.state.IMicroserviceStateUpdatesKafkaConsumer#
     * onTenantEngineStateUpdate(com.sitewhere.spi.microservice.state.
     * ITenantEngineState)
     */
    @Override
    public void onTenantEngineStateUpdate(ITenantEngineState state) {
	MicroserviceState placeholder = new MicroserviceState();
	placeholder.setMicroserviceDetails(state.getMicroserviceDetails());
	placeholder.setLifecycleStatus(LifecycleStatus.Started);

	IInstanceMicroservice microservice = getOrCreateMicroservice(placeholder);
	IInstanceTenantEngine engine = microservice.getTenantEngines().get(state.getTenantId());
	if (engine == null) {
	    InstanceTenantEngine created = new InstanceTenantEngine();
	    microservice.getTenantEngines().put(state.getTenantId(), created);
	    engine = created;
	}
	((InstanceTenantEngine) engine).setLatestState(state);
    }

    /**
     * Get microservice record for given state or create one if not already
     * registered.
     * 
     * @param state
     * @return
     */
    protected IInstanceMicroservice getOrCreateMicroservice(IMicroserviceState state) {
	IInstanceMicroservice microservice = getMicroservice(state);
	if (microservice == null) {
	    microservice = addMicroservice(state);
	    sendTopologySnapshot();
	}
	return microservice;
    }

    /**
     * Get existing microservice record from topology or null if no match.
     * 
     * @param state
     * @return
     */
    protected IInstanceMicroservice getMicroservice(IMicroserviceState state) {
	IMicroserviceDetails microservice = state.getMicroserviceDetails();
	Map<String, IInstanceMicroservice> services = getTopology().get(microservice.getIdentifier());
	if (services == null) {
	    return null;
	}
	return services.get(microservice.getHostname());
    }

    /**
     * Add record for microservice based on state.
     * 
     * @param state
     * @return
     */
    protected IInstanceMicroservice addMicroservice(IMicroserviceState state) {
	IMicroserviceDetails details = state.getMicroserviceDetails();
	Map<String, IInstanceMicroservice> services = getTopology().get(details.getIdentifier());
	if (services == null) {
	    services = new HashMap<String, IInstanceMicroservice>();
	    getTopology().put(details.getIdentifier(), services);
	}

	// Create record for microservice.
	InstanceMicroservice microservice = new InstanceMicroservice();
	microservice.setLatestState(state);
	services.put(details.getHostname(), microservice);

	return microservice;
    }

    /**
     * Create a topology snapshot from current inferred state.
     * 
     * @return
     */
    protected InstanceTopologySnapshot createTopologySnapshot() {
	InstanceTopologySnapshot snapshot = new InstanceTopologySnapshot();
	for (Map<String, IInstanceMicroservice> category : getTopology().values()) {
	    for (IInstanceMicroservice service : category.values()) {
		IMicroserviceState state = service.getLatestState();
		InstanceTopologyEntry entry = new InstanceTopologyEntry();
		entry.setMicroserviceDetails(state.getMicroserviceDetails());
		entry.setLastUpdated(System.currentTimeMillis());
		snapshot.getTopologyEntries().add(entry);
	    }
	}
	return snapshot;
    }

    /**
     * Send a snapshot of the current inferred topology.
     * 
     * @param update
     */
    protected void sendTopologySnapshot() {
	try {
	    ((IInstanceManagementMicroservice) getMicroservice()).getInstanceTopologyUpdatesKafkaProducer()
		    .send(createTopologySnapshot());
	} catch (SiteWhereException e) {
	    getLogger().warn("Unable to send topology snapshot.", e);
	}
    }

    /**
     * Sends instance topology snapshots at an interval.
     * 
     * @author Derek
     */
    private class TopologySnapshotUpdate implements Runnable {

	/*
	 * @see java.lang.Runnable#run()
	 */
	@Override
	public void run() {
	    while (true) {
		try {
		    sendTopologySnapshot();
		    Thread.sleep(AUTO_SNAPSHOT_INTERVAL);
		} catch (InterruptedException e) {
		    getLogger().warn("Snapshot update thread shutting down.");
		    return;
		} catch (Throwable t) {
		    getLogger().error("Error in topology snapshot delivery.");
		}
	    }
	}
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.sitewhere.spi.server.lifecycle.ILifecycleComponent#getLogger()
     */
    @Override
    public Logger getLogger() {
	return LOGGER;
    }

    public Map<String, Map<String, IInstanceMicroservice>> getTopology() {
	return topology;
    }

    public void setTopology(Map<String, Map<String, IInstanceMicroservice>> topology) {
	this.topology = topology;
    }
}