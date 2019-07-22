package com.sitewhere.rdb;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.sitewhere.configuration.instance.rdb.RDBConfiguration;
import com.sitewhere.server.lifecycle.TenantEngineLifecycleComponent;
import com.sitewhere.server.lifecycle.parameters.StringComponentParameter;
import com.sitewhere.spi.SiteWhereException;
import com.sitewhere.spi.server.lifecycle.IDiscoverableTenantLifecycleComponent;
import com.sitewhere.spi.server.lifecycle.ILifecycleComponentParameter;
import com.sitewhere.spi.server.lifecycle.ILifecycleProgressMonitor;

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

    /** Hostname parameter */
    private ILifecycleComponentParameter<String> hostname;

    /** Database parameter */
    private ILifecycleComponentParameter<String> databaseName;

    /**
     *
     * @param configuration
     */
    public DbClient(RDBConfiguration configuration) {
        this.configuration = configuration;
    }

    @Override
    public boolean isRequired() {
        return true;
    }

    @Override
    public void initializeParameters() throws SiteWhereException {
        // Add hostname.
        this.hostname = StringComponentParameter.newBuilder(this, "Hostname").value(configuration.getHostname())
                .makeRequired().build();
        getParameters().add(hostname);

        // Add database name.
        this.databaseName = StringComponentParameter.newBuilder(this, "Database")
                .value(configuration.getDatabaseName()).makeRequired().build();
        getParameters().add(databaseName);
    }

    @Override
    public void initialize(ILifecycleProgressMonitor monitor) throws SiteWhereException {
        ObjectMapper mapper = new ObjectMapper();
        String jsonStr;
        try {
            jsonStr = mapper.writeValueAsString(configuration);
        } catch (JsonProcessingException e) {
            throw new SiteWhereException(e.getMessage(), e);
        }
        dbManager = new DbManager(jsonStr);
    }

    @Override
    public void start(ILifecycleProgressMonitor monitor) throws SiteWhereException {
        getLogger().info("Relation database client will connect to " + hostname.getValue() + ":"
                + configuration.getUrl() + " for database '" + databaseName.getValue() + "'");

        dbManager.start();
    }

    @Override
    public void stop(ILifecycleProgressMonitor monitor) throws SiteWhereException {
        getLogger().info("Relation database client will disconnect to " + hostname.getValue() + ":"
                + configuration.getUrl() + " for database '" + databaseName.getValue() + "'");

        dbManager.stop();
    }

    public DbManager getDbManager() throws SiteWhereException {
        if (dbManager == null) {
            throw new SiteWhereException("dbManager is null. Relational DB client was not properly initialized.");
        }
        return dbManager;
    }

    public ILifecycleComponentParameter<String> getHostname() {
        return hostname;
    }

    public void setHostname(ILifecycleComponentParameter<String> hostname) {
        this.hostname = hostname;
    }

    public ILifecycleComponentParameter<String> getDatabaseName() {
        return databaseName;
    }

    public void setDatabaseName(ILifecycleComponentParameter<String> databaseName) {
        this.databaseName = databaseName;
    }
}
