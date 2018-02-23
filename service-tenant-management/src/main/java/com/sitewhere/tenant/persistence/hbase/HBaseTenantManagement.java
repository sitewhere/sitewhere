/*
 * Copyright (c) SiteWhere, LLC. All rights reserved. http://www.sitewhere.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package com.sitewhere.tenant.persistence.hbase;

import java.util.UUID;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.hadoop.hbase.regionserver.BloomType;

import com.sitewhere.hbase.HBaseContext;
import com.sitewhere.hbase.ISiteWhereHBase;
import com.sitewhere.hbase.ISiteWhereHBaseClient;
import com.sitewhere.hbase.common.SiteWhereTables;
import com.sitewhere.hbase.encoder.IPayloadMarshaler;
import com.sitewhere.hbase.encoder.JsonPayloadMarshaler;
import com.sitewhere.server.lifecycle.LifecycleComponent;
import com.sitewhere.spi.SiteWhereException;
import com.sitewhere.spi.search.ISearchResults;
import com.sitewhere.spi.search.user.ITenantSearchCriteria;
import com.sitewhere.spi.server.lifecycle.ILifecycleProgressMonitor;
import com.sitewhere.spi.server.lifecycle.LifecycleComponentType;
import com.sitewhere.spi.tenant.ITenant;
import com.sitewhere.spi.tenant.ITenantManagement;
import com.sitewhere.spi.tenant.request.ITenantCreateRequest;

/**
 * HBase implementation of SiteWhere tenant management.
 * 
 * @author Derek
 */
public class HBaseTenantManagement extends LifecycleComponent implements ITenantManagement {

    /** Static logger instance */
    private static Log LOGGER = LogFactory.getLog(HBaseTenantManagement.class);

    /** Used to communicate with HBase */
    private ISiteWhereHBaseClient client;

    /** Injected payload encoder */
    private IPayloadMarshaler payloadMarshaler = new JsonPayloadMarshaler();

    /** Supplies context to implementation methods */
    private HBaseContext context;

    /** User id manager */
    // private UserIdManager userIdManager;

    public HBaseTenantManagement() {
	super(LifecycleComponentType.DataStore);
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
	ensureTablesExist();

	// Create context from configured options.
	this.context = new HBaseContext();
	context.setClient(getClient());
	context.setPayloadMarshaler(getPayloadMarshaler());

	// Create device id manager instance.
	// userIdManager = new UserIdManager();
	// userIdManager.load(context);
	// context.setUserIdManager(userIdManager);
	throw new RuntimeException("Tenant management should be separated from user management.");
    }

    /**
     * Ensure that the tables this implementation depends on are there.
     * 
     * @throws SiteWhereException
     */
    protected void ensureTablesExist() throws SiteWhereException {
	SiteWhereTables.assureTable(client, ISiteWhereHBase.USERS_TABLE_NAME, BloomType.ROW);
	SiteWhereTables.assureTable(client, ISiteWhereHBase.UID_TABLE_NAME, BloomType.ROW);
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.sitewhere.spi.server.lifecycle.ILifecycleComponent#getLogger()
     */
    @Override
    public Log getLogger() {
	return LOGGER;
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * com.sitewhere.spi.tenant.ITenantManagement#createTenant(com.sitewhere.spi
     * .tenant. request.ITenantCreateRequest)
     */
    @Override
    public ITenant createTenant(ITenantCreateRequest request) throws SiteWhereException {
	return HBaseTenant.createTenant(context, request);
    }

    /*
     * @see com.sitewhere.spi.tenant.ITenantManagement#updateTenant(java.util.UUID,
     * com.sitewhere.spi.tenant.request.ITenantCreateRequest)
     */
    @Override
    public ITenant updateTenant(UUID id, ITenantCreateRequest request) throws SiteWhereException {
	return HBaseTenant.updateTenant(context, null, request);
    }

    /*
     * @see com.sitewhere.spi.tenant.ITenantManagement#getTenant(java.util.UUID)
     */
    @Override
    public ITenant getTenant(UUID id) throws SiteWhereException {
	return HBaseTenant.getTenantById(context, null);
    }

    /*
     * @see
     * com.sitewhere.spi.tenant.ITenantManagement#getTenantByToken(java.lang.String)
     */
    @Override
    public ITenant getTenantByToken(String token) throws SiteWhereException {
	return null;
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * com.sitewhere.spi.tenant.ITenantManagement#listTenants(com.sitewhere.spi.
     * search. user.ITenantSearchCriteria)
     */
    @Override
    public ISearchResults<ITenant> listTenants(ITenantSearchCriteria criteria) throws SiteWhereException {
	return HBaseTenant.listTenants(context, criteria);
    }

    /*
     * @see com.sitewhere.spi.tenant.ITenantManagement#deleteTenant(java.util.UUID,
     * boolean)
     */
    @Override
    public ITenant deleteTenant(UUID tenantId, boolean force) throws SiteWhereException {
	return HBaseTenant.deleteTenant(context, null, force);
    }

    public ISiteWhereHBaseClient getClient() {
	return client;
    }

    public void setClient(ISiteWhereHBaseClient client) {
	this.client = client;
    }

    public IPayloadMarshaler getPayloadMarshaler() {
	return payloadMarshaler;
    }

    public void setPayloadMarshaler(IPayloadMarshaler payloadMarshaler) {
	this.payloadMarshaler = payloadMarshaler;
    }
}