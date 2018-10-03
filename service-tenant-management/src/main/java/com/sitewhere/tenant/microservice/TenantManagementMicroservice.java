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

import org.springframework.beans.factory.NoSuchBeanDefinitionException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;

import com.sitewhere.microservice.GlobalMicroservice;
import com.sitewhere.server.lifecycle.CompositeLifecycleStep;
import com.sitewhere.spi.SiteWhereException;
import com.sitewhere.spi.microservice.MicroserviceIdentifier;
import com.sitewhere.spi.microservice.configuration.model.IConfigurationModel;
import com.sitewhere.spi.microservice.multitenant.IDatasetTemplate;
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
import com.sitewhere.tenant.spi.templates.IDatasetTemplateManager;
import com.sitewhere.tenant.spi.templates.ITenantTemplateManager;

/**
 * Microservice that provides tenant management functionality.
 * 
 * @author Derek
 */
public class TenantManagementMicroservice extends GlobalMicroservice<MicroserviceIdentifier>
	implements ITenantManagementMicroservice<MicroserviceIdentifier>, ITenantAdministration {

    /** Microservice name */
    private static final String NAME = "Tenant Management";

    /** Tenant management configuration file name */
    private static final String CONFIGURATION_PATH = MicroserviceIdentifier.TenantManagement.getPath() + ".xml";

    /** Root folder for tenant templates */
    private static final String TEMPLATES_ROOT = "/templates";

    /** Root folder for dataset templates */
    private static final String DATASETS_ROOT = "/datasets";

    /** Responds to tenant management GRPC requests */
    private ITenantManagementGrpcServer tenantManagementGrpcServer;

    /** Accessor for tenant management implementation */
    private TenantManagementAccessor tenantManagementAccessor = new TenantManagementAccessor();

    /** Tenant management implementation */
    private ITenantManagement tenantManagement;

    /** Tenant template manager */
    @Autowired
    private ITenantTemplateManager tenantTemplateManager;

    /** Dataset template manager */
    @Autowired
    private IDatasetTemplateManager datasetTemplateManager;

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
     * @see com.sitewhere.spi.microservice.IMicroservice#getIdentifier()
     */
    @Override
    public MicroserviceIdentifier getIdentifier() {
	return MicroserviceIdentifier.TenantManagement;
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
	    ITenantManagement implementation = (ITenantManagement) context
		    .getBean(TenantManagementBeans.BEAN_TENANT_MANAGEMENT);
	    return new TenantManagementKafkaTriggers(implementation, getTenantModelProducer());
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
	// Initialize components that communicate via Kafka.
	initializeKafkaComponents();

	// Initialize GRPC server.
	this.tenantManagementGrpcServer = new TenantManagementGrpcServer(this, getTenantManagementAccessor(), this);

	// Composite step for initializing microservice.
	ICompositeLifecycleStep init = new CompositeLifecycleStep("Initialize " + getName());

	// Initialize tenant template manager.
	init.addInitializeStep(this, getTenantTemplateManager(), true);

	// Initialize dataset template manager.
	init.addInitializeStep(this, getDatasetTemplateManager(), true);

	// Initialize tenant management GRPC server.
	init.addInitializeStep(this, getTenantManagementGrpcServer(), true);

	// Initialize tenant bootstrap model consumer.
	init.addInitializeStep(this, getTenantBootstrapModelConsumer(), true);

	// Initialize tenant model producer.
	init.addInitializeStep(this, getTenantModelProducer(), true);

	// Execute initialization steps.
	init.execute(monitor);
    }

    /**
     * Initialize Apache Kafka components.
     * 
     * @throws SiteWhereException
     */
    protected void initializeKafkaComponents() throws SiteWhereException {
	this.tenantModelProducer = new TenantModelProducer();
	this.tenantBootstrapModelConsumer = new TenantBootstrapModelConsumer();
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

	// Start dataset template manager.
	start.addStartStep(this, getDatasetTemplateManager(), true);

	// Start GRPC server.
	start.addStartStep(this, getTenantManagementGrpcServer(), true);

	// Start tenant bootstrap model consumer.
	start.addStartStep(this, getTenantBootstrapModelConsumer(), true);

	// Start tenant model producer.
	start.addStartStep(this, getTenantModelProducer(), true);

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

	// Stop tenant model producer.
	stop.addStopStep(this, getTenantModelProducer());

	// Stop tenant bootstrap model consumer.
	stop.addStopStep(this, getTenantBootstrapModelConsumer());

	// Stop GRPC manager.
	stop.addStopStep(this, getTenantManagementGrpcServer());

	// Stop dataset template manager.
	stop.addStopStep(this, getDatasetTemplateManager());

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
	    throw new SiteWhereException("Tenant templates folder not found in Docker image.");
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
     * @see com.sitewhere.tenant.spi.microservice.ITenantManagementMicroservice#
     * getDatasetTemplatesRoot()
     */
    @Override
    public File getDatasetTemplatesRoot() throws SiteWhereException {
	File datasets = new File(DATASETS_ROOT);
	if (!datasets.exists()) {
	    throw new SiteWhereException("Dataset templates folder not found in Docker image.");
	}
	return datasets;
    }

    /*
     * @see com.sitewhere.spi.tenant.ITenantAdministration#getDatasetTemplates()
     */
    @Override
    public List<IDatasetTemplate> getDatasetTemplates() throws SiteWhereException {
	return getDatasetTemplateManager().getDatasetTemplates();
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
     * @see com.sitewhere.tenant.spi.microservice.ITenantManagementMicroservice#
     * getDatasetTemplateManager()
     */
    @Override
    public IDatasetTemplateManager getDatasetTemplateManager() {
	return datasetTemplateManager;
    }

    public void setDatasetTemplateManager(IDatasetTemplateManager datasetTemplateManager) {
	this.datasetTemplateManager = datasetTemplateManager;
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

    public TenantManagementAccessor getTenantManagementAccessor() {
	return tenantManagementAccessor;
    }

    public void setTenantManagementAccessor(TenantManagementAccessor tenantManagementAccessor) {
	this.tenantManagementAccessor = tenantManagementAccessor;
    }
}