/*
 * Copyright (c) SiteWhere, LLC. All rights reserved. http://www.sitewhere.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package com.sitewhere.rdb;

import com.sitewhere.configuration.instance.rdb.RDBConfiguration;
import com.sitewhere.rdb.multitenancy.MultiTenantContext;
import com.sitewhere.rdb.multitenancy.MultiTenantProperties;
import com.sitewhere.server.lifecycle.TenantEngineLifecycleComponent;
import com.sitewhere.server.lifecycle.parameters.StringComponentParameter;
import com.sitewhere.spi.SiteWhereException;
import com.sitewhere.spi.server.lifecycle.IDiscoverableTenantLifecycleComponent;
import com.sitewhere.spi.server.lifecycle.ILifecycleComponentParameter;
import com.sitewhere.spi.server.lifecycle.ILifecycleProgressMonitor;
import com.sitewhere.spi.server.lifecycle.LifecycleComponentType;

/**
 * Client used for connecting to and interacting with an Relational database server.
 *
 * Simeon Chen
 */
public abstract class DbClient extends TenantEngineLifecycleComponent implements IDiscoverableTenantLifecycleComponent {

    /** Relational database client */
    private DbManager dbManager;

    /** Relational database configuration */
    private RDBConfiguration configuration;

    /** Url parameter */
    private ILifecycleComponentParameter<String> url;

    /**
     *
     * @param configuration
     */
    public DbClient(RDBConfiguration configuration) {
        super(LifecycleComponentType.DataStore);
        this.configuration = configuration;
    }

    @Override
    public boolean isRequired() {
        return true;
    }

    @Override
    public void initializeParameters() throws SiteWhereException {
        // Add database url
        this.url = StringComponentParameter.newBuilder(this,"Url")
                .value(configuration.getUrl()).makeRequired().build();
        getParameters().add(url);

    }

    @Override
    public void initialize(ILifecycleProgressMonitor monitor) throws SiteWhereException {
        dbManager = new DbManager();
    }

    @Override
    public void start(ILifecycleProgressMonitor monitor) throws SiteWhereException {
        getLogger().info("Relation database client will connect to " + configuration.getUrl());
        String tenantId = this.getTenantEngine().getTenant().getId().toString();
        MultiTenantProperties.ADD_NEW_DATASOURCE(configuration, tenantId);
        MultiTenantContext.setTenantId(tenantId);
        dbManager.start();
    }

    @Override
    public void stop(ILifecycleProgressMonitor monitor) throws SiteWhereException {
        getLogger().info("Relation database client will connect to " + configuration.getUrl());
        dbManager.stop();
    }

    public DbManager getDbManager() throws SiteWhereException {
        if (dbManager == null) {
            throw new SiteWhereException("dbManager is null. Relational DB client was not properly initialized.");
        }
        return dbManager;
    }

    public ILifecycleComponentParameter<String> getUrl() {
        return url;
    }

    public void setUrl(ILifecycleComponentParameter<String> url) {
        this.url = url;
    }
}
