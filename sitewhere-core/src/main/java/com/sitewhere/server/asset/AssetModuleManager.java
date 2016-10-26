/*
 * Copyright (c) SiteWhere, LLC. All rights reserved. http://www.sitewhere.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package com.sitewhere.server.asset;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.sitewhere.SiteWhere;
import com.sitewhere.rest.model.command.CommandResponse;
import com.sitewhere.rest.model.search.SearchCriteria;
import com.sitewhere.server.asset.datastore.HardwareAssetModule;
import com.sitewhere.server.asset.datastore.LocationAssetModule;
import com.sitewhere.server.asset.datastore.PersonAssetModule;
import com.sitewhere.server.lifecycle.TenantLifecycleComponent;
import com.sitewhere.spi.SiteWhereException;
import com.sitewhere.spi.asset.IAsset;
import com.sitewhere.spi.asset.IAssetCategory;
import com.sitewhere.spi.asset.IAssetModule;
import com.sitewhere.spi.asset.IAssetModuleManager;
import com.sitewhere.spi.command.CommandResult;
import com.sitewhere.spi.command.ICommandResponse;
import com.sitewhere.spi.search.ISearchResults;
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

    /** Map of datastore modules by id */
    private Map<String, IAssetModule<?>> dsModulesById = new HashMap<String, IAssetModule<?>>();

    public AssetModuleManager() {
	super(LifecycleComponentType.AssetModuleManager);
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
	refreshDatastoreModules(monitor);
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * com.sitewhere.spi.asset.IAssetModuleManager#refreshDatastoreModules(com.
     * sitewhere.spi.server.lifecycle.ILifecycleProgressMonitor)
     */
    public List<ICommandResponse> refreshDatastoreModules(ILifecycleProgressMonitor monitor) throws SiteWhereException {
	List<ICommandResponse> responses = new ArrayList<ICommandResponse>();
	dsModulesById.clear();
	ISearchResults<IAssetCategory> categories = SiteWhere.getServer().getAssetManagement(getTenant())
		.listAssetCategories(new SearchCriteria(1, 0));
	for (IAssetCategory category : categories.getResults()) {
	    switch (category.getAssetType()) {
	    case Device:
	    case Hardware: {
		HardwareAssetModule module = new HardwareAssetModule(category);
		startDatastoreModule(category, module, responses, monitor);
		break;
	    }
	    case Person: {
		PersonAssetModule module = new PersonAssetModule(category);
		startDatastoreModule(category, module, responses, monitor);
		break;
	    }
	    case Location: {
		LocationAssetModule module = new LocationAssetModule(category);
		startDatastoreModule(category, module, responses, monitor);
		break;
	    }
	    }
	}
	return responses;
    }

    /**
     * Start a datastore asset module.
     * 
     * @param category
     * @param module
     * @param responses
     * @param monitor
     * @throws SiteWhereException
     */
    protected void startDatastoreModule(IAssetCategory category, IAssetModule<?> module,
	    List<ICommandResponse> responses, ILifecycleProgressMonitor monitor) throws SiteWhereException {
	startNestedComponent(module, monitor, true);
	dsModulesById.put(category.getId(), module);
	responses
		.add(new CommandResponse(CommandResult.Successful, "Asset module " + category.getId() + " refreshed."));
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
	for (IAssetModule<?> module : dsModulesById.values()) {
	    module.lifecycleStop(monitor);
	}
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * com.sitewhere.spi.asset.IAssetModuleManager#getModule(java.lang.String)
     */
    @Override
    public IAssetModule<?> getModule(String assetModuleId) throws SiteWhereException {
	return assertAssetModule(assetModuleId);
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.sitewhere.spi.asset.IAssetModuleManager#listModules()
     */
    @Override
    public List<IAssetModule<?>> listModules() {
	List<IAssetModule<?>> modules = new ArrayList<IAssetModule<?>>();
	for (IAssetModule<?> module : modulesById.values()) {
	    modules.add(module);
	}
	for (IAssetModule<?> module : dsModulesById.values()) {
	    modules.add(module);
	}
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
	IAssetModule<?> match = assertAssetModule(assetModuleId);
	return match.getAssetById(id);
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.sitewhere.spi.asset.IAssetModuleManager#search(java.lang.String,
     * java.lang.String)
     */
    public List<? extends IAsset> search(String assetModuleId, String criteria) throws SiteWhereException {
	IAssetModule<?> match = assertAssetModule(assetModuleId);
	List<? extends IAsset> results = match.search(criteria);
	Collections.sort(results);
	return results;
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * com.sitewhere.spi.asset.IAssetModuleManager#refreshModules(com.sitewhere.
     * spi.server.lifecycle.ILifecycleProgressMonitor)
     */
    public List<ICommandResponse> refreshModules(ILifecycleProgressMonitor monitor) throws SiteWhereException {
	List<ICommandResponse> responses = new ArrayList<ICommandResponse>();
	for (IAssetModule<?> module : modulesById.values()) {
	    responses.add(module.refresh());
	}
	List<ICommandResponse> dsResponses = refreshDatastoreModules(monitor);
	responses.addAll(dsResponses);
	return responses;
    }

    /**
     * Get asset module by id or throw exception if not found.
     * 
     * @param id
     * @return
     * @throws SiteWhereException
     */
    protected IAssetModule<?> assertAssetModule(String id) throws SiteWhereException {
	IAssetModule<?> match = modulesById.get(id);
	if (match == null) {
	    // NOTE: External modules with same id can hide datastore modules!
	    match = dsModulesById.get(id);
	    if (match == null) {
		throw new SiteWhereException("Invalid asset module id: " + id);
	    }
	}
	return match;
    }

    public List<IAssetModule<?>> getModules() {
	return modules;
    }

    public void setModules(List<IAssetModule<?>> modules) {
	this.modules = modules;
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