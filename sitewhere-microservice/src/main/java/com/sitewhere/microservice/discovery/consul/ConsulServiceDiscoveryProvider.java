/*
 * Copyright (c) SiteWhere, LLC. All rights reserved. http://www.sitewhere.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package com.sitewhere.microservice.discovery.consul;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import com.google.common.net.HostAndPort;
import com.orbitz.consul.AgentClient;
import com.orbitz.consul.Consul;
import com.orbitz.consul.HealthClient;
import com.orbitz.consul.NotRegisteredException;
import com.orbitz.consul.model.agent.ImmutableRegistration;
import com.orbitz.consul.model.agent.Registration;
import com.orbitz.consul.model.health.ServiceHealth;
import com.sitewhere.microservice.discovery.ServiceNode;
import com.sitewhere.server.lifecycle.LifecycleComponent;
import com.sitewhere.spi.SiteWhereException;
import com.sitewhere.spi.microservice.IFunctionIdentifier;
import com.sitewhere.spi.microservice.discovery.IServiceDiscoveryProvider;
import com.sitewhere.spi.microservice.discovery.IServiceNode;
import com.sitewhere.spi.microservice.instance.IInstanceSettings;
import com.sitewhere.spi.server.lifecycle.ILifecycleProgressMonitor;

/**
 * Implementation of {@link IServiceDiscoveryProvider} that interacts with
 * Consul.
 * 
 * @author Derek
 */
public class ConsulServiceDiscoveryProvider extends LifecycleComponent implements IServiceDiscoveryProvider {

    /** Consul client */
    private Consul consulClient;

    public ConsulServiceDiscoveryProvider() {
    }

    /*
     * @see
     * com.sitewhere.server.lifecycle.LifecycleComponent#initialize(com.sitewhere.
     * spi.server.lifecycle.ILifecycleProgressMonitor)
     */
    @Override
    public void initialize(ILifecycleProgressMonitor monitor) throws SiteWhereException {
	// Create Consul client.
	IInstanceSettings settings = getMicroservice().getInstanceSettings();
	this.consulClient = Consul.builder()
		.withHostAndPort(HostAndPort.fromParts(settings.getConsulHost(), settings.getConsulPort())).build();
    }

    /*
     * @see
     * com.sitewhere.server.lifecycle.LifecycleComponent#start(com.sitewhere.spi.
     * server.lifecycle.ILifecycleProgressMonitor)
     */
    @Override
    public void start(ILifecycleProgressMonitor monitor) throws SiteWhereException {
    }

    /*
     * @see
     * com.sitewhere.server.lifecycle.LifecycleComponent#stop(com.sitewhere.spi.
     * server.lifecycle.ILifecycleProgressMonitor)
     */
    @Override
    public void stop(ILifecycleProgressMonitor monitor) throws SiteWhereException {
    }

    /*
     * @see com.sitewhere.spi.microservice.discovery.IServiceDiscoveryProvider#
     * registerService()
     */
    @Override
    public void registerService() throws SiteWhereException {
	getLogger().info("Registering Service: " + 
		getMicroservice().getIdentifier().getShortName() +
		" id: " +
		getMicroservice().getId().toString() +
		" address: " + 
		getMicroservice().getInstanceSettings().getServicePortName() +
		" port: " +
		getMicroservice().getInstanceSettings().getGrpcPort());	
	
	AgentClient agentClient = getConsulClient().agentClient();
	List<String> tags = new ArrayList<>();
	tags.add("microservice");
	tags.add(getMicroservice().getIdentifier().getShortName());
	Registration service = ImmutableRegistration.builder()
		.id(getMicroservice().getId().toString())
		.name(getMicroservice().getIdentifier().getShortName())
		.address(getMicroservice().getInstanceSettings().getServicePortName())
		.port(getMicroservice().getInstanceSettings().getGrpcPort())
		.check(Registration.RegCheck.ttl(30L))
		.tags(tags)
		.meta(Collections.singletonMap("version", getMicroservice().getVersion().getVersionIdentifier()))
		.build();
	agentClient.register(service);
    }

    /*
     * @see com.sitewhere.spi.microservice.discovery.IServiceDiscoveryProvider#
     * sendHeartbeat()
     */
    @Override
    public void sendHeartbeat() throws SiteWhereException {
	AgentClient agentClient = getConsulClient().agentClient();
	try {
	    agentClient.pass(getMicroservice().getId().toString());
	} catch (NotRegisteredException e) {
	    throw new SiteWhereException("Unable to send heartbeat.", e);
	}
    }

    /*
     * @see com.sitewhere.spi.microservice.discovery.IServiceDiscoveryProvider#
     * getNodesForFunction(com.sitewhere.spi.microservice.IFunctionIdentifier)
     */
    @Override
    public List<IServiceNode> getNodesForFunction(IFunctionIdentifier identifier) throws SiteWhereException {
	HealthClient healthClient = getConsulClient().healthClient();
	List<ServiceHealth> matches = healthClient.getHealthyServiceInstances(identifier.getShortName()).getResponse();
	List<IServiceNode> nodes = new ArrayList<>();
	for (ServiceHealth match : matches) {
	    String host = match.getService().getAddress();
	    ServiceNode node = new ServiceNode();
	    node.setAddress(host);
	    nodes.add(node);
	}
	return nodes;
    }

    protected Consul getConsulClient() {
	return consulClient;
    }
}