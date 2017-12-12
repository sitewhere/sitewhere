/*
 * Copyright (c) SiteWhere, LLC. All rights reserved. http://www.sitewhere.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package com.sitewhere.spi.microservice;

import com.sitewhere.spi.SiteWhereException;
import com.sitewhere.spi.microservice.configuration.IZookeeperManager;
import com.sitewhere.spi.microservice.configuration.model.IConfigurationModel;
import com.sitewhere.spi.microservice.grpc.IMicroserviceManagementGrpcServer;
import com.sitewhere.spi.microservice.hazelcast.IHazelcastManager;
import com.sitewhere.spi.microservice.instance.IInstanceSettings;
import com.sitewhere.spi.microservice.kafka.IKafkaTopicNaming;
import com.sitewhere.spi.microservice.security.ISystemUser;
import com.sitewhere.spi.microservice.security.ITokenManagement;
import com.sitewhere.spi.microservice.state.IInstanceTopologyUpdatesManager;
import com.sitewhere.spi.microservice.state.IMicroserviceDetails;
import com.sitewhere.spi.microservice.state.IMicroserviceState;
import com.sitewhere.spi.microservice.state.IMicroserviceStateUpdatesKafkaProducer;
import com.sitewhere.spi.microservice.state.ITenantEngineState;
import com.sitewhere.spi.server.lifecycle.ILifecycleComponent;
import com.sitewhere.spi.system.IVersion;
import com.sitewhere.spi.tracing.ITracerProvider;

/**
 * Functionality common to all SiteWhere microservices.
 * 
 * @author Derek
 */
public interface IMicroservice extends ILifecycleComponent, ITracerProvider {

    /**
     * Get name shown for microservice.
     * 
     * @return
     */
    public String getName();

    /**
     * Get version information.
     * 
     * @return
     */
    public IVersion getVersion();

    /**
     * Get unique microservice identifier.
     * 
     * @return
     */
    public String getIdentifier();

    /**
     * Get assigned hostname.
     * 
     * @return
     * @throws SiteWhereException
     */
    public String getHostname() throws SiteWhereException;

    /**
     * Indicates whether the microservice is global in scope.
     * 
     * @return
     */
    public boolean isGlobal();

    /**
     * Get configuration model.
     * 
     * @return
     */
    public IConfigurationModel getConfigurationModel();

    /**
     * Get details that identify and describe the microservice.
     * 
     * @return
     * @throws SiteWhereException
     */
    public IMicroserviceDetails getMicroserviceDetails() throws SiteWhereException;

    /**
     * Get current state for microservice.
     * 
     * @return
     * @throws SiteWhereException
     */
    public IMicroserviceState getCurrentState() throws SiteWhereException;

    /**
     * Called when state of managed tenant engine is updated.
     * 
     * @param state
     */
    public void onTenantEngineStateChanged(ITenantEngineState state);

    /**
     * Get settings for SiteWhere instance.
     * 
     * @return
     */
    public IInstanceSettings getInstanceSettings();

    /**
     * Get token management interface.
     * 
     * @return
     */
    public ITokenManagement getTokenManagement();

    /**
     * Get system superuser.
     * 
     * @return
     */
    public ISystemUser getSystemUser();

    /**
     * Get Kafka topic naming helper.
     * 
     * @return
     */
    public IKafkaTopicNaming getKafkaTopicNaming();

    /**
     * Get microservice management GRPC server.
     * 
     * @return
     */
    public IMicroserviceManagementGrpcServer getMicroserviceManagementGrpcServer();

    /**
     * Get Kafka producer for reporting state updates in microservice and managed
     * tenant engines.
     * 
     * @return
     */
    public IMicroserviceStateUpdatesKafkaProducer getStateUpdatesKafkaProducer();

    /**
     * Get mangager that allows for listening to instance topology updates.
     * 
     * @return
     */
    public IInstanceTopologyUpdatesManager getInstanceTopologyUpdatesManager();

    /**
     * Code executed after microservice has been started.
     */
    public void afterMicroserviceStarted();

    /**
     * Get Zookeeper node path for instance.
     * 
     * @return
     */
    public String getInstanceZkPath();

    /**
     * Get Zookeeper path for instance configuration.
     * 
     * @return
     */
    public String getInstanceConfigurationPath();

    /**
     * Get path for marker used to indicate instance is bootstrapped.
     * 
     * @return
     * @throws SiteWhereException
     */
    public String getInstanceBootstrappedMarker() throws SiteWhereException;

    /**
     * Get Zookeeper manager.
     * 
     * @return
     */
    public IZookeeperManager getZookeeperManager();

    /**
     * Get manager for Hazelcast instance.
     * 
     * @return
     */
    public IHazelcastManager getHazelcastManager();

    /**
     * Wait for SiteWhere instance configuration metadata to become initialized
     * before proceeding.
     * 
     * @throws SiteWhereException
     */
    public void waitForInstanceInitialization() throws SiteWhereException;
}