/*
 * Copyright (c) SiteWhere, LLC. All rights reserved. http://www.sitewhere.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package com.sitewhere.hbase.tenant;

import java.util.List;

import org.apache.hadoop.hbase.regionserver.BloomType;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.sitewhere.hbase.HBaseContext;
import com.sitewhere.hbase.ISiteWhereHBase;
import com.sitewhere.hbase.ISiteWhereHBaseClient;
import com.sitewhere.hbase.common.SiteWhereTables;
import com.sitewhere.hbase.encoder.IPayloadMarshaler;
import com.sitewhere.hbase.encoder.JsonPayloadMarshaler;
import com.sitewhere.hbase.user.UserIdManager;
import com.sitewhere.server.lifecycle.LifecycleComponent;
import com.sitewhere.spi.SiteWhereException;
import com.sitewhere.spi.search.ISearchCriteria;
import com.sitewhere.spi.search.ISearchResults;
import com.sitewhere.spi.search.user.ITenantSearchCriteria;
import com.sitewhere.spi.server.lifecycle.LifecycleComponentType;
import com.sitewhere.spi.tenant.ITenant;
import com.sitewhere.spi.tenant.ITenantGroup;
import com.sitewhere.spi.tenant.ITenantGroupElement;
import com.sitewhere.spi.tenant.ITenantManagement;
import com.sitewhere.spi.tenant.request.ITenantCreateRequest;
import com.sitewhere.spi.tenant.request.ITenantGroupCreateRequest;
import com.sitewhere.spi.tenant.request.ITenantGroupElementCreateRequest;

/**
 * HBase implementation of SiteWhere tenant management.
 * 
 * @author Derek
 */
public class HBaseTenantManagement extends LifecycleComponent implements ITenantManagement {

	/** Static logger instance */
	private static final Logger LOGGER = LogManager.getLogger();

	/** Used to communicate with HBase */
	private ISiteWhereHBaseClient client;

	/** Injected payload encoder */
	private IPayloadMarshaler payloadMarshaler = new JsonPayloadMarshaler();

	/** Supplies context to implementation methods */
	private HBaseContext context;

	/** User id manager */
	private UserIdManager userIdManager;

	public HBaseTenantManagement() {
		super(LifecycleComponentType.DataStore);
	}

	@Override
	public void start() throws SiteWhereException {
		ensureTablesExist();

		// Create context from configured options.
		this.context = new HBaseContext();
		context.setClient(getClient());
		context.setPayloadMarshaler(getPayloadMarshaler());

		// Create device id manager instance.
		userIdManager = new UserIdManager();
		userIdManager.load(context);
		context.setUserIdManager(userIdManager);
	}

	@Override
	public void stop() throws SiteWhereException {
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
	public Logger getLogger() {
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
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.sitewhere.spi.tenant.ITenantManagement#updateTenant(java.lang.String,
	 * com.sitewhere.spi.tenant.request.ITenantCreateRequest)
	 */
	@Override
	public ITenant updateTenant(String id, ITenantCreateRequest request) throws SiteWhereException {
		return HBaseTenant.updateTenant(context, id, request);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.sitewhere.spi.tenant.ITenantManagement#getTenantById(java.lang.
	 * String)
	 */
	@Override
	public ITenant getTenantById(String id) throws SiteWhereException {
		return HBaseTenant.getTenantById(context, id);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.sitewhere.spi.tenant.ITenantManagement#getTenantByAuthenticationToken
	 * (java.lang .String)
	 */
	@Override
	public ITenant getTenantByAuthenticationToken(String token) throws SiteWhereException {
		return HBaseTenant.getTenantByAuthenticationToken(context, token);
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
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.sitewhere.spi.tenant.ITenantManagement#deleteTenant(java.lang.String,
	 * boolean)
	 */
	@Override
	public ITenant deleteTenant(String tenantId, boolean force) throws SiteWhereException {
		return HBaseTenant.deleteTenant(context, tenantId, force);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.sitewhere.spi.tenant.ITenantManagement#createTenantGroup(com.
	 * sitewhere.spi.tenant.request.ITenantGroupCreateRequest)
	 */
	@Override
	public ITenantGroup createTenantGroup(ITenantGroupCreateRequest request) throws SiteWhereException {
		return HBaseTenantGroup.createTenantGroup(context, request);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.sitewhere.spi.tenant.ITenantManagement#updateTenantGroup(java.lang.
	 * String, com.sitewhere.spi.tenant.request.ITenantGroupCreateRequest)
	 */
	@Override
	public ITenantGroup updateTenantGroup(String id, ITenantGroupCreateRequest request) throws SiteWhereException {
		return HBaseTenantGroup.updateTenantGroup(context, id, request);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.sitewhere.spi.tenant.ITenantManagement#getTenantGroupById(java.lang.
	 * String)
	 */
	@Override
	public ITenantGroup getTenantGroupByToken(String token) throws SiteWhereException {
		return HBaseTenantGroup.getTenantGroupByToken(context, token);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.sitewhere.spi.tenant.ITenantManagement#listTenantGroups(com.sitewhere
	 * .spi.search.ISearchCriteria)
	 */
	@Override
	public ISearchResults<ITenantGroup> listTenantGroups(ISearchCriteria criteria) throws SiteWhereException {
		return HBaseTenantGroup.listTenantGroups(context, false, criteria);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.sitewhere.spi.tenant.ITenantManagement#deleteTenantGroup(java.lang.
	 * String, boolean)
	 */
	@Override
	public ITenantGroup deleteTenantGroup(String groupId, boolean force) throws SiteWhereException {
		return HBaseTenantGroup.deleteTenantGroup(context, groupId, force);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.sitewhere.spi.tenant.ITenantManagement#addTenantGroupElements(java.
	 * lang.String, java.util.List)
	 */
	@Override
	public List<ITenantGroupElement> addTenantGroupElements(String groupId,
			List<ITenantGroupElementCreateRequest> elements) throws SiteWhereException {
		return HBaseTenantGroupElement.createTenantGroupElements(context, groupId, elements);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.sitewhere.spi.tenant.ITenantManagement#removeTenantGroupElements(java
	 * .lang.String, java.util.List)
	 */
	@Override
	public List<ITenantGroupElement> removeTenantGroupElements(String groupId,
			List<ITenantGroupElementCreateRequest> elements) throws SiteWhereException {
		return HBaseTenantGroupElement.removeTenantGroupElements(context, groupId, elements);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.sitewhere.spi.tenant.ITenantManagement#listTenantGroupElements(java.
	 * lang.String, com.sitewhere.spi.search.ISearchCriteria)
	 */
	@Override
	public ISearchResults<ITenantGroupElement> listTenantGroupElements(String groupId, ISearchCriteria criteria)
			throws SiteWhereException {
		return HBaseTenantGroupElement.listTenantGroupElements(context, groupId, criteria);
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