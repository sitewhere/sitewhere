/*
 * Copyright (c) SiteWhere, LLC. All rights reserved. http://www.sitewhere.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package com.sitewhere.rdb;

import com.sitewhere.rdb.multitenancy.DvdRentalTenantContext;
import com.sitewhere.server.lifecycle.TenantEngineLifecycleComponent;
import com.sitewhere.spi.SiteWhereException;
import com.sitewhere.spi.server.lifecycle.ILifecycleProgressMonitor;
import com.sitewhere.spi.server.lifecycle.LifecycleComponentType;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadFactory;

/**
 * Base class for components in a tenant engine that rely on RDB
 * connectivity.
 */
public abstract class RDBTenantComponent <T extends DbClient> extends TenantEngineLifecycleComponent {


    /** Threads used for indexing */
    private ExecutorService indexer;

    public RDBTenantComponent() {
    }

    public RDBTenantComponent(LifecycleComponentType type) {
        super(type);
    }

    /**
     * Get the RDB client used to access the database.
     *
     * @return
     * @throws SiteWhereException
     */
    public abstract T getRDBClient() throws SiteWhereException;


    public ExecutorService getIndexer() {
        return indexer;
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

        //TODO: NO estoy seguro de que venga aca
        String tenantId = this.getTenantEngine().getTenant().getId().toString();
        DvdRentalTenantContext.setTenantId(tenantId);
        this.indexer = Executors.newSingleThreadExecutor(new IndexerThreadFactory());

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
     * Runs indexing code in background thread.
     */
    protected class Indexer implements Runnable {
        @Override
        public void run() {
            getLogger().info("Rerifying if the database exists...");
        }
    }

    /** Used for naming indexer thread */
    private class IndexerThreadFactory implements ThreadFactory {

        public Thread newThread(Runnable r) {
            return new Thread(r, "RDB Indexer");
        }
    }
}
