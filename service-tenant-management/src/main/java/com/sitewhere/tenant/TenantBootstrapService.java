package com.sitewhere.tenant;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.sitewhere.groovy.asset.GroovyAssetModelInitializer;
import com.sitewhere.groovy.device.GroovyDeviceModelInitializer;
import com.sitewhere.groovy.scheduling.GroovyScheduleModelInitializer;
import com.sitewhere.server.lifecycle.TenantLifecycleComponent;
import com.sitewhere.spi.SiteWhereException;
import com.sitewhere.spi.error.ResourceExistsException;
import com.sitewhere.spi.server.lifecycle.ILifecycleProgressMonitor;
import com.sitewhere.spi.server.lifecycle.LifecycleComponentType;
import com.sitewhere.spi.server.tenant.ISiteWhereTenantEngine;
import com.sitewhere.tenant.spi.ITenantBootstrapService;
import com.sitewhere.tenant.templates.TenantTemplate;

/**
 * Service that bootstraps a tenant with sample data from the tenant template it
 * is associated with.
 * 
 * @author Derek
 */
public class TenantBootstrapService extends TenantLifecycleComponent implements ITenantBootstrapService {

    /** Static logger instance */
    private static Logger LOGGER = LogManager.getLogger();

    /** Tenant engine being bootstrapped */
    private ISiteWhereTenantEngine tenantEngine;

    public TenantBootstrapService(ISiteWhereTenantEngine tenantEngine) {
	super(LifecycleComponentType.Other);
	this.tenantEngine = tenantEngine;
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.sitewhere.server.lifecycle.LifecycleComponent#initialize(com.
     * sitewhere.spi.server.lifecycle.ILifecycleProgressMonitor)
     */
    @Override
    public void initialize(ILifecycleProgressMonitor monitor) throws SiteWhereException {
	if (!getTenantEngine().getTenantConfigurationResolver().hasValidConfiguration()) {
	    try {
		LOGGER.info("Copying tenant template resources.");
		getTenantEngine().getTenantConfigurationResolver().copyTenantTemplateResources();
	    } catch (Throwable t) {
		throw new SiteWhereException("Unable copy tenant template resources.", t);
	    }
	}
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * com.sitewhere.server.lifecycle.LifecycleComponent#start(com.sitewhere.spi
     * .server.lifecycle.ILifecycleProgressMonitor)
     */
    @Override
    public void start(ILifecycleProgressMonitor monitor) throws SiteWhereException {
	// IResource templateResource =
	// getTenantEngine().getTenantConfigurationResolver()
	// .getResourceForPath(IDefaultResourcePaths.TEMPLATE_JSON_FILE_NAME);
	// if (templateResource == null) {
	// LOGGER.info("Tenant already bootstrapped with tenant template
	// data.");
	// return;
	// }
	// // Unmarshal template and bootstrap from it.
	// TenantTemplate template =
	// MarshalUtils.unmarshalJson(templateResource.getContent(),
	// TenantTemplate.class);
	// try {
	// LOGGER.info("Bootstrapping tenant with template data.");
	// bootstrapFromTemplate(template);
	// } catch (Throwable t) {
	// throw new SiteWhereException("Unable to bootstrap tenant from tenant
	// template configuration.", t);
	// }
	//
	// // Delete template file to prevent bootstrapping on future startups.
	// SiteWhere.getServer().getRuntimeResourceManager().deleteTenantResource(getTenant().getId(),
	// IDefaultResourcePaths.TEMPLATE_JSON_FILE_NAME);
    }

    /**
     * Bootstrap tenant data based on information contained in the tenant
     * template.
     * 
     * @param template
     * @throws SiteWhereException
     */
    protected void bootstrapFromTemplate(TenantTemplate template) throws SiteWhereException {
	if (template.getInitializers() != null) {

	    // Execute asset management model initializers if configured.
	    if (template.getInitializers().getAssetManagement() != null) {
		for (String script : template.getInitializers().getAssetManagement()) {
		    GroovyAssetModelInitializer amInit = new GroovyAssetModelInitializer(
			    getTenantEngine().getGroovyConfiguration(), script);
		    try {
			amInit.initialize(getTenantEngine().getAssetModuleManager(),
				getTenantEngine().getAssetManagement());
		    } catch (ResourceExistsException e) {
			LOGGER.warn("Asset management initializer data overlaps existing data. "
				+ "Skipping further asset management initialization.");
		    }
		}
	    }

	    // Execute device management model initializers if configured.
	    if (template.getInitializers().getDeviceManagement() != null) {
		for (String script : template.getInitializers().getDeviceManagement()) {
		    GroovyDeviceModelInitializer dmInit = new GroovyDeviceModelInitializer(
			    getTenantEngine().getGroovyConfiguration(), script);
		    try {
			dmInit.initialize(getTenantEngine().getDeviceManagement(),
				getTenantEngine().getDeviceEventManagement(), getTenantEngine().getAssetManagement(),
				getTenantEngine().getAssetModuleManager());
		    } catch (ResourceExistsException e) {
			LOGGER.warn("Device management initializer data overlaps existing data. "
				+ "Skipping further device management initialization.");
		    }
		}
	    }

	    // Execute schedule management model initializers if configured.
	    if (template.getInitializers().getScheduleManagement() != null) {
		for (String script : template.getInitializers().getScheduleManagement()) {
		    GroovyScheduleModelInitializer smInit = new GroovyScheduleModelInitializer(
			    getTenantEngine().getGroovyConfiguration(), script);
		    try {
			smInit.initialize(getTenantEngine().getScheduleManagement());
		    } catch (ResourceExistsException e) {
			LOGGER.warn("Schedule management initializer data overlaps existing data. "
				+ "Skipping further asset management initialization.");
		    }
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

    public ISiteWhereTenantEngine getTenantEngine() {
	return tenantEngine;
    }

    public void setTenantEngine(ISiteWhereTenantEngine tenantEngine) {
	this.tenantEngine = tenantEngine;
    }
}