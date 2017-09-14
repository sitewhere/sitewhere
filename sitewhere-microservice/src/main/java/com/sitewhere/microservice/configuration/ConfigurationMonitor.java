package com.sitewhere.microservice.configuration;

import java.util.ArrayList;
import java.util.List;

import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.recipes.cache.ChildData;
import org.apache.curator.framework.recipes.cache.TreeCache;
import org.apache.curator.framework.recipes.cache.TreeCacheEvent;
import org.apache.curator.framework.recipes.cache.TreeCacheListener;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.sitewhere.common.MarshalUtils;
import com.sitewhere.microservice.spi.configuration.IConfigurationListener;
import com.sitewhere.microservice.spi.configuration.IConfigurationMonitor;
import com.sitewhere.microservice.spi.configuration.IZookeeperManager;
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
    private IZookeeperManager zkManager;

    /** Instance configuration Zk path */
    private String configurationPath;

    /** Tree cache for configuration data */
    private TreeCache treeCache;

    /** List of configuration listeners */
    private List<IConfigurationListener> listeners = new ArrayList<IConfigurationListener>();

    public ConfigurationMonitor(IZookeeperManager zkManager, String configurationPath) {
	this.zkManager = zkManager;
	this.configurationPath = configurationPath;
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.sitewhere.server.lifecycle.LifecycleComponent#initialize(com.
     * sitewhere.spi.server.lifecycle.ILifecycleProgressMonitor)
     */
    @Override
    public void initialize(ILifecycleProgressMonitor monitor) throws SiteWhereException {
	this.treeCache = new TreeCache(getZkManager().getCurator(), getConfigurationPath());
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
	try {
	    getTreeCache().getListenable().addListener(new TreeCacheListener() {

		/*
		 * (non-Javadoc)
		 * 
		 * @see
		 * org.apache.curator.framework.recipes.cache.TreeCacheListener#
		 * childEvent(org.apache.curator.framework.CuratorFramework,
		 * org.apache.curator.framework.recipes.cache.TreeCacheEvent)
		 */
		@Override
		public void childEvent(CuratorFramework client, TreeCacheEvent event) throws Exception {
		    switch (event.getType()) {
		    case INITIALIZED: {
			onCacheInitialized();
			break;
		    }
		    case NODE_ADDED: {
			onNodeAdded(event);
			break;
		    }
		    case NODE_UPDATED: {
			onNodeUpdated(event);
			break;
		    }
		    case NODE_REMOVED: {
			onNodeDeleted(event);
			break;
		    }
		    default: {
			String json = MarshalUtils.marshalJsonAsPrettyString(event);
			LOGGER.info("Tree cache event.\n\n" + json);
		    }
		    }
		}
	    });
	    getTreeCache().start();
	    LOGGER.info("Configuration manager listening for configuration updates.");
	} catch (Exception e) {
	    throw new SiteWhereException("Unable to start tree cache for configuration monitor.", e);
	}
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
	getTreeCache().close();
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.sitewhere.microservice.spi.configuration.IConfigurationMonitor#
     * getConfigurationDataFor(java.lang.String)
     */
    @Override
    public byte[] getConfigurationDataFor(String path) throws SiteWhereException {
	ChildData data = getTreeCache().getCurrentData(path);
	return (data != null) ? data.getData() : null;
    }

    /**
     * Called after cache has been initialized.
     */
    protected void onCacheInitialized() {
	for (IConfigurationListener listener : getListeners()) {
	    listener.onConfigurationCacheInitialized();
	}
    }

    /**
     * Called when node data is added.
     * 
     * @param event
     */
    protected void onNodeAdded(TreeCacheEvent event) {
	for (IConfigurationListener listener : getListeners()) {
	    listener.onConfigurationAdded(event.getData().getPath(), event.getData().getData());
	}
    }

    /**
     * Called when node data is updated.
     * 
     * @param event
     */
    protected void onNodeUpdated(TreeCacheEvent event) {
	for (IConfigurationListener listener : getListeners()) {
	    listener.onConfigurationUpdated(event.getData().getPath(), event.getData().getData());
	}
    }

    /**
     * Called when node data is deleted.
     * 
     * @param event
     */
    protected void onNodeDeleted(TreeCacheEvent event) {
	for (IConfigurationListener listener : getListeners()) {
	    listener.onConfigurationDeleted(event.getData().getPath());
	}
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.sitewhere.microservice.spi.configuration.IConfigurationMonitor#
     * getListeners()
     */
    @Override
    public List<IConfigurationListener> getListeners() {
	return listeners;
    }

    public void setListeners(List<IConfigurationListener> listeners) {
	this.listeners = listeners;
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

    public IZookeeperManager getZkManager() {
	return zkManager;
    }

    public void setZkManager(IZookeeperManager zkManager) {
	this.zkManager = zkManager;
    }

    public String getConfigurationPath() {
	return configurationPath;
    }

    public void setConfigurationPath(String configurationPath) {
	this.configurationPath = configurationPath;
    }

    public TreeCache getTreeCache() {
	return treeCache;
    }

    public void setTreeCache(TreeCache treeCache) {
	this.treeCache = treeCache;
    }
}