/*
 * Copyright (c) SiteWhere, LLC. All rights reserved. http://www.sitewhere.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package com.sitewhere.asset.modules;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.sitewhere.asset.modules.datastore.DatastoreAssetModuleManager;
import com.sitewhere.asset.spi.modules.IAssetModule;
import com.sitewhere.asset.spi.modules.IAssetModuleManager;
import com.sitewhere.server.lifecycle.TenantLifecycleComponent;
import com.sitewhere.spi.SiteWhereException;
import com.sitewhere.spi.SiteWhereSystemException;
import com.sitewhere.spi.asset.IAsset;
import com.sitewhere.spi.asset.IAssetCategory;
import com.sitewhere.spi.asset.IAssetManagement;
import com.sitewhere.spi.error.ErrorCode;
import com.sitewhere.spi.error.ErrorLevel;
import com.sitewhere.spi.server.lifecycle.ILifecycleProgressMonitor;
import com.sitewhere.spi.server.lifecycle.LifecycleComponentType;

/**
 * Manages the list of modules
 * 
 * @author dadams
 */
public class AssetModuleManager extends TenantLifecycleComponent implements IAssetModuleManager {

    /** Static logger instance */
    private static Logger LOGGER = LogManager.getLogger();

    /** List of asset modules */
    private List<IAssetModule<?>> modules;

    /** Map of asset modules by unique id */
    private Map<String, IAssetModule<?>> modulesById = new HashMap<String, IAssetModule<?>>();

    /** Manages asset modules loaded from the datastore */
    private DatastoreAssetModuleManager dsModuleManager = new DatastoreAssetModuleManager();

    /** Asset mangement implementation */
    private IAssetManagement assetManagement;

    public AssetModuleManager() {
	super(LifecycleComponentType.AssetModuleManager);
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.sitewhere.server.lifecycle.LifecycleComponent#initialize(com.
     * sitewhere.spi.server.lifecycle.ILifecycleProgressMonitor)
     */
    @Override
    public void initialize(ILifecycleProgressMonitor monitor) throws SiteWhereException {
	initializeNestedComponent(dsModuleManager, monitor, true);
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * com.sitewhere.server.lifecycle.LifecycleComponent#start(com.sitewhere.spi
     * .server.lifecycle.ILifecycleProgressMonitor)
     */
    public void start(ILifecycleProgressMonitor monitor) throws SiteWhereException {
	getLifecycleComponents().clear();

	modulesById.clear();
	for (IAssetModule<?> module : modules) {
	    startNestedComponent(module, monitor, true);
	    modulesById.put(module.getId(), module);
	}

	// Start datastore module manager as nested component.
	startNestedComponent(dsModuleManager, monitor, true);
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

    /*
     * (non-Javadoc)
     * 
     * @see
     * com.sitewhere.server.lifecycle.LifecycleComponent#stop(com.sitewhere.spi.
     * server.lifecycle.ILifecycleProgressMonitor)
     */
    public void stop(ILifecycleProgressMonitor monitor) {
	for (IAssetModule<?> module : modulesById.values()) {
	    module.lifecycleStop(monitor);
	}

	// Stop datastore module manager.
	dsModuleManager.lifecycleStop(monitor);
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.sitewhere.spi.asset.IAssetModuleManager#getModule(java.lang.String)
     */
    @Override
    public IAssetModule<?> getModule(String assetModuleId) throws SiteWhereException {
	IAssetModule<?> module = modulesById.get(assetModuleId);
	if (module == null) {
	    module = dsModuleManager.getModule(assetModuleId);
	}
	return module;
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.sitewhere.spi.asset.IAssetModuleManager#listModules()
     */
    @Override
    public List<IAssetModule<?>> listModules() throws SiteWhereException {
	List<IAssetModule<?>> modules = new ArrayList<IAssetModule<?>>();
	for (IAssetModule<?> module : modulesById.values()) {
	    modules.add(module);
	}
	modules.addAll(dsModuleManager.listModules());
	Collections.sort(modules, new AssetModuleComparator());
	return modules;
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.sitewhere.spi.asset.IAssetModuleManager#getAssetById(java.lang.
     * String, java.lang.String)
     */
    public IAsset getAssetById(String assetModuleId, String id) throws SiteWhereException {
	IAssetModule<?> match = getModule(assetModuleId);
	if (match != null) {
	    return match.getAsset(id);
	}
	throw new SiteWhereSystemException(ErrorCode.InvalidAssetCategoryId, ErrorLevel.ERROR);
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.sitewhere.spi.asset.IAssetModuleManager#search(java.lang.String,
     * java.lang.String)
     */
    public List<? extends IAsset> search(String assetModuleId, String criteria) throws SiteWhereException {
	IAssetModule<?> match = getModule(assetModuleId);
	if (match != null) {
	    List<? extends IAsset> results = match.search(criteria);
	    Collections.sort(results);
	    return results;
	}
	throw new SiteWhereSystemException(ErrorCode.InvalidAssetCategoryId, ErrorLevel.ERROR);
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * com.sitewhere.spi.asset.IAssetModuleManager#refreshModules(com.sitewhere.
     * spi.server.lifecycle.ILifecycleProgressMonitor)
     */
    public void refreshModules(ILifecycleProgressMonitor monitor) throws SiteWhereException {
	for (IAssetModule<?> module : modulesById.values()) {
	    module.refresh(monitor);
	}
	dsModuleManager.refreshModules(monitor);
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.sitewhere.spi.asset.IAssetModuleManager#onAssetCategoryAdded(com.
     * sitewhere.spi.asset.IAssetCategory,
     * com.sitewhere.spi.server.lifecycle.ILifecycleProgressMonitor)
     */
    @Override
    public void onAssetCategoryAdded(IAssetCategory category, ILifecycleProgressMonitor monitor)
	    throws SiteWhereException {
	dsModuleManager.onAssetCategoryAdded(category, monitor);
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.sitewhere.spi.asset.IAssetModuleManager#onAssetCategoryUpdated(com.
     * sitewhere.spi.asset.IAssetCategory,
     * com.sitewhere.spi.server.lifecycle.ILifecycleProgressMonitor)
     */
    @Override
    public void onAssetCategoryUpdated(IAssetCategory category, ILifecycleProgressMonitor monitor)
	    throws SiteWhereException {
	dsModuleManager.onAssetCategoryUpdated(category, monitor);
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.sitewhere.spi.asset.IAssetModuleManager#onAssetCategoryRemoved(com.
     * sitewhere.spi.asset.IAssetCategory,
     * com.sitewhere.spi.server.lifecycle.ILifecycleProgressMonitor)
     */
    @Override
    public void onAssetCategoryRemoved(IAssetCategory category, ILifecycleProgressMonitor monitor)
	    throws SiteWhereException {
	dsModuleManager.onAssetCategoryRemoved(category, monitor);
    }

    public List<IAssetModule<?>> getModules() {
	return modules;
    }

    public void setModules(List<IAssetModule<?>> modules) {
	this.modules = modules;
    }

    /*
     * @see
     * com.sitewhere.asset.spi.modules.IAssetModuleManager#setAssetManagement(com.
     * sitewhere.spi.asset.IAssetManagement)
     */
    @Override
    public void setAssetManagement(IAssetManagement assetManagement) {
	this.assetManagement = assetManagement;
	dsModuleManager.setAssetManagement(assetManagement);
    }

    public IAssetManagement getAssetManagement() {
	return assetManagement;
    }

    /**
     * Used for sorting asset modules.
     * 
     * @author Derek
     */
    private class AssetModuleComparator implements Comparator<IAssetModule<?>> {

	@Override
	public int compare(IAssetModule<?> o1, IAssetModule<?> o2) {
	    return o1.getName().compareTo(o2.getName());
	}
    }
}