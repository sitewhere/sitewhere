package com.sitewhere.server.asset.datastore;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.sitewhere.rest.model.search.SearchCriteria;
import com.sitewhere.server.lifecycle.LifecycleProgressContext;
import com.sitewhere.server.lifecycle.LifecycleProgressMonitor;
import com.sitewhere.server.lifecycle.TenantLifecycleComponent;
import com.sitewhere.spi.SiteWhereException;
import com.sitewhere.spi.asset.IAsset;
import com.sitewhere.spi.asset.IAssetCategory;
import com.sitewhere.spi.asset.IAssetManagement;
import com.sitewhere.spi.asset.IAssetModule;
import com.sitewhere.spi.asset.IAssetModuleManager;
import com.sitewhere.spi.search.ISearchResults;
import com.sitewhere.spi.server.lifecycle.ILifecycleProgressMonitor;
import com.sitewhere.spi.server.lifecycle.LifecycleComponentType;

/**
 * Manages asset modules that are loaded from the datastore.
 * 
 * @author Derek
 */
public class DatastoreAssetModuleManager extends TenantLifecycleComponent implements IAssetModuleManager {

    /** Static logger instance */
    private static Logger LOGGER = LogManager.getLogger();

    /** Map of datastore modules by id */
    private Map<String, IAssetModule<?>> dsModulesById = new HashMap<String, IAssetModule<?>>();

    /** Asset management implementation */
    private IAssetManagement assetManagement;

    public DatastoreAssetModuleManager() {
	super(LifecycleComponentType.Other);
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.sitewhere.server.lifecycle.LifecycleComponent#initialize(com.
     * sitewhere.spi.server.lifecycle.ILifecycleProgressMonitor)
     */
    @Override
    public void initialize(ILifecycleProgressMonitor monitor) throws SiteWhereException {
	initializeDatastoreModules(
		new LifecycleProgressMonitor(new LifecycleProgressContext(1, "Initializing datastore asset modules")));
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
	getLifecycleComponents().clear();

	for (IAssetModule<?> module : dsModulesById.values()) {
	    startNestedComponent(module, monitor, true);
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
	return dsModulesById.get(assetModuleId);
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.sitewhere.spi.asset.IAssetModuleManager#listModules()
     */
    @Override
    public List<IAssetModule<?>> listModules() throws SiteWhereException {
	List<IAssetModule<?>> modules = new ArrayList<IAssetModule<?>>();
	for (IAssetModule<?> module : dsModulesById.values()) {
	    modules.add(module);
	}
	return modules;
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * com.sitewhere.spi.asset.IAssetModuleManager#refreshModules(com.sitewhere.
     * spi.server.lifecycle.ILifecycleProgressMonitor)
     */
    @Override
    public void refreshModules(ILifecycleProgressMonitor monitor) throws SiteWhereException {
	for (IAssetModule<?> module : dsModulesById.values()) {
	    module.refresh(monitor);
	}
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * com.sitewhere.spi.asset.IAssetModuleManager#onAssetCategoryAdded(com.
     * sitewhere.spi.asset.IAssetCategory,
     * com.sitewhere.spi.server.lifecycle.ILifecycleProgressMonitor)
     */
    @Override
    public void onAssetCategoryAdded(IAssetCategory category, ILifecycleProgressMonitor monitor)
	    throws SiteWhereException {
	addAssetCategoryModule(category, monitor);
	LOGGER.info("Asset module added for category '" + category.getName() + "'.");
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * com.sitewhere.spi.asset.IAssetModuleManager#onAssetCategoryUpdated(com.
     * sitewhere.spi.asset.IAssetCategory,
     * com.sitewhere.spi.server.lifecycle.ILifecycleProgressMonitor)
     */
    @Override
    public void onAssetCategoryUpdated(IAssetCategory category, ILifecycleProgressMonitor monitor)
	    throws SiteWhereException {
	IAssetModule<?> module = getModule(category.getId());
	if (module != null) {
	    module.refresh(monitor);
	} else {
	    LOGGER.warn("Update received for non-existent asset module. Category: " + category.getId());
	}
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * com.sitewhere.spi.asset.IAssetModuleManager#onAssetCategoryRemoved(com.
     * sitewhere.spi.asset.IAssetCategory,
     * com.sitewhere.spi.server.lifecycle.ILifecycleProgressMonitor)
     */
    @Override
    public void onAssetCategoryRemoved(IAssetCategory category, ILifecycleProgressMonitor monitor)
	    throws SiteWhereException {
	IAssetModule<?> module = dsModulesById.get(category.getId());
	if (module != null) {
	    module.lifecycleStop(monitor);
	    dsModulesById.remove(category.getId());
	    LOGGER.info("Asset module removed for category '" + category.getName() + "'.");
	}
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.sitewhere.spi.asset.IAssetModuleManager#getAssetById(java.lang.
     * String, java.lang.String)
     */
    @Override
    public IAsset getAssetById(String assetModuleId, String id) throws SiteWhereException {
	IAssetModule<?> match = assertAssetModule(assetModuleId);
	return match.getAsset(id);
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.sitewhere.spi.asset.IAssetModuleManager#search(java.lang.String,
     * java.lang.String)
     */
    @Override
    public List<? extends IAsset> search(String assetModuleId, String criteria) throws SiteWhereException {
	IAssetModule<?> match = assertAssetModule(assetModuleId);
	List<? extends IAsset> results = match.search(criteria);
	Collections.sort(results);
	return results;
    }

    /**
     * Initialize a datastore module.
     * 
     * @param category
     * @param module
     * @param monitor
     * @throws SiteWhereException
     */
    protected void initializeDatastoreModule(IAssetCategory category, IAssetModule<?> module,
	    ILifecycleProgressMonitor monitor) throws SiteWhereException {
	dsModulesById.put(category.getId(), module);
    }

    /**
     * Create and initialize datastore asset modules.
     * 
     * @param monitor
     * @throws SiteWhereException
     */
    protected void initializeDatastoreModules(ILifecycleProgressMonitor monitor) throws SiteWhereException {
	dsModulesById.clear();
	ISearchResults<IAssetCategory> categories = getAssetManagement().listAssetCategories(SearchCriteria.ALL);
	LOGGER.info("Loading asset modules for " + categories.getNumResults() + " asset categories.");
	for (IAssetCategory category : categories.getResults()) {
	    addAssetCategoryModule(category, monitor);
	    LOGGER.info("Added module for '" + category.getName() + " (" + category.getId() + ").");
	}
    }

    /**
     * Add an asset module for the given {@link IAssetCategory}.
     * 
     * @param category
     * @param monitor
     * @throws SiteWhereException
     */
    protected void addAssetCategoryModule(IAssetCategory category, ILifecycleProgressMonitor monitor)
	    throws SiteWhereException {
	switch (category.getAssetType()) {
	case Device:
	case Hardware: {
	    HardwareAssetModule module = new HardwareAssetModule(category);
	    initializeDatastoreModule(category, module, monitor);
	    break;
	}
	case Person: {
	    PersonAssetModule module = new PersonAssetModule(category);
	    initializeDatastoreModule(category, module, monitor);
	    break;
	}
	case Location: {
	    LocationAssetModule module = new LocationAssetModule(category);
	    initializeDatastoreModule(category, module, monitor);
	    break;
	}
	}
    }

    /**
     * Get an asset module by unique id. Throw exception if not found.
     * 
     * @param id
     * @return
     * @throws SiteWhereException
     */
    protected IAssetModule<?> assertAssetModule(String id) throws SiteWhereException {
	IAssetModule<?> match = dsModulesById.get(id);
	if (match == null) {
	    throw new SiteWhereException("Invalid asset module id: " + id);
	}
	return match;
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

    public IAssetManagement getAssetManagement() {
	return assetManagement;
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.sitewhere.spi.asset.IAssetModuleManager#setAssetManagement(com.
     * sitewhere.spi.asset.IAssetManagement)
     */
    public void setAssetManagement(IAssetManagement assetManagement) {
	this.assetManagement = assetManagement;
    }
}