package com.sitewhere.tenant.microservice;

import java.io.File;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.recipes.locks.InterProcessSemaphoreMutex;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.context.ApplicationContext;

import com.sitewhere.microservice.GlobalMicroservice;
import com.sitewhere.microservice.spi.spring.TenantManagementBeans;
import com.sitewhere.microservice.zookeeper.ZkUtils;
import com.sitewhere.server.lifecycle.CompositeLifecycleStep;
import com.sitewhere.server.lifecycle.InitializeComponentLifecycleStep;
import com.sitewhere.server.lifecycle.SimpleLifecycleStep;
import com.sitewhere.server.lifecycle.StartComponentLifecycleStep;
import com.sitewhere.server.lifecycle.StopComponentLifecycleStep;
import com.sitewhere.spi.SiteWhereException;
import com.sitewhere.spi.server.lifecycle.ICompositeLifecycleStep;
import com.sitewhere.spi.server.lifecycle.ILifecycleProgressMonitor;
import com.sitewhere.spi.server.lifecycle.ILifecycleStep;
import com.sitewhere.spi.tenant.ITenantManagement;
import com.sitewhere.tenant.grpc.TenantManagementGrpcServer;
import com.sitewhere.tenant.spi.grpc.ITenantManagementGrpcServer;
import com.sitewhere.tenant.spi.microservice.ITenantManagementMicroservice;

/**
 * Microservice that provides tenant management functionality.
 * 
 * @author Derek
 */
public class TenantManagementMicroservice extends GlobalMicroservice implements ITenantManagementMicroservice {

    /** Static logger instance */
    private static Logger LOGGER = LogManager.getLogger();

    /** Microservice name */
    private static final String NAME = "Tenant Management";

    /** Tenant management configuration file name */
    private static final String TENANT_MANAGEMENT_CONFIGURATION = "tenant-management.xml";

    /** List of configuration paths required by microservice */
    private static final String[] CONFIGURATION_PATHS = { TENANT_MANAGEMENT_CONFIGURATION };

    /** Root folder for instance templates */
    private static final String TEMPLATES_ROOT = "/templates";

    /** Relative path for storing tenant templates */
    private static final String TEMPLATES_PATH = "/templates";

    /** Relative path for template population lock */
    private static final String TEMPLATE_POPULATION_LOCK_PATH = "/locks/templates";

    /** Responds to tenant management GRPC requests */
    private ITenantManagementGrpcServer tenantManagementGrpcServer;

    /** Tenant management persistence API */
    private ITenantManagement tenantManagement;

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
     * @see
     * com.sitewhere.microservice.spi.IGlobalMicroservice#getConfigurationPaths(
     * )
     */
    @Override
    public String[] getConfigurationPaths() throws SiteWhereException {
	return CONFIGURATION_PATHS;
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.sitewhere.microservice.spi.IGlobalMicroservice#
     * initializeFromSpringContexts(org.springframework.context.
     * ApplicationContext, java.util.Map)
     */
    @Override
    public void initializeFromSpringContexts(ApplicationContext global, Map<String, ApplicationContext> contexts)
	    throws SiteWhereException {
	ApplicationContext context = contexts.get(TENANT_MANAGEMENT_CONFIGURATION);
	this.tenantManagement = (ITenantManagement) context.getBean(TenantManagementBeans.BEAN_TENANT_MANAGEMENT);
	this.tenantManagementGrpcServer = new TenantManagementGrpcServer(getTenantManagement());
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
	// Composite step for initializing microservice.
	ICompositeLifecycleStep init = new CompositeLifecycleStep("Initialize " + getName());

	// Initialize discoverable lifecycle components.
	init.addStep(initializeDiscoverableBeans(getTenantManagementApplicationContext(), monitor));

	// Initialize tenant management implementation.
	init.addStep(new InitializeComponentLifecycleStep(this, getTenantManagement(), "Tenant management persistence",
		"Unable to initialize tenant management persistence", true));

	// Initialize tenant management GRPC server.
	init.addStep(new InitializeComponentLifecycleStep(this, getTenantManagementGrpcServer(),
		"Tenant management GRPC server", "Unable to initialize tenant management GRPC server", true));

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
	ICompositeLifecycleStep start = new CompositeLifecycleStep("Start " + getName());

	// Verify or create Zk node for instance information.
	start.addStep(populateTemplatesIfNotPresent());

	// Start discoverable lifecycle components.
	start.addStep(startDiscoverableBeans(getTenantManagementApplicationContext(), monitor));

	// Start tenant mangement persistence.
	start.addStep(new StartComponentLifecycleStep(this, getTenantManagement(), "Tenant management persistence",
		"Unable to start tenant management persistence.", true));

	// Start GRPC server.
	start.addStep(new StartComponentLifecycleStep(this, getTenantManagementGrpcServer(),
		"Tenant management GRPC manager", "Unable to start tenant management GRPC manager.", true));

	// Execute initialization steps.
	start.execute(monitor);
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * com.sitewhere.microservice.spi.IGlobalMicroservice#microserviceStop(com.
     * sitewhere.spi.server.lifecycle.ILifecycleProgressMonitor)
     */
    @Override
    public void microserviceStop(ILifecycleProgressMonitor monitor) throws SiteWhereException {
	// Composite step for stopping microservice.
	ICompositeLifecycleStep stop = new CompositeLifecycleStep("Stop " + getName());

	// Stop GRPC manager.
	stop.addStep(new StopComponentLifecycleStep(this, getTenantManagementGrpcServer(),
		"Tenant management GRPC manager"));

	// Stop tenant management persistence.
	stop.addStep(new StopComponentLifecycleStep(this, getTenantManagement(), "Tenant management persistence"));

	// Stop discoverable lifecycle components.
	stop.addStep(stopDiscoverableBeans(getTenantManagementApplicationContext(), monitor));
    }

    /**
     * Run template population logic in a monitorable step.
     * 
     * @return
     */
    public ILifecycleStep populateTemplatesIfNotPresent() {
	return new SimpleLifecycleStep("Populate tenant templates.") {

	    @Override
	    public void execute(ILifecycleProgressMonitor monitor) throws SiteWhereException {
		templatePopulationLogic();
	    }
	};
    }

    /**
     * Populate the list of tenant templates. NOTE: This uses Zk locking to make
     * sure that only one instance of tenant management populates the tenant
     * templates.
     * 
     * @throws SiteWhereException
     */
    protected void templatePopulationLogic() throws SiteWhereException {
	CuratorFramework curator = getZookeeperManager().getCurator();
	InterProcessSemaphoreMutex lock = new InterProcessSemaphoreMutex(curator,
		getInstanceZkPath() + TEMPLATE_POPULATION_LOCK_PATH);
	try {
	    // Acquire the lock.
	    if (lock.acquire(5, TimeUnit.SECONDS)) {
		LOGGER.info("Acquired lock for populating tenant templates.");
		if (curator.checkExists().forPath(getInstanceZkPath() + TEMPLATES_PATH) == null) {
		    LOGGER.info("Templates not present in Zk. Adding node and importing templates.");
		    curator.create().forPath(getInstanceZkPath() + TEMPLATES_PATH);
		    LOGGER.info("Created templates node in Zk.");

		    copyTemplatesToZk();
		} else {
		    LOGGER.info("Templates already present in Zk.");
		}
	    } else {
		LOGGER.error("Failed to acquire lock for populating tenant templates.");
	    }
	} catch (Exception e) {
	    LOGGER.error("Exception while attempting to populate tenant templates.", e);
	} finally {
	    try {
		lock.release();
		LOGGER.info("Released lock for populating tenant templates.");
	    } catch (Exception e) {
		LOGGER.error("Failed to release template population lock.", e);
	    }
	}
    }

    /**
     * Copy templates from the filesystem into Zk.
     * 
     * @throws SiteWhereException
     */
    protected void copyTemplatesToZk() throws SiteWhereException {
	File templates = new File(TEMPLATES_ROOT);
	if (!templates.exists()) {
	    throw new SiteWhereException("Templates folder not found in Docker image.");
	}
	ZkUtils.copyFolderRecursivelytoZk(getZookeeperManager().getCurator(), getInstanceZkPath() + "/templates",
		templates, templates);
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
     * (non-Javadoc)
     * 
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

    protected ApplicationContext getTenantManagementApplicationContext() {
	return getGlobalContexts().get(TENANT_MANAGEMENT_CONFIGURATION);
    }
}