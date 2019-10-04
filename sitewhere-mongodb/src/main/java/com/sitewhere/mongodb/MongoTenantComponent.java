/*
 * Copyright (c) SiteWhere, LLC. All rights reserved. http://www.sitewhere.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package com.sitewhere.mongodb;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadFactory;

import com.sitewhere.server.lifecycle.TenantEngineLifecycleComponent;
import com.sitewhere.spi.SiteWhereException;
import com.sitewhere.spi.server.lifecycle.ILifecycleProgressMonitor;
import com.sitewhere.spi.server.lifecycle.LifecycleComponentType;

/**
 * Base class for components in a tenant engine that rely on MongoDB
 * connectivity.
 */
public abstract class MongoTenantComponent<T extends MongoDbClient> extends TenantEngineLifecycleComponent {

    /** Number of seconds to wait between liveness checks */
    private static final int MONGODB_LIVENESS_CHECK_IN_SECS = 5;

    /** Number of retries before writing warnings to log */
    private static final int WARN_AFTER_RETRIES = 20;

    /** Threads used for indexing */
    private ExecutorService indexer;

    public MongoTenantComponent() {
    }

    public MongoTenantComponent(LifecycleComponentType type) {
	super(type);
    }

    /*
     * @see
     * com.sitewhere.server.lifecycle.LifecycleComponent#provision(com.sitewhere.spi
     * .server.lifecycle.ILifecycleProgressMonitor)
     */
    @Override
    public void provision(ILifecycleProgressMonitor monitor) throws SiteWhereException {
	if (getIndexer() != null) {
	    getIndexer().shutdownNow();
	}
	this.indexer = Executors.newSingleThreadExecutor(new IndexerThreadFactory());

	// Don't allow the component to start until MongoDB is available.
	getLogger().info("Waiting for MongoDB to become available...");
	waitForMongoAvailable();

	// Run index validate/create in background.
	getIndexer().submit(new Indexer());
    }

    /*
     * @see
     * com.sitewhere.server.lifecycle.LifecycleComponent#stop(com.sitewhere.spi.
     * server.lifecycle.ILifecycleProgressMonitor)
     */
    @Override
    public void stop(ILifecycleProgressMonitor monitor) throws SiteWhereException {
	if (getIndexer() != null) {
	    getIndexer().shutdownNow();
	}
    }

    /**
     * Waits for a connection to MongoDB to become available before continuing.
     * 
     * @throws SiteWhereException
     */
    protected void waitForMongoAvailable() throws SiteWhereException {
	int retries = 0;
	while (true) {
	    try {
		getMongoClient().getMongoClient().listDatabases();
		getLogger().info("MongoDB detected as available.");
		return;
	    } catch (Throwable t) {
		getLogger().info("MongoDB not available yet.", t);
	    }

	    try {
		Thread.sleep(MONGODB_LIVENESS_CHECK_IN_SECS * 1000);
		retries++;
		if (retries > WARN_AFTER_RETRIES) {
		    getLogger().warn(String.format("MongoDB not available after %d retries.", retries));
		}
	    } catch (InterruptedException e) {
		getLogger().warn("Interrupted while waiting for MongoDB to become available.");
		throw new SiteWhereException(e);
	    }
	}
    }

    /**
     * Ensure that required collection indexes exist.
     * 
     * @throws SiteWhereException
     */
    public abstract void ensureIndexes() throws SiteWhereException;

    /**
     * Get the MongoDB client used to access the database.
     * 
     * @return
     * @throws SiteWhereException
     */
    public abstract T getMongoClient() throws SiteWhereException;

    protected ExecutorService getIndexer() {
	return indexer;
    }

    /**
     * Runs indexing code in background thread.
     */
    protected class Indexer implements Runnable {

	@Override
	public void run() {
	    // Ensure that collection indexes exist.
	    getLogger().info("Verifying indexes for MongoDB collections...");
	    try {
		ensureIndexes();
		getLogger().info("Index verification complete.");
	    } catch (SiteWhereException e) {
		getLogger().error("Unable to create/update MongoDB indexes.", e);
	    }
	}
    }

    /** Used for naming indexer thread */
    private class IndexerThreadFactory implements ThreadFactory {

	public Thread newThread(Runnable r) {
	    return new Thread(r, "MongoDB Indexer");
	}
    }
}