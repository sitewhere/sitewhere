package com.sitewhere.microservice.configuration;

import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.recipes.cache.TreeCache;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.sitewhere.microservice.spi.configuration.IConfigurationMonitor;
import com.sitewhere.server.lifecycle.LifecycleComponent;
import com.sitewhere.spi.SiteWhereException;
import com.sitewhere.spi.server.lifecycle.ILifecycleProgressMonitor;

/**
 * Monitors configuration nodes in Zk and allows microservices to respond to
 * configuration changes.
 * 
 * @author Derek
 */
public class ConfigurationMonitor extends LifecycleComponent implements IConfigurationMonitor {

    /** Static logger instance */
    private static Logger LOGGER = LogManager.getLogger();

    /** Curator */
    private CuratorFramework curator;

    /** Instance Zk path */
    private String instanceZkPath;

    /** Tree cache for configuration data */
    private TreeCache treeCache;

    public ConfigurationMonitor(CuratorFramework curator, String instanceZkPath) {
	this.curator = curator;
	this.instanceZkPath = instanceZkPath;
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.sitewhere.server.lifecycle.LifecycleComponent#initialize(com.
     * sitewhere.spi.server.lifecycle.ILifecycleProgressMonitor)
     */
    @Override
    public void initialize(ILifecycleProgressMonitor monitor) throws SiteWhereException {
	this.treeCache = new TreeCache(getCurator(), getInstanceZkPath());
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

    public CuratorFramework getCurator() {
	return curator;
    }

    public void setCurator(CuratorFramework curator) {
	this.curator = curator;
    }

    public String getInstanceZkPath() {
	return instanceZkPath;
    }

    public void setInstanceZkPath(String instanceZkPath) {
	this.instanceZkPath = instanceZkPath;
    }

    public TreeCache getTreeCache() {
	return treeCache;
    }

    public void setTreeCache(TreeCache treeCache) {
	this.treeCache = treeCache;
    }
}