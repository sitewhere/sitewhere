/*
 * Copyright (c) SiteWhere, LLC. All rights reserved. http://www.sitewhere.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package com.sitewhere.tenant.microservice;

import java.io.File;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.NoSuchBeanDefinitionException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;

import com.sitewhere.microservice.GlobalMicroservice;
import com.sitewhere.microservice.hazelcast.cache.CacheAwareTenantManagement;
import com.sitewhere.server.lifecycle.CompositeLifecycleStep;
import com.sitewhere.spi.SiteWhereException;
import com.sitewhere.spi.microservice.IMicroserviceIdentifiers;
import com.sitewhere.spi.microservice.configuration.model.IConfigurationModel;
import com.sitewhere.spi.microservice.multitenant.ITenantTemplate;
import com.sitewhere.spi.microservice.spring.TenantManagementBeans;
import com.sitewhere.spi.server.lifecycle.ICompositeLifecycleStep;
import com.sitewhere.spi.server.lifecycle.ILifecycleProgressMonitor;
import com.sitewhere.spi.tenant.ITenantAdministration;
import com.sitewhere.spi.tenant.ITenantManagement;
import com.sitewhere.tenant.configuration.TenantManagementModelProvider;
import com.sitewhere.tenant.grpc.TenantManagementGrpcServer;
import com.sitewhere.tenant.kafka.TenantBootstrapModelConsumer;
import com.sitewhere.tenant.kafka.TenantManagementKafkaTriggers;
import com.sitewhere.tenant.kafka.TenantModelProducer;
import com.sitewhere.tenant.persistence.TenantManagementAccessor;
import com.sitewhere.tenant.spi.grpc.ITenantManagementGrpcServer;
import com.sitewhere.tenant.spi.kafka.ITenantBootstrapModelConsumer;
import com.sitewhere.tenant.spi.kafka.ITenantModelProducer;
import com.sitewhere.tenant.spi.microservice.ITenantManagementMicroservice;
import com.sitewhere.tenant.spi.templates.ITenantTemplateManager;

/**
 * Microservice that provides tenant management functionality.
 * 
 * @author Derek
 */
public class TenantManagementMicroservice extends GlobalMicroservice
	implements ITenantManagementMicroservice, ITenantAdministration {

    /** Static logger instance */
    private static Log LOGGER = LogFactory.getLog(TenantManagementMicroservice.class);

    /** Microservice name */
    private static final String NAME = "Tenant Management";

    /** Tenant management configuration file name */
    private static final String CONFIGURATION_PATH = IMicroserviceIdentifiers.TENANT_MANAGEMENT + ".xml";

    /** Root folder for instance templates */
    private static final String TEMPLATES_ROOT = "/templates";

    /** Responds to tenant management GRPC requests */
    private ITenantManagementGrpcServer tenantManagementGrpcServer;

    /** Accessor for tenant management implementation */
    private TenantManagementAccessor tenantManagementAccessor = new TenantManagementAccessor();

    /** Tenant management implementation */
    private ITenantManagement tenantManagement;

    /** Tenant template manager */
    @Autowired
    private ITenantTemplateManager tenantTemplateManager;

    /** Reflects tenant model updates to Kafka topic */
    private ITenantModelProducer tenantModelProducer;

    /** Watches tenant model updates and bootstraps new tenants */
    private ITenantBootstrapModelConsumer tenantBootstrapModelConsumer;

    /*
     * (non-Javadoc)
     * 
     * @see com.sitewhere.microservice.spi.IMicroservice#getName()
     */
    @Override
    public String getName() {
	return NAME;
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.sitewhere.microservice.spi.IMicroservice#getIdentifier()
     */
    @Override
    public String getIdentifier() {
	return IMicroserviceIdentifiers.TENANT_MANAGEMENT;
    }

    /*
     * @see com.sitewhere.spi.microservice.IMicroservice#isGlobal()
     */
    @Override
    public boolean isGlobal() {
	return true;
    }

    /*
     * @see com.sitewhere.spi.microservice.IMicroservice#buildConfigurationModel()
     */
    @Override
    public IConfigurationModel buildConfigurationModel() {
	return new TenantManagementModelProvider().buildModel();
    }

    /*
     * @see com.sitewhere.spi.microservice.configuration.IConfigurableMicroservice#
     * getConfigurationPath()
     */
    @Override
    public String getConfigurationPath() throws SiteWhereException {
	return CONFIGURATION_PATH;
    }

    /*
     * @see com.sitewhere.microservice.configuration.ConfigurableMicroservice#
     * configurationInitialize(org.springframework.context.ApplicationContext,
     * org.springframework.context.ApplicationContext,
     * com.sitewhere.spi.server.lifecycle.ILifecycleProgressMonitor)
     */
    @Override
    public void configurationInitialize(ApplicationContext global, ApplicationContext local,
	    ILifecycleProgressMonitor monitor) throws SiteWhereException {
	super.configurationInitialize(global, local, monitor);
	this.tenantManagement = initializeTenantManagement(local);

	// Composite step for initializing microservice.
	ICompositeLifecycleStep init = new CompositeLifecycleStep("Initialize configuration for " + getName());

	// Initialize tenant management implementation.
	init.addInitializeStep(this, getTenantManagement(), true);

	// Execute initialization steps.
	init.execute(monitor);
    }

    /**
     * Initialize tenant management implementation from context bean and wrap it
     * with triggers to broadcast model updates via Kafka.
     * 
     * @param context
     * @return
     * @throws SiteWhereException
     */
    protected ITenantManagement initializeTenantManagement(ApplicationContext context) throws SiteWhereException {
	try {
	    ITenantManagement bean = (ITenantManagement) context.getBean(TenantManagementBeans.BEAN_TENANT_MANAGEMENT);
	    ITenantManagement cached = new CacheAwareTenantManagement(bean, this);
	    return new TenantManagementKafkaTriggers(cached, getTenantModelProducer());
	} catch (NoSuchBeanDefinitionException e) {
	    throw new SiteWhereException("Tenant management bean not found.", e);
	}
    }

    /*
     * @see com.sitewhere.microservice.configuration.ConfigurableMicroservice#
     * configurationStart(org.springframework.context.ApplicationContext,
     * org.springframework.context.ApplicationContext,
     * com.sitewhere.spi.server.lifecycle.ILifecycleProgressMonitor)
     */
    @Override
    public void configurationStart(ApplicationContext global, ApplicationContext local,
	    ILifecycleProgressMonitor monitor) throws SiteWhereException {
	super.configurationStart(global, local, monitor);
	getTenantManagementAccessor().setDelegate(getTenantManagement());

	// Composite step for starting microservice.
	ICompositeLifecycleStep start = new CompositeLifecycleStep("Start " + getComponentName());

	// Start tenant mangement persistence.
	start.addStartStep(this, getTenantManagement(), true);

	// Execute initialization steps.
	start.execute(monitor);
    }

    /*
     * @see com.sitewhere.microservice.configuration.ConfigurableMicroservice#
     * configurationStop(org.springframework.context.ApplicationContext,
     * org.springframework.context.ApplicationContext,
     * com.sitewhere.spi.server.lifecycle.ILifecycleProgressMonitor)
     */
    @Override
    public void configurationStop(ApplicationContext global, ApplicationContext local,
	    ILifecycleProgressMonitor monitor) throws SiteWhereException {
	// Composite step for stopping microservice.
	ICompositeLifecycleStep stop = new CompositeLifecycleStep("Stop " + getName());

	// Stop tenant management persistence.
	stop.addStopStep(this, getTenantManagement());

	// Execute shutdown steps.
	stop.execute(monitor);
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * com.sitewhere.microservice.spi.IGlobalMicroservice#microserviceInitialize
     * (com.sitewhere.spi.server.lifecycle.ILifecycleProgressMonitor)
     */
    @Override
    public void microserviceInitialize(ILifecycleProgressMonitor monitor) throws SiteWhereException {
	this.tenantModelProducer = new TenantModelProducer(this);
	this.tenantBootstrapModelConsumer = new TenantBootstrapModelConsumer(this);
	this.tenantManagementGrpcServer = new TenantManagementGrpcServer(this, getTenantManagementAccessor(), this);

	// Composite step for initializing microservice.
	ICompositeLifecycleStep init = new CompositeLifecycleStep("Initialize " + getName());

	// Initialize tenant template manager.
	init.addInitializeStep(this, getTenantTemplateManager(), true);

	// Initialize tenant management GRPC server.
	init.addInitializeStep(this, getTenantManagementGrpcServer(), true);

	// Initialize tenant model producer.
	init.addInitializeStep(this, getTenantModelProducer(), true);

	// Initialize tenant bootstrap model consumer.
	init.addInitializeStep(this, getTenantBootstrapModelConsumer(), true);

	// Execute initialization steps.
	init.execute(monitor);
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * com.sitewhere.microservice.spi.IGlobalMicroservice#microserviceStart(com.
     * sitewhere.spi.server.lifecycle.ILifecycleProgressMonitor)
     */
    @Override
    public void microserviceStart(ILifecycleProgressMonitor monitor) throws SiteWhereException {
	// Composite step for starting microservice.
	ICompositeLifecycleStep start = new CompositeLifecycleStep("Start " + getComponentName());

	// Start tenant template manager.
	start.addStartStep(this, getTenantTemplateManager(), true);

	// Start GRPC server.
	start.addStartStep(this, getTenantManagementGrpcServer(), true);

	// Start tenant model producer.
	start.addStartStep(this, getTenantModelProducer(), true);

	// Start tenant bootstrap model consumer.
	start.addStartStep(this, getTenantBootstrapModelConsumer(), true);

	// Execute initialization steps.
	start.execute(monitor);
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.sitewhere.microservice.spi.IGlobalMicroservice#microserviceStop(com.
     * sitewhere.spi.server.lifecycle.ILifecycleProgressMonitor)
     */
    @Override
    public void microserviceStop(ILifecycleProgressMonitor monitor) throws SiteWhereException {
	// Composite step for stopping microservice.
	ICompositeLifecycleStep stop = new CompositeLifecycleStep("Stop " + getName());

	// Stop tenant bootstrap model consumer.
	stop.addStopStep(this, getTenantBootstrapModelConsumer());

	// Stop tenant model producer.
	stop.addStopStep(this, getTenantModelProducer());

	// Stop GRPC manager.
	stop.addStopStep(this, getTenantManagementGrpcServer());

	// Stop tenant template manager.
	stop.addStopStep(this, getTenantTemplateManager());

	// Execute shutdown steps.
	stop.execute(monitor);
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.sitewhere.tenant.spi.microservice.ITenantManagementMicroservice#
     * getTenantTemplatesRoot()
     */
    @Override
    public File getTenantTemplatesRoot() throws SiteWhereException {
	File templates = new File(TEMPLATES_ROOT);
	if (!templates.exists()) {
	    throw new SiteWhereException("Templates folder not found in Docker image.");
	}
	return templates;
    }

    /*
     * @see com.sitewhere.spi.tenant.ITenantAdministration#getTenantTemplates()
     */
    @Override
    public List<ITenantTemplate> getTenantTemplates() throws SiteWhereException {
	return getTenantTemplateManager().getTenantTemplates();
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.sitewhere.tenant.spi.microservice.ITenantManagement#
     * getTenantManagementGrpcServer()
     */
    @Override
    public ITenantManagementGrpcServer getTenantManagementGrpcServer() {
	return tenantManagementGrpcServer;
    }

    public void setTenantManagementGrpcServer(ITenantManagementGrpcServer tenantManagementGrpcServer) {
	this.tenantManagementGrpcServer = tenantManagementGrpcServer;
    }

    /*
     * @see com.sitewhere.tenant.spi.microservice.ITenantManagementMicroservice#
     * getTenantManagement()
     */
    @Override
    public ITenantManagement getTenantManagement() {
	return tenantManagement;
    }

    public void setTenantManagement(ITenantManagement tenantManagement) {
	this.tenantManagement = tenantManagement;
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.sitewhere.tenant.spi.microservice.ITenantManagementMicroservice#
     * getTenantTemplateManager()
     */
    @Override
    public ITenantTemplateManager getTenantTemplateManager() {
	return tenantTemplateManager;
    }

    public void setTenantTemplateManager(ITenantTemplateManager tenantTemplateManager) {
	this.tenantTemplateManager = tenantTemplateManager;
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.sitewhere.tenant.spi.microservice.ITenantManagementMicroservice#
     * getTenantModelProducer()
     */
    @Override
    public ITenantModelProducer getTenantModelProducer() {
	return tenantModelProducer;
    }

    public void setTenantModelProducer(ITenantModelProducer tenantModelProducer) {
	this.tenantModelProducer = tenantModelProducer;
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.sitewhere.tenant.spi.microservice.ITenantManagementMicroservice#
     * getTenantBootstrapModelConsumer()
     */
    @Override
    public ITenantBootstrapModelConsumer getTenantBootstrapModelConsumer() {
	return tenantBootstrapModelConsumer;
    }

    public void setTenantBootstrapModelConsumer(ITenantBootstrapModelConsumer tenantBootstrapModelConsumer) {
	this.tenantBootstrapModelConsumer = tenantBootstrapModelConsumer;
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.sitewhere.spi.server.lifecycle.ILifecycleComponent#getLogger()
     */
    @Override
    public Log getLogger() {
	return LOGGER;
    }

    public TenantManagementAccessor getTenantManagementAccessor() {
	return tenantManagementAccessor;
    }

    public void setTenantManagementAccessor(TenantManagementAccessor tenantManagementAccessor) {
	this.tenantManagementAccessor = tenantManagementAccessor;
    }
}