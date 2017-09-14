/*
 * Copyright (c) SiteWhere, LLC. All rights reserved. http://www.sitewhere.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package com.sitewhere.instance.microservice;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.zookeeper.data.Stat;

import com.sitewhere.instance.spi.microservice.IInstanceManagement;
import com.sitewhere.instance.spi.templates.IInstanceTemplate;
import com.sitewhere.instance.spi.templates.IInstanceTemplateManager;
import com.sitewhere.instance.templates.InstanceTemplateManager;
import com.sitewhere.microservice.Microservice;
import com.sitewhere.microservice.MicroserviceEnvironment;
import com.sitewhere.server.lifecycle.CompositeLifecycleStep;
import com.sitewhere.server.lifecycle.SimpleLifecycleStep;
import com.sitewhere.server.lifecycle.StartComponentLifecycleStep;
import com.sitewhere.server.lifecycle.StopComponentLifecycleStep;
import com.sitewhere.spi.SiteWhereException;
import com.sitewhere.spi.server.lifecycle.ICompositeLifecycleStep;
import com.sitewhere.spi.server.lifecycle.ILifecycleProgressMonitor;
import com.sitewhere.spi.server.lifecycle.ILifecycleStep;

/**
 * Microservice that provides instance management functionality.
 * 
 * @author Derek
 */
public class InstanceManagement extends Microservice implements IInstanceManagement {

    /** Static logger instance */
    private static Logger LOGGER = LogManager.getLogger();

    /** Microservice name */
    private static final String NAME = "Instance Management";

    /** Default instance template id used if not set in environment */
    private static final String DEFAULT_INSTANCE_TEMPLATE_ID = "mongodb-default";

    /** Instance template manager */
    private IInstanceTemplateManager instanceTemplateManager = new InstanceTemplateManager();

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
     * com.sitewhere.server.lifecycle.LifecycleComponent#start(com.sitewhere.spi
     * .server.lifecycle.ILifecycleProgressMonitor)
     */
    @Override
    public void start(ILifecycleProgressMonitor monitor) throws SiteWhereException {
	// Create step that will start components.
	ICompositeLifecycleStep start = new CompositeLifecycleStep("Start " + getName());

	// Verify or create Zk node for instance information.
	start.addStep(verifyOrCreateInstanceNode());

	// Start instance template manager.
	start.addStep(new StartComponentLifecycleStep(this, getInstanceTemplateManager(), "Instance Template Manager",
		"Unable to start instance template manager", true));

	// Verify Zk node for instance configuration or bootstrap instance.
	start.addStep(verifyOrBootstrapConfiguration());

	// Execute initialization steps.
	start.execute(monitor);
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * com.sitewhere.server.lifecycle.LifecycleComponent#stop(com.sitewhere.spi.
     * server.lifecycle.ILifecycleProgressMonitor)
     */
    @Override
    public void stop(ILifecycleProgressMonitor monitor) throws SiteWhereException {
	// Create step that will stop components.
	ICompositeLifecycleStep stop = new CompositeLifecycleStep("Stop " + getName());

	// Stop instance template manager.
	stop.addStep(new StopComponentLifecycleStep(this, getInstanceTemplateManager(), "Instance Template Manager"));

	// Execute shutdown steps.
	stop.execute(monitor);
    }

    /**
     * Verify that a Zk node exists to hold instance information. Create the
     * folder if it does not exist. Other microservices block while waiting on
     * this node to be created.
     * 
     * @return
     */
    public ILifecycleStep verifyOrCreateInstanceNode() {
	return new SimpleLifecycleStep("Verify instance bootstrapped") {

	    @Override
	    public void execute(ILifecycleProgressMonitor monitor) throws SiteWhereException {
		try {
		    Stat existing = getZookeeperManager().getCurator().checkExists().forPath(getInstanceZkPath());
		    if (existing == null) {
			LOGGER.info("Zk node for instance not found. Creating...");
			getZookeeperManager().getCurator().create().forPath(getInstanceZkPath());
			LOGGER.info("Created instance Zk node.");
		    } else {
			LOGGER.info("Found Zk node for instance.");
		    }
		} catch (Exception e) {
		    throw new SiteWhereException(e);
		}
	    }
	};
    }

    /**
     * Verify that a Zk node exists to hold instance configuration information.
     * Create the folder and bootstrap from the instance template if it does not
     * exist. Other microservices block while waiting on this node to be
     * created.
     * 
     * @return
     */
    public ILifecycleStep verifyOrBootstrapConfiguration() {
	return new SimpleLifecycleStep("Verify instance configured") {

	    @Override
	    public void execute(ILifecycleProgressMonitor monitor) throws SiteWhereException {
		try {
		    Stat existing = getZookeeperManager().getCurator().checkExists()
			    .forPath(getInstanceConfigurationPath());
		    if (existing == null) {
			LOGGER.info("Zk node for instance configuration not found. Bootstrapping...");
			bootstrapInstanceConfiguration();
			LOGGER.info("Bootstrapped instance configuration from template.");
		    } else {
			LOGGER.info("Found Zk node for instance configuration. Skipping instance bootstrap.");
		    }
		} catch (SiteWhereException e) {
		    throw e;
		} catch (Exception e) {
		    throw new SiteWhereException(e);
		}
	    }
	};
    }

    /**
     * Bootstrap instance configuration data from chosen instance template.
     * 
     * @throws SiteWhereException
     */
    protected void bootstrapInstanceConfiguration() throws SiteWhereException {
	try {
	    getZookeeperManager().getCurator().create().forPath(getInstanceConfigurationPath());
	    getInstanceTemplateManager().copyTemplateConfigurationToZk(getDefaultInstanceTemplateOrOverride(),
		    getZookeeperManager().getCurator(), getInstanceConfigurationPath());
	} catch (Exception e) {
	    throw new SiteWhereException(e);
	}
    }

    /**
     * Check environment variable for SiteWhere instance template id or use
     * default template if not specified.
     */
    protected static String getDefaultInstanceTemplateOrOverride() {
	String envInstanceTemplate = System.getenv().get(MicroserviceEnvironment.ENV_INSTANCE_TEMPLATE_ID);
	if (envInstanceTemplate != null) {
	    LOGGER.info("Instance template found in " + MicroserviceEnvironment.ENV_INSTANCE_TEMPLATE_ID + ": "
		    + envInstanceTemplate);
	    return envInstanceTemplate;
	}
	return DEFAULT_INSTANCE_TEMPLATE_ID;
    }

    /**
     * Get instance template chosen via enviroment variable or default.
     * 
     * @return
     * @throws SiteWhereException
     */
    protected IInstanceTemplate getChosenInstanceTemplate() throws SiteWhereException {
	String templateId = getDefaultInstanceTemplateOrOverride();
	IInstanceTemplate template = getInstanceTemplateManager().getInstanceTemplates().get(templateId);
	if (template == null) {
	    throw new SiteWhereException("Unable to locate instance template: " + templateId);
	}
	return template;
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.sitewhere.instance.spi.microservice.IInstanceManagement#
     * getInstanceTemplateManager()
     */
    @Override
    public IInstanceTemplateManager getInstanceTemplateManager() {
	return instanceTemplateManager;
    }

    public void setInstanceTemplateManager(IInstanceTemplateManager instanceTemplateManager) {
	this.instanceTemplateManager = instanceTemplateManager;
    }
}