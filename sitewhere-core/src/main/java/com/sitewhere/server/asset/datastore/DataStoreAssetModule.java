/*
 * Copyright (c) SiteWhere, LLC. All rights reserved. http://www.sitewhere.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package com.sitewhere.server.asset.datastore;

import java.util.ArrayList;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.hazelcast.core.ILock;
import com.hazelcast.core.IMap;
import com.hazelcast.query.Predicate;
import com.sitewhere.SiteWhere;
import com.sitewhere.rest.model.command.CommandResponse;
import com.sitewhere.rest.model.search.SearchCriteria;
import com.sitewhere.server.asset.AssetMatcher;
import com.sitewhere.server.lifecycle.TenantLifecycleComponent;
import com.sitewhere.spi.SiteWhereException;
import com.sitewhere.spi.asset.AssetType;
import com.sitewhere.spi.asset.IAsset;
import com.sitewhere.spi.asset.IAssetCategory;
import com.sitewhere.spi.asset.IAssetModule;
import com.sitewhere.spi.command.CommandResult;
import com.sitewhere.spi.command.ICommandResponse;
import com.sitewhere.spi.search.ISearchResults;
import com.sitewhere.spi.server.lifecycle.ILifecycleProgressMonitor;
import com.sitewhere.spi.server.lifecycle.LifecycleComponentType;

/**
 * Implementation of {@link IAssetModule} that loads assets from the datastore.
 * 
 * @author Derek
 */
public abstract class DataStoreAssetModule<T extends IAsset> extends TenantLifecycleComponent
	implements IAssetModule<T> {

    /** Serial version UID */
    private static final long serialVersionUID = -7587874182078265400L;

    /** Static logger instance */
    private static Logger LOGGER = LogManager.getLogger();

    /** Asset category */
    private IAssetCategory category;

    /** Asset store for category */
    protected IMap<String, T> assets;

    /** Matcher used for searches */
    protected AssetMatcher matcher = new AssetMatcher();

    public DataStoreAssetModule(IAssetCategory category) {
	super(LifecycleComponentType.AssetModule);
	this.category = category;
	this.assets = SiteWhere.getServer().getHazelcastConfiguration().getHazelcastInstance()
		.getMap(getHazelcastMapName());
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
	if (assets.size() == 0) {
	    refresh(monitor);
	} else {
	    LOGGER.info("Skipping datastore refresh due to existing distributed map data.");
	}
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.sitewhere.server.lifecycle.LifecycleComponent#getComponentName()
     */
    @Override
    public String getComponentName() {
	return getClass().getSimpleName() + " [" + getCategory().getId() + "] " + getCategory().getName();
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.sitewhere.spi.asset.IAssetCategory#getId()
     */
    public String getId() {
	return getCategory().getId();
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.sitewhere.spi.asset.IAssetCategory#getName()
     */
    public String getName() {
	return getCategory().getName();
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.sitewhere.spi.asset.IAssetCategory#getAssetType()
     */
    public AssetType getAssetType() {
	return getCategory().getAssetType();
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * com.sitewhere.spi.asset.IAssetModule#refresh(com.sitewhere.spi.server.
     * lifecycle.ILifecycleProgressMonitor)
     */
    @SuppressWarnings("unchecked")
    public ICommandResponse refresh(ILifecycleProgressMonitor monitor) throws SiteWhereException {
	ILock lock = SiteWhere.getServer().getHazelcastConfiguration().getHazelcastInstance()
		.getLock(getHazelcastMapName());
	try {
	    LOGGER.debug("Locking asset module to load assets from datastore.");
	    lock.lock();
	    ISearchResults<IAsset> matches = SiteWhere.getServer().getAssetManagement(getTenant())
		    .listAssets(category.getId(), SearchCriteria.ALL);
	    assets.clear();
	    for (IAsset asset : matches.getResults()) {
		assets.put(asset.getId(), (T) asset);
	    }
	    return new CommandResponse(CommandResult.Successful, "Asset list loaded from datastore.");
	} catch (Throwable t) {
	    return new CommandResponse(CommandResult.Failed, "Asset load operation failed. " + t.getMessage());
	} finally {
	    lock.unlock();
	    LOGGER.debug("Released lock after loading assets from datastore.");
	}
    }

    /**
     * Get an asset by unique id.
     * 
     * @param id
     * @return
     */
    protected T doGetAsset(String id) {
	return assets.get(id);
    }

    /**
     * Add an asset to the module. Note: This does not put the asset in the
     * underlying datastore. This is used to reflect changes via the APIs so
     * that the data is kept up-to-date without having to reload all of the
     * assets.
     * 
     * @param id
     * @param asset
     */
    protected void doPutAsset(String id, T asset) {
	assets.put(id, asset);
    }

    /**
     * Removes an asset from the module. This does not remove the asset from the
     * underlying datastore. This is used to reflect changes via the APIs so
     * that the data is kept up-to-date without having to reload all of the
     * assets.
     * 
     * @param id
     */
    protected void doRemoveAsset(String id) {
	assets.remove(id);
    }

    /**
     * Search cached assets based on criteria. TODO: Use Hazelcast
     * {@link Predicate} to do search on grid.
     * 
     * @param criteria
     * @return
     */
    protected List<T> doSearch(String criteria) {
	List<T> results = new ArrayList<T>();
	for (T asset : assets.values()) {
	    if (matcher.isMatch(getCategory().getAssetType(), asset, criteria)) {
		results.add(asset);
	    }
	}
	return results;
    }

    /**
     * Get name of map stored in Hazelcast.
     * 
     * @return
     */
    protected String getHazelcastMapName() {
	return DataStoreAssetModule.class.getName() + ":" + getId();
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

    public IAssetCategory getCategory() {
	return category;
    }

    public void setCategory(IAssetCategory category) {
	this.category = category;
    }
}