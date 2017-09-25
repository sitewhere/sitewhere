package com.sitewhere.microservice.spi.configuration;

import java.util.Map;

import org.springframework.context.ApplicationContext;

import com.sitewhere.microservice.spi.IMicroservice;
import com.sitewhere.spi.SiteWhereException;
import com.sitewhere.spi.server.lifecycle.IDiscoverableTenantLifecycleComponent;
import com.sitewhere.spi.server.lifecycle.ILifecycleProgressMonitor;
import com.sitewhere.spi.server.lifecycle.ILifecycleStep;

/**
 * Microservice that supports dynamic monitoring of configuration.
 * 
 * @author Derek
 */
public interface IConfigurableMicroservice extends IMicroservice {

    /**
     * Get configuration monitor.
     * 
     * @return
     */
    public IConfigurationMonitor getConfigurationMonitor();

    /**
     * Get current configuration state.
     * 
     * @return
     */
    public ConfigurationState getConfigurationState();

    /**
     * Wait for configuration to be loaded.
     * 
     * @throws SiteWhereException
     */
    public void waitForConfigurationReady() throws SiteWhereException;

    /**
     * Indicates if configuration has been cached from Zk.
     * 
     * @return
     */
    public boolean isConfigurationCacheReady();

    /**
     * Get configuration data for the given path.
     * 
     * @param path
     * @return
     * @throws SiteWhereException
     */
    public byte[] getConfigurationDataFor(String path) throws SiteWhereException;

    /**
     * Get instance global context.
     * 
     * @return
     * @throws SiteWhereException
     */
    public ApplicationContext getInstanceGlobalContext() throws SiteWhereException;

    /**
     * Get global contexts indexed by path.
     * 
     * @return
     * @throws SiteWhereException
     */
    public Map<String, ApplicationContext> getGlobalContexts() throws SiteWhereException;

    /**
     * Initialize components from the given context marked as
     * {@link IDiscoverableTenantLifecycleComponent}.
     * 
     * @param context
     * @param monitor
     * @return
     * @throws SiteWhereException
     */
    public ILifecycleStep initializeDiscoverableBeans(ApplicationContext context, ILifecycleProgressMonitor monitor)
	    throws SiteWhereException;

    /**
     * Start components from the given context marked as
     * {@link IDiscoverableTenantLifecycleComponent}.
     * 
     * @param context
     * @param monitor
     * @return
     * @throws SiteWhereException
     */
    public ILifecycleStep startDiscoverableBeans(ApplicationContext context, ILifecycleProgressMonitor monitor)
	    throws SiteWhereException;

    /**
     * Stop components from the given context marked as
     * {@link IDiscoverableTenantLifecycleComponent}.
     * 
     * @param context
     * @param monitor
     * @return
     * @throws SiteWhereException
     */
    public ILifecycleStep stopDiscoverableBeans(ApplicationContext context, ILifecycleProgressMonitor monitor)
	    throws SiteWhereException;
}