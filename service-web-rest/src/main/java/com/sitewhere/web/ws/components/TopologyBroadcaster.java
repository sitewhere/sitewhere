/*
 * Copyright (c) SiteWhere, LLC. All rights reserved. http://www.sitewhere.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package com.sitewhere.web.ws.components;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationListener;
import org.springframework.messaging.core.MessageSendingOperations;
import org.springframework.messaging.simp.broker.BrokerAvailabilityEvent;
import org.springframework.stereotype.Component;

import com.sitewhere.spi.microservice.state.IInstanceTopologyUpdatesListener;
import com.sitewhere.spi.microservice.state.IMicroserviceState;
import com.sitewhere.spi.microservice.state.ITenantEngineState;
import com.sitewhere.web.spi.microservice.IWebRestMicroservice;
import com.sitewhere.web.ws.components.topology.MicroserviceTopologyEvent;
import com.sitewhere.web.ws.components.topology.TenantTopologyEvent;
import com.sitewhere.web.ws.components.topology.TopologyEventType;

/**
 * Component that broadcasts topology updates to interested WebSocket
 * subscribers.
 * 
 * @author Derek Adams
 */
@Component
public class TopologyBroadcaster
	implements ApplicationListener<BrokerAvailabilityEvent>, IInstanceTopologyUpdatesListener {

    /** Static logger instance */
    private static Logger LOGGER = LogManager.getLogger();

    /** Base path for topology broadcasts */
    private static final String BASE_TOPOLOGY_PATH = "/topic/topology/";

    /** Base path for topology microservice broadcasts */
    private static final String BASE_MICROSERVICES_PATH = BASE_TOPOLOGY_PATH + "microservices/";

    @Autowired
    private IWebRestMicroservice microservice;

    @Autowired
    private MessageSendingOperations<String> messagingTemplate;

    /*
     * @see org.springframework.context.ApplicationListener#onApplicationEvent(org.
     * springframework.context.ApplicationEvent)
     */
    @Override
    public void onApplicationEvent(final BrokerAvailabilityEvent event) {
	getLogger().info("WebSocket broker detected as available.");
	getMicroservice().getTopologyStateAggregator().addInstanceTopologyUpdatesListener(this);
    }

    /*
     * @see com.sitewhere.spi.microservice.state.IInstanceTopologyUpdatesListener#
     * onMicroserviceAdded(com.sitewhere.spi.microservice.state.IMicroserviceState)
     */
    @Override
    public void onMicroserviceAdded(IMicroserviceState microservice) {
	getLogger().debug("Sending microservice added ...");
	deliver(getPathForMicroservice(microservice),
		new MicroserviceTopologyEvent(TopologyEventType.ADDED, microservice));
    }

    /*
     * @see com.sitewhere.spi.microservice.state.IInstanceTopologyUpdatesListener#
     * onMicroserviceUpdated(com.sitewhere.spi.microservice.state.
     * IMicroserviceState, com.sitewhere.spi.microservice.state.IMicroserviceState)
     */
    @Override
    public void onMicroserviceUpdated(IMicroserviceState previous, IMicroserviceState updated) {
	getLogger().debug("Sending microservice updated ...");
	deliver(getPathForMicroservice(updated), new MicroserviceTopologyEvent(TopologyEventType.UPDATED, updated));
    }

    /*
     * @see com.sitewhere.spi.microservice.state.IInstanceTopologyUpdatesListener#
     * onMicroserviceRemoved(com.sitewhere.spi.microservice.state.
     * IMicroserviceState)
     */
    @Override
    public void onMicroserviceRemoved(IMicroserviceState microservice) {
	getLogger().debug("Sending microservice removed ...");
	deliver(getPathForMicroservice(microservice),
		new MicroserviceTopologyEvent(TopologyEventType.REMOVED, microservice));
    }

    /*
     * @see com.sitewhere.spi.microservice.state.IInstanceTopologyUpdatesListener#
     * onTenantEngineAdded(com.sitewhere.spi.microservice.state.IMicroserviceState,
     * com.sitewhere.spi.microservice.state.ITenantEngineState)
     */
    @Override
    public void onTenantEngineAdded(IMicroserviceState microservice, ITenantEngineState tenantEngine) {
	getLogger().debug("Sending tenant engine added ...");
	deliver(getPathForTenant(tenantEngine), new TenantTopologyEvent(TopologyEventType.ADDED, tenantEngine));
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
	getLogger().debug("Sending tenant engine updated ...");
	deliver(getPathForTenant(updated), new TenantTopologyEvent(TopologyEventType.UPDATED, updated));
    }

    /*
     * @see com.sitewhere.spi.microservice.state.IInstanceTopologyUpdatesListener#
     * onTenantEngineRemoved(com.sitewhere.spi.microservice.state.
     * IMicroserviceState, com.sitewhere.spi.microservice.state.ITenantEngineState)
     */
    @Override
    public void onTenantEngineRemoved(IMicroserviceState microservice, ITenantEngineState tenantEngine) {
	getLogger().debug("Sending tenant engine removed ...");
	deliver(getPathForTenant(tenantEngine), new TenantTopologyEvent(TopologyEventType.REMOVED, tenantEngine));
    }

    /**
     * Get topic for broadcasting microservice updates.
     * 
     * @param microservice
     * @return
     */
    protected String getPathForMicroservice(IMicroserviceState state) {
	return BASE_MICROSERVICES_PATH + state.getMicroservice().getIdentifier();
    }

    /**
     * Get topic for broadcasting tenant engine updates.
     * 
     * @param tenantEngine
     * @return
     */
    protected String getPathForTenant(ITenantEngineState state) {
	return BASE_MICROSERVICES_PATH + state.getMicroservice().getIdentifier() + "/tenant/" + state.getTenantId();
    }

    /**
     * Deliver payload to topic using message template.
     * 
     * @param topic
     * @param payload
     */
    protected void deliver(String topic, Object payload) {
	getLogger().info("Broadcasting message to: " + topic);
	getMessagingTemplate().convertAndSend(topic, payload);
    }

    public IWebRestMicroservice getMicroservice() {
	return microservice;
    }

    public void setMicroservice(IWebRestMicroservice microservice) {
	this.microservice = microservice;
    }

    public MessageSendingOperations<String> getMessagingTemplate() {
	return messagingTemplate;
    }

    public void setMessagingTemplate(MessageSendingOperations<String> messagingTemplate) {
	this.messagingTemplate = messagingTemplate;
    }

    public Logger getLogger() {
	return LOGGER;
    }
}