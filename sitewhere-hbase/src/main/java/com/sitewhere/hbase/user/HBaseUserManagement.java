/*
 * Copyright (c) SiteWhere, LLC. All rights reserved. http://www.sitewhere.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package com.sitewhere.hbase.user;

import java.util.List;

import org.apache.hadoop.hbase.regionserver.BloomType;
import org.apache.log4j.Logger;

import com.sitewhere.hbase.HBaseContext;
import com.sitewhere.hbase.ISiteWhereHBase;
import com.sitewhere.hbase.ISiteWhereHBaseClient;
import com.sitewhere.hbase.common.SiteWhereTables;
import com.sitewhere.hbase.encoder.IPayloadMarshaler;
import com.sitewhere.hbase.encoder.JsonPayloadMarshaler;
import com.sitewhere.hbase.tenant.HBaseTenant;
import com.sitewhere.server.lifecycle.LifecycleComponent;
import com.sitewhere.spi.SiteWhereException;
import com.sitewhere.spi.search.ISearchResults;
import com.sitewhere.spi.search.user.ITenantSearchCriteria;
import com.sitewhere.spi.server.lifecycle.LifecycleComponentType;
import com.sitewhere.spi.user.IGrantedAuthority;
import com.sitewhere.spi.user.IGrantedAuthoritySearchCriteria;
import com.sitewhere.spi.user.ITenant;
import com.sitewhere.spi.user.IUser;
import com.sitewhere.spi.user.IUserManagement;
import com.sitewhere.spi.user.IUserSearchCriteria;
import com.sitewhere.spi.user.request.IGrantedAuthorityCreateRequest;
import com.sitewhere.spi.user.request.ITenantCreateRequest;
import com.sitewhere.spi.user.request.IUserCreateRequest;

/**
 * HBase implementation of SiteWhere user management.
 * 
 * @author Derek
 */
public class HBaseUserManagement extends LifecycleComponent implements IUserManagement {

	/** Static logger instance */
	private static final Logger LOGGER = Logger.getLogger(HBaseUserManagement.class);

	/** Used to communicate with HBase */
	private ISiteWhereHBaseClient client;

	/** Injected payload encoder */
	private IPayloadMarshaler payloadMarshaler = new JsonPayloadMarshaler();

	/** Supplies context to implementation methods */
	private HBaseContext context;

	/** User id manager */
	private UserIdManager userIdManager;

	public HBaseUserManagement() {
		super(LifecycleComponentType.DataStore);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.sitewhere.spi.server.lifecycle.ILifecycleComponent#start()
	 */
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
	 * @see com.sitewhere.spi.server.lifecycle.ILifecycleComponent#stop()
	 */
	@Override
	public void stop() throws SiteWhereException {
		LOGGER.info("HBase user management stopped.");
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
	 * @see
	 * com.sitewhere.spi.user.IUserManagement#createUser(com.sitewhere.spi.user.request
	 * .IUserCreateRequest)
	 */
	@Override
	public IUser createUser(IUserCreateRequest request) throws SiteWhereException {
		return HBaseUser.createUser(context, request);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.sitewhere.spi.user.IUserManagement#authenticate(java.lang.String,
	 * java.lang.String)
	 */
	@Override
	public IUser authenticate(String username, String password) throws SiteWhereException {
		return HBaseUser.authenticate(context, username, password);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.sitewhere.spi.user.IUserManagement#updateUser(java.lang.String,
	 * com.sitewhere.spi.user.request.IUserCreateRequest)
	 */
	@Override
	public IUser updateUser(String username, IUserCreateRequest request) throws SiteWhereException {
		return HBaseUser.updateUser(context, username, request);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.sitewhere.spi.user.IUserManagement#getUserByUsername(java.lang.String)
	 */
	@Override
	public IUser getUserByUsername(String username) throws SiteWhereException {
		return HBaseUser.getUserByUsername(context, username);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.sitewhere.spi.user.IUserManagement#getGrantedAuthorities(java.lang.String)
	 */
	@Override
	public List<IGrantedAuthority> getGrantedAuthorities(String username) throws SiteWhereException {
		return HBaseUser.getGrantedAuthorities(context, username);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.sitewhere.spi.user.IUserManagement#addGrantedAuthorities(java.lang.String,
	 * java.util.List)
	 */
	@Override
	public List<IGrantedAuthority> addGrantedAuthorities(String username, List<String> authorities)
			throws SiteWhereException {
		throw new SiteWhereException("Not implemented.");
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.sitewhere.spi.user.IUserManagement#removeGrantedAuthorities(java.lang.String,
	 * java.util.List)
	 */
	@Override
	public List<IGrantedAuthority> removeGrantedAuthorities(String username, List<String> authorities)
			throws SiteWhereException {
		throw new SiteWhereException("Not implemented.");
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.sitewhere.spi.user.IUserManagement#listUsers(com.sitewhere.spi.user.
	 * IUserSearchCriteria)
	 */
	@Override
	public List<IUser> listUsers(IUserSearchCriteria criteria) throws SiteWhereException {
		return HBaseUser.listUsers(context, criteria);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.sitewhere.spi.user.IUserManagement#deleteUser(java.lang.String, boolean)
	 */
	@Override
	public IUser deleteUser(String username, boolean force) throws SiteWhereException {
		return HBaseUser.deleteUser(context, username, force);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.sitewhere.spi.user.IUserManagement#createGrantedAuthority(com.sitewhere.spi
	 * .user.request.IGrantedAuthorityCreateRequest)
	 */
	@Override
	public IGrantedAuthority createGrantedAuthority(IGrantedAuthorityCreateRequest request)
			throws SiteWhereException {
		return HBaseGrantedAuthority.createGrantedAuthority(context, request);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.sitewhere.spi.user.IUserManagement#getGrantedAuthorityByName(java.lang.String)
	 */
	@Override
	public IGrantedAuthority getGrantedAuthorityByName(String name) throws SiteWhereException {
		return HBaseGrantedAuthority.getGrantedAuthorityByName(context, name);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.sitewhere.spi.user.IUserManagement#updateGrantedAuthority(java.lang.String,
	 * com.sitewhere.spi.user.request.IGrantedAuthorityCreateRequest)
	 */
	@Override
	public IGrantedAuthority updateGrantedAuthority(String name, IGrantedAuthorityCreateRequest request)
			throws SiteWhereException {
		throw new SiteWhereException("Not implemented.");
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.sitewhere.spi.user.IUserManagement#listGrantedAuthorities(com.sitewhere.spi
	 * .user.IGrantedAuthoritySearchCriteria)
	 */
	@Override
	public List<IGrantedAuthority> listGrantedAuthorities(IGrantedAuthoritySearchCriteria criteria)
			throws SiteWhereException {
		return HBaseGrantedAuthority.listGrantedAuthorities(context, criteria);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.sitewhere.spi.user.IUserManagement#deleteGrantedAuthority(java.lang.String)
	 */
	@Override
	public void deleteGrantedAuthority(String authority) throws SiteWhereException {
		throw new SiteWhereException("Not implemented.");
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.sitewhere.spi.user.IUserManagement#createTenant(com.sitewhere.spi.user.request
	 * .ITenantCreateRequest)
	 */
	@Override
	public ITenant createTenant(ITenantCreateRequest request) throws SiteWhereException {
		return HBaseTenant.createTenant(context, request);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.sitewhere.spi.user.IUserManagement#updateTenant(java.lang.String,
	 * com.sitewhere.spi.user.request.ITenantCreateRequest)
	 */
	@Override
	public ITenant updateTenant(String id, ITenantCreateRequest request) throws SiteWhereException {
		return HBaseTenant.updateTenant(context, id, request);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.sitewhere.spi.user.IUserManagement#getTenantById(java.lang.String)
	 */
	@Override
	public ITenant getTenantById(String id) throws SiteWhereException {
		return HBaseTenant.getTenantById(context, id);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.sitewhere.spi.user.IUserManagement#getTenantByAuthenticationToken(java.lang
	 * .String)
	 */
	@Override
	public ITenant getTenantByAuthenticationToken(String token) throws SiteWhereException {
		return HBaseTenant.getTenantByAuthenticationToken(context, token);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.sitewhere.spi.user.IUserManagement#listTenants(com.sitewhere.spi.search.user
	 * .ITenantSearchCriteria)
	 */
	@Override
	public ISearchResults<ITenant> listTenants(ITenantSearchCriteria criteria) throws SiteWhereException {
		return HBaseTenant.listTenants(context, criteria);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.sitewhere.spi.user.IUserManagement#deleteTenant(java.lang.String, boolean)
	 */
	@Override
	public ITenant deleteTenant(String tenantId, boolean force) throws SiteWhereException {
		return HBaseTenant.deleteTenant(context, tenantId, force);
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