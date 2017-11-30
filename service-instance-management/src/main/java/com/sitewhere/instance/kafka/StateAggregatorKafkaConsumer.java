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

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.sitewhere.instance.spi.kafka.IStateAggregatorKafkaConsumer;
import com.sitewhere.instance.spi.microservice.IInstanceManagementMicroservice;
import com.sitewhere.microservice.state.MicroserviceStateUpdatesKafkaConsumer;
import com.sitewhere.rest.model.microservice.state.InstanceMicroservice;
import com.sitewhere.rest.model.microservice.state.InstanceTenantEngine;
import com.sitewhere.rest.model.microservice.state.InstanceTopologyUpdate;
import com.sitewhere.rest.model.microservice.state.MicroserviceState;
import com.sitewhere.spi.SiteWhereException;
import com.sitewhere.spi.microservice.IMicroservice;
import com.sitewhere.spi.microservice.state.IInstanceMicroservice;
import com.sitewhere.spi.microservice.state.IInstanceTenantEngine;
import com.sitewhere.spi.microservice.state.IInstanceTopologyUpdate;
import com.sitewhere.spi.microservice.state.IMicroserviceState;
import com.sitewhere.spi.microservice.state.ITenantEngineState;
import com.sitewhere.spi.microservice.state.InstanceTopologyUpdateType;
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

    /** Current inferred topology */
    private Map<String, Map<String, IInstanceMicroservice>> topology = new HashMap<String, Map<String, IInstanceMicroservice>>();

    public StateAggregatorKafkaConsumer(IMicroservice microservice) {
	super(microservice);
    }

    /*
     * @see
     * com.sitewhere.spi.microservice.state.IMicroserviceStateUpdatesKafkaConsumer#
     * onMicroserviceStateUpdate(com.sitewhere.spi.microservice.state.
     * IMicroserviceState)
     */
    @Override
    public void onMicroserviceStateUpdate(IMicroserviceState state) {
	IInstanceMicroservice microservice = getOrCreateMicroservice(state);
	detectMicroserviceStateChange(microservice, state);
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
	placeholder.setMicroserviceIdentifier(state.getMicroserviceIdentifier());
	placeholder.setMicroserviceHostname(state.getMicroserviceHostname());
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
	Map<String, IInstanceMicroservice> services = getTopology().get(state.getMicroserviceIdentifier());
	if (services == null) {
	    return null;
	}
	return services.get(state.getMicroserviceHostname());
    }

    /**
     * Add record for microservice based on state.
     * 
     * @param state
     * @return
     */
    protected IInstanceMicroservice addMicroservice(IMicroserviceState state) {
	Map<String, IInstanceMicroservice> services = getTopology().get(state.getMicroserviceIdentifier());
	if (services == null) {
	    services = new HashMap<String, IInstanceMicroservice>();
	    getTopology().put(state.getMicroserviceIdentifier(), services);
	}

	// Create record for microservice.
	InstanceMicroservice microservice = new InstanceMicroservice();
	microservice.setLatestState(state);
	services.put(state.getMicroserviceHostname(), microservice);

	// Send a topology update.
	InstanceTopologyUpdate update = new InstanceTopologyUpdate();
	update.setMicroserviceIdentifier(state.getMicroserviceIdentifier());
	update.setMicroserviceHostname(state.getMicroserviceHostname());
	update.setType(InstanceTopologyUpdateType.MicroserviceStarted);
	sendTopologyUpdate(update);

	return microservice;
    }

    /**
     * Detect microservice state changes.
     * 
     * @param microservice
     * @param updated
     */
    protected void detectMicroserviceStateChange(IInstanceMicroservice microservice, IMicroserviceState updated) {
	IMicroserviceState last = microservice.getLatestState();
	if (updated.getLifecycleStatus() != last.getLifecycleStatus()) {
	    if (updated.getLifecycleStatus() == LifecycleStatus.Stopped) {
		InstanceTopologyUpdate update = new InstanceTopologyUpdate();
		update.setMicroserviceIdentifier(updated.getMicroserviceIdentifier());
		update.setMicroserviceHostname(updated.getMicroserviceHostname());
		update.setType(InstanceTopologyUpdateType.MicroserviceStopped);
		sendTopologyUpdate(update);
	    }
	}
	((InstanceMicroservice) microservice).setLatestState(updated);
    }

    /**
     * Send a topology update.
     * 
     * @param update
     */
    protected void sendTopologyUpdate(IInstanceTopologyUpdate update) {
	try {
	    getLogger().debug("Sending topology update -> " + update.getMicroserviceIdentifier() + ":"
		    + update.getMicroserviceHostname() + ":" + update.getType().name());
	    ((IInstanceManagementMicroservice) getMicroservice()).getInstanceTopologyUpdatesKafkaProducer()
		    .send(update);
	} catch (SiteWhereException e) {
	    getLogger().warn("Unable to send topology update.", e);
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