/*
 * Copyright (c) SiteWhere, LLC. All rights reserved. http://www.sitewhere.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package com.sitewhere.spi.microservice;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ExecutorService;

import com.sitewhere.spi.SiteWhereException;
import com.sitewhere.spi.microservice.configuration.model.IConfigurationModel;
import com.sitewhere.spi.microservice.grpc.IMicroserviceManagementGrpcServer;
import com.sitewhere.spi.microservice.instance.IInstanceSettings;
import com.sitewhere.spi.microservice.kafka.IKafkaTopicNaming;
import com.sitewhere.spi.microservice.metrics.IMetricsServer;
import com.sitewhere.spi.microservice.scripting.IScriptTemplateManager;
import com.sitewhere.spi.microservice.security.ISystemUser;
import com.sitewhere.spi.microservice.security.ITokenManagement;
import com.sitewhere.spi.microservice.state.IMicroserviceDetails;
import com.sitewhere.spi.microservice.state.IMicroserviceState;
import com.sitewhere.spi.microservice.state.ITenantEngineState;
import com.sitewhere.spi.server.lifecycle.ILifecycleComponent;
import com.sitewhere.spi.system.IVersion;
import com.sitewhere.spi.tenant.ITenantManagement;

import io.fabric8.kubernetes.client.DefaultKubernetesClient;
import io.fabric8.kubernetes.client.informers.SharedInformerFactory;
import io.sitewhere.k8s.crd.ISiteWhereKubernetesClient;
import io.sitewhere.k8s.crd.instance.SiteWhereInstance;
import io.sitewhere.k8s.crd.instance.dataset.InstanceDatasetTemplate;
import io.sitewhere.k8s.crd.microservice.SiteWhereMicroservice;
import io.sitewhere.k8s.crd.tenant.SiteWhereTenant;
import io.sitewhere.k8s.crd.tenant.engine.SiteWhereTenantEngine;

/**
 * Functionality common to all SiteWhere microservices.
 */
public interface IMicroservice<T extends IFunctionIdentifier>
	extends ILifecycleComponent, IMicroserviceClassification<T> {

    /**
     * Get unique id.
     * 
     * @return
     */
    public UUID getId();

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
    public T getIdentifier();

    /**
     * Get assigned hostname.
     * 
     * @return
     */
    public String getHostname();

    /**
     * Indicates whether the microservice is global in scope.
     * 
     * @return
     */
    public boolean isGlobal();

    /**
     * Get properties that should be passed into Spring context.
     * 
     * @return
     */
    public Map<String, Object> getSpringProperties();

    /**
     * Build configuration model.
     * 
     * @return
     */
    public IConfigurationModel buildConfigurationModel();

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
    public IMicroserviceDetails getMicroserviceDetails();

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
     * Get tenant management API.
     * 
     * @return
     */
    public ITenantManagement getTenantManagement();

    /**
     * Get tenant management API with caching enabled.
     * 
     * @return
     */
    public ITenantManagement getCachedTenantManagement();

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
     * Get manager for script templates which provide examples of
     * microservice-specific scripting funcionality.
     * 
     * @return
     */
    public IScriptTemplateManager getScriptTemplateManager();

    /**
     * Get analytics processor.
     * 
     * @return
     */
    public IMicroserviceAnalytics getMicroserviceAnalytics();

    /**
     * Code executed after microservice has been started.
     */
    public void afterMicroserviceStarted();

    /**
     * Kubernetes for local connection.
     * 
     * @return
     */
    public DefaultKubernetesClient getKubernetesClient();

    /**
     * Get SiteWhere k8s client wrapper.
     * 
     * @return
     */
    public ISiteWhereKubernetesClient getSiteWhereKubernetesClient();

    /**
     * Create Kubernetes resource controllers which pull from shared informer
     * factory.
     * 
     * @param informers
     * @throws SiteWhereException
     */
    public void createKubernetesResourceControllers(SharedInformerFactory informers) throws SiteWhereException;

    /**
     * Get metrics server.
     * 
     * @return
     */
    public IMetricsServer getMetricsServer();

    /**
     * Get local microservice configuration.
     * 
     * @param instance
     * @return
     */
    public byte[] getLocalConfiguration(SiteWhereInstance instance);

    /**
     * Wait for SiteWhere instance configuration metadata to become initialized
     * before proceeding.
     * 
     * @throws SiteWhereException
     */
    public void waitForInstanceInitialization() throws SiteWhereException;

    /**
     * Get executor service that handles long-running microservice operations.
     * 
     * @return
     */
    public ExecutorService getMicroserviceOperationsService();

    /**
     * Loads latest instance configuration from Kubernetes.
     * 
     * @return
     * @throws SiteWhereException
     */
    public SiteWhereInstance loadInstanceConfiguration() throws SiteWhereException;

    /**
     * Loads latest instance dataset template from Kubernetes.
     * 
     * @param instance
     * @return
     * @throws SiteWhereException
     */
    public InstanceDatasetTemplate loadInstanceDatasetTemplate(SiteWhereInstance instance) throws SiteWhereException;

    /**
     * Update instance configuration.
     * 
     * @param instance
     * @throws SiteWhereException
     */
    public SiteWhereInstance updateInstanceConfiguration(SiteWhereInstance instance) throws SiteWhereException;

    /**
     * Get tenant engine configuration.
     * 
     * @param tenant
     * @param microservice
     * @return
     * @throws SiteWhereException
     */
    public SiteWhereTenantEngine getTenantEngineConfiguration(SiteWhereTenant tenant,
	    SiteWhereMicroservice microservice) throws SiteWhereException;

    /**
     * Set configuration for a tenant engine.
     * 
     * @param tenant
     * @param microservice
     * @param configuration
     * @throws SiteWhereException
     */
    public SiteWhereTenantEngine setTenantEngineConfiguration(SiteWhereTenant tenant,
	    SiteWhereMicroservice microservice, String configuration) throws SiteWhereException;
}