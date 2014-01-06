/*
 * HBaseUserManagement.java 
 * --------------------------------------------------------------------------------------
 * Copyright (c) Reveal Technologies, LLC. All rights reserved. http://www.reveal-tech.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package com.sitewhere.hbase.user;

import java.util.List;

import org.apache.hadoop.hbase.regionserver.StoreFile.BloomType;
import org.apache.log4j.Logger;

import com.sitewhere.hbase.ISiteWhereHBase;
import com.sitewhere.hbase.ISiteWhereHBaseClient;
import com.sitewhere.hbase.common.SiteWhereTables;
import com.sitewhere.hbase.uid.IdManager;
import com.sitewhere.spi.SiteWhereException;
import com.sitewhere.spi.user.IGrantedAuthority;
import com.sitewhere.spi.user.IGrantedAuthoritySearchCriteria;
import com.sitewhere.spi.user.IUser;
import com.sitewhere.spi.user.IUserManagement;
import com.sitewhere.spi.user.IUserSearchCriteria;
import com.sitewhere.spi.user.request.IGrantedAuthorityCreateRequest;
import com.sitewhere.spi.user.request.IUserCreateRequest;

/**
 * HBase implementation of SiteWhere user management.
 * 
 * @author Derek
 */
public class HBaseUserManagement implements IUserManagement {

	/** Static logger instance */
	private static final Logger LOGGER = Logger.getLogger(HBaseUserManagement.class);

	/** Used to communicate with HBase */
	private ISiteWhereHBaseClient client;

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.sitewhere.spi.ISiteWhereLifecycle#start()
	 */
	@Override
	public void start() throws SiteWhereException {
		LOGGER.info("HBase user management starting...");

		LOGGER.info("Verifying tables...");
		ensureTablesExist();

		LOGGER.info("Loading id management...");
		IdManager.getInstance().load(client);

		LOGGER.info("HBase user management started.");
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.sitewhere.spi.ISiteWhereLifecycle#stop()
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
		return HBaseUser.createUser(client, request);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.sitewhere.spi.user.IUserManagement#authenticate(java.lang.String,
	 * java.lang.String)
	 */
	@Override
	public IUser authenticate(String username, String password) throws SiteWhereException {
		return HBaseUser.authenticate(client, username, password);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.sitewhere.spi.user.IUserManagement#updateUser(java.lang.String,
	 * com.sitewhere.spi.user.request.IUserCreateRequest)
	 */
	@Override
	public IUser updateUser(String username, IUserCreateRequest request) throws SiteWhereException {
		return HBaseUser.updateUser(client, username, request);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.sitewhere.spi.user.IUserManagement#getUserByUsername(java.lang.String)
	 */
	@Override
	public IUser getUserByUsername(String username) throws SiteWhereException {
		return HBaseUser.getUserByUsername(client, username);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.sitewhere.spi.user.IUserManagement#getGrantedAuthorities(java.lang.String)
	 */
	@Override
	public List<IGrantedAuthority> getGrantedAuthorities(String username) throws SiteWhereException {
		return HBaseUser.getGrantedAuthorities(client, username);
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
		return HBaseUser.listUsers(client, criteria);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.sitewhere.spi.user.IUserManagement#deleteUser(java.lang.String, boolean)
	 */
	@Override
	public IUser deleteUser(String username, boolean force) throws SiteWhereException {
		return HBaseUser.deleteUser(client, username, force);
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
		return HBaseGrantedAuthority.createGrantedAuthority(client, request);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.sitewhere.spi.user.IUserManagement#getGrantedAuthorityByName(java.lang.String)
	 */
	@Override
	public IGrantedAuthority getGrantedAuthorityByName(String name) throws SiteWhereException {
		return HBaseGrantedAuthority.getGrantedAuthorityByName(client, name);
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
		return HBaseGrantedAuthority.listGrantedAuthorities(client, criteria);
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

	public ISiteWhereHBaseClient getClient() {
		return client;
	}

	public void setClient(ISiteWhereHBaseClient client) {
		this.client = client;
	}
}