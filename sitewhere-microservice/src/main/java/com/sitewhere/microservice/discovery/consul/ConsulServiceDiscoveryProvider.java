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
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import com.google.common.net.HostAndPort;
import com.orbitz.consul.AgentClient;
import com.orbitz.consul.Consul;
import com.orbitz.consul.HealthClient;
import com.orbitz.consul.NotRegisteredException;
import com.orbitz.consul.model.agent.ImmutableRegistration;
import com.orbitz.consul.model.agent.Registration;
import com.orbitz.consul.model.health.HealthCheck;
import com.orbitz.consul.model.health.ServiceHealth;
import com.sitewhere.core.Boilerplate;
import com.sitewhere.microservice.discovery.ServiceNode;
import com.sitewhere.server.lifecycle.LifecycleComponent;
import com.sitewhere.spi.SiteWhereException;
import com.sitewhere.spi.microservice.IFunctionIdentifier;
import com.sitewhere.spi.microservice.discovery.IServiceDiscoveryProvider;
import com.sitewhere.spi.microservice.discovery.IServiceNode;
import com.sitewhere.spi.microservice.discovery.ServiceNodeStatus;
import com.sitewhere.spi.microservice.instance.IInstanceSettings;
import com.sitewhere.spi.server.lifecycle.ILifecycleProgressMonitor;

/**
 * Implementation of {@link IServiceDiscoveryProvider} that interacts with
 * Consul.
 * 
 * @author Derek
 */
public class ConsulServiceDiscoveryProvider extends LifecycleComponent implements IServiceDiscoveryProvider {

    /** Number of seconds to wait between registration retries */
    private static final int REGISTRATION_RETRY_INTERVAL_IN_SECS = 15;

    /** Consul client */
    private Consul consulClient;

    /** Handles registration in a separate thread */
    private ExecutorService registrationExecutor;

    /*
     * @see
     * com.sitewhere.server.lifecycle.LifecycleComponent#start(com.sitewhere.spi.
     * server.lifecycle.ILifecycleProgressMonitor)
     */
    @Override
    public void start(ILifecycleProgressMonitor monitor) throws SiteWhereException {
	if (getRegistrationExecutor() != null) {
	    getRegistrationExecutor().shutdownNow();
	}
	this.registrationExecutor = Executors.newSingleThreadExecutor();
    }

    /*
     * @see
     * com.sitewhere.server.lifecycle.LifecycleComponent#stop(com.sitewhere.spi.
     * server.lifecycle.ILifecycleProgressMonitor)
     */
    @Override
    public void stop(ILifecycleProgressMonitor monitor) throws SiteWhereException {
	if (getRegistrationExecutor() != null) {
	    getRegistrationExecutor().shutdownNow();
	}
    }

    /*
     * @see com.sitewhere.spi.microservice.discovery.IServiceDiscoveryProvider#
     * registerService()
     */
    @Override
    public void registerService() throws SiteWhereException {
	getRegistrationExecutor().execute(new BackgroundExecutor());
    }

    /**
     * Background thread which attempts to register microservice with Consul.
     * 
     * @author Derek
     */
    private class BackgroundExecutor implements Runnable {

	@Override
	public void run() {
	    getLogger().info("Attempting to register service with Consul...");
	    while (true) {
		try {
		    // Create Consul client.
		    IInstanceSettings settings = getMicroservice().getInstanceSettings();
		    setConsulClient(Consul.builder()
			    .withHostAndPort(HostAndPort.fromParts(settings.getConsulHost(), settings.getConsulPort()))
			    .build());

		    AgentClient agentClient = getConsulClient().agentClient();
		    List<String> tags = new ArrayList<>();
		    tags.add("microservice");
		    tags.add(getMicroservice().getIdentifier().getShortName());
		    Registration service = ImmutableRegistration.builder().id(getMicroservice().getId().toString())
			    .name(getMicroservice().getIdentifier().getShortName())
			    .address(getMicroservice().getHostname())
			    .port(getMicroservice().getInstanceSettings().getGrpcPort())
			    .check(Registration.RegCheck.ttl(30L)).tags(tags).meta(Collections.singletonMap("version",
				    getMicroservice().getVersion().getVersionIdentifier()))
			    .build();
		    agentClient.register(service);

		    // Log block to indicate registration.
		    List<String> messages = new ArrayList<String>();
		    messages.add("Registered Service with Consul");
		    messages.add("Identifier: " + getMicroservice().getIdentifier().getShortName());
		    messages.add("Registered Address: " + getMicroservice().getHostname());
		    messages.add(
			    "K8S Pod IP Address: " + getMicroservice().getInstanceSettings().getKubernetesPodAddress());
		    String message = Boilerplate.boilerplate(messages, "*");
		    getLogger().info(message);
		    return;
		} catch (Throwable t) {
		    getLogger().warn(String.format("Consul registration attempt failed. Will retry in %d seconds.",
			    REGISTRATION_RETRY_INTERVAL_IN_SECS), t);
		}
		try {
		    Thread.sleep(REGISTRATION_RETRY_INTERVAL_IN_SECS * 1000);
		} catch (InterruptedException t) {
		    getLogger().warn("Interrupted while attempting to register with Consul.");
		    return;
		}
	    }
	}
    }

    /*
     * @see com.sitewhere.spi.microservice.discovery.IServiceDiscoveryProvider#
     * sendHeartbeat()
     */
    @Override
    public void sendHeartbeat() throws SiteWhereException {
	if (getConsulClient() != null) {
	    AgentClient agentClient = getConsulClient().agentClient();
	    try {
		String serviceId = getMicroservice().getId().toString();
		if (agentClient.isRegistered(serviceId)) {
		    agentClient.pass(serviceId);
		}
	    } catch (NotRegisteredException e) {
		throw new SiteWhereException("Unable to send heartbeat.", e);
	    }
	} else {
	    getLogger().info("Skipping heartbeat. Consul client not connected.");
	}
    }

    /*
     * @see com.sitewhere.spi.microservice.discovery.IServiceDiscoveryProvider#
     * getNodesForFunction(com.sitewhere.spi.microservice.IFunctionIdentifier)
     */
    @Override
    public List<IServiceNode> getNodesForFunction(IFunctionIdentifier identifier) throws SiteWhereException {
	if (getConsulClient() != null) {
	    HealthClient healthClient = getConsulClient().healthClient();
	    List<ServiceHealth> matches = healthClient.getAllServiceInstances(identifier.getShortName()).getResponse();
	    List<IServiceNode> nodes = new ArrayList<>();
	    for (ServiceHealth match : matches) {
		String host = match.getService().getAddress();
		ServiceNode node = new ServiceNode();
		node.setAddress(host);
		node.setStatus(ServiceNodeStatus.Online);
		nodes.add(node);

		List<HealthCheck> checks = match.getChecks();
		for (HealthCheck check : checks) {
		    if (!"passing".equals(check.getStatus())) {
			node.setStatus(ServiceNodeStatus.Offline);
			getLogger().debug(String.format("Service for '%s' at %s is reporting a '%s' status!",
				identifier.getShortName(), match.getService().getAddress(), check.getStatus()));
		    }
		}
	    }
	    return nodes;
	} else {
	    List<IServiceNode> nodes = new ArrayList<>();
	    return nodes;
	}
    }

    protected Consul getConsulClient() {
	return consulClient;
    }

    protected void setConsulClient(Consul consulClient) {
	this.consulClient = consulClient;
    }

    protected ExecutorService getRegistrationExecutor() {
	return registrationExecutor;
    }
}