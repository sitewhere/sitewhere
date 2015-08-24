/*
 * Copyright (c) SiteWhere, LLC. All rights reserved. http://www.sitewhere.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package com.sitewhere.mongodb.user;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;

import com.mongodb.BasicDBObject;
import com.mongodb.DBCollection;
import com.mongodb.DBCursor;
import com.mongodb.DBObject;
import com.mongodb.MongoTimeoutException;
import com.sitewhere.core.SiteWherePersistence;
import com.sitewhere.mongodb.IUserManagementMongoClient;
import com.sitewhere.mongodb.MongoPersistence;
import com.sitewhere.mongodb.common.MongoSiteWhereEntity;
import com.sitewhere.rest.model.user.GrantedAuthority;
import com.sitewhere.rest.model.user.GrantedAuthoritySearchCriteria;
import com.sitewhere.rest.model.user.Tenant;
import com.sitewhere.rest.model.user.User;
import com.sitewhere.server.lifecycle.LifecycleComponent;
import com.sitewhere.spi.SiteWhereException;
import com.sitewhere.spi.SiteWhereSystemException;
import com.sitewhere.spi.error.ErrorCode;
import com.sitewhere.spi.error.ErrorLevel;
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
 * User management implementation that uses MongoDB for persistence.
 * 
 * @author dadams
 */
public class MongoUserManagement extends LifecycleComponent implements IUserManagement {

	/** Static logger instance */
	private static Logger LOGGER = Logger.getLogger(MongoUserManagement.class);

	/** Injected with global SiteWhere Mongo client */
	private IUserManagementMongoClient mongoClient;

	public MongoUserManagement() {
		super(LifecycleComponentType.DataStore);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.sitewhere.spi.server.lifecycle.ILifecycleComponent#start()
	 */
	public void start() throws SiteWhereException {
		/** Ensure that expected indexes exist */
		ensureIndexes();
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

	/**
	 * Ensure that expected collection indexes exist.
	 * 
	 * @throws SiteWhereException
	 */
	protected void ensureIndexes() throws SiteWhereException {
		getMongoClient().getUsersCollection().createIndex(new BasicDBObject("username", 1),
				new BasicDBObject("unique", true));
		getMongoClient().getAuthoritiesCollection().createIndex(new BasicDBObject("authority", 1),
				new BasicDBObject("unique", true));
		getMongoClient().getTenantsCollection().createIndex(new BasicDBObject(MongoTenant.PROP_ID, 1),
				new BasicDBObject("unique", true));
		getMongoClient().getTenantsCollection().createIndex(
				new BasicDBObject(MongoTenant.PROP_AUTH_TOKEN, 1), new BasicDBObject("unique", true));
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.sitewhere.spi.server.lifecycle.ILifecycleComponent#stop()
	 */
	public void stop() throws SiteWhereException {
		LOGGER.info("Mongo user management stopped.");
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.sitewhere.spi.user.IUserManagement#createUser(com.sitewhere.spi.user.request
	 * .IUserCreateRequest)
	 */
	public IUser createUser(IUserCreateRequest request) throws SiteWhereException {
		IUser existing = getUserByUsername(request.getUsername());
		if (existing != null) {
			throw new SiteWhereSystemException(ErrorCode.DuplicateUser, ErrorLevel.ERROR,
					HttpServletResponse.SC_CONFLICT);
		}
		User user = SiteWherePersistence.userCreateLogic(request);

		DBCollection users = getMongoClient().getUsersCollection();
		DBObject created = MongoUser.toDBObject(user);
		MongoPersistence.insert(users, created);
		return user;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.sitewhere.spi.user.IUserManagement#authenticate(java.lang.String,
	 * java.lang.String)
	 */
	public IUser authenticate(String username, String password) throws SiteWhereException {
		if (password == null) {
			throw new SiteWhereSystemException(ErrorCode.InvalidPassword, ErrorLevel.ERROR,
					HttpServletResponse.SC_BAD_REQUEST);
		}
		DBObject userObj = assertUser(username);
		String inPassword = SiteWherePersistence.encodePassoword(password);
		User match = MongoUser.fromDBObject(userObj);
		if (!match.getHashedPassword().equals(inPassword)) {
			throw new SiteWhereSystemException(ErrorCode.InvalidPassword, ErrorLevel.ERROR,
					HttpServletResponse.SC_UNAUTHORIZED);
		}

		// Update last login date.
		match.setLastLogin(new Date());
		DBObject updated = MongoUser.toDBObject(match);
		DBCollection users = getMongoClient().getUsersCollection();
		BasicDBObject query = new BasicDBObject(MongoUser.PROP_USERNAME, username);
		MongoPersistence.update(users, query, updated);

		return match;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.sitewhere.spi.user.IUserManagement#updateUser(java.lang.String,
	 * com.sitewhere.spi.user.request.IUserCreateRequest)
	 */
	public IUser updateUser(String username, IUserCreateRequest request) throws SiteWhereException {
		DBObject existing = assertUser(username);

		// Copy any non-null fields.
		User updatedUser = MongoUser.fromDBObject(existing);
		SiteWherePersistence.userUpdateLogic(request, updatedUser);

		DBObject updated = MongoUser.toDBObject(updatedUser);

		DBCollection users = getMongoClient().getUsersCollection();
		BasicDBObject query = new BasicDBObject(MongoUser.PROP_USERNAME, username);
		MongoPersistence.update(users, query, updated);
		return MongoUser.fromDBObject(updated);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.sitewhere.spi.user.IUserManagement#getUserByUsername(java.lang.String)
	 */
	public IUser getUserByUsername(String username) throws SiteWhereException {
		DBObject dbUser = getUserObjectByUsername(username);
		if (dbUser != null) {
			return MongoUser.fromDBObject(dbUser);
		}
		return null;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.sitewhere.spi.user.IUserManagement#getGrantedAuthorities(java.lang.String)
	 */
	public List<IGrantedAuthority> getGrantedAuthorities(String username) throws SiteWhereException {
		IUser user = getUserByUsername(username);
		List<String> userAuths = user.getAuthorities();
		List<IGrantedAuthority> all = listGrantedAuthorities(new GrantedAuthoritySearchCriteria());
		List<IGrantedAuthority> matched = new ArrayList<IGrantedAuthority>();
		for (IGrantedAuthority auth : all) {
			if (userAuths.contains(auth.getAuthority())) {
				matched.add(auth);
			}
		}
		return matched;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.sitewhere.spi.user.IUserManagement#addGrantedAuthorities(java.lang.String,
	 * java.util.List)
	 */
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
	public List<IGrantedAuthority> removeGrantedAuthorities(String username, List<String> authorities)
			throws SiteWhereException {
		throw new SiteWhereException("Not implemented.");
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.sitewhere.spi.user.IUserManagement#listUsers(com.sitewhere.spi.user.request
	 * .IUserSearchCriteria)
	 */
	public List<IUser> listUsers(IUserSearchCriteria criteria) throws SiteWhereException {
		try {
			DBCollection users = getMongoClient().getUsersCollection();
			DBObject dbCriteria = new BasicDBObject();
			if (!criteria.isIncludeDeleted()) {
				MongoSiteWhereEntity.setDeleted(dbCriteria, false);
			}
			DBCursor cursor = users.find(dbCriteria).sort(new BasicDBObject(MongoUser.PROP_USERNAME, 1));
			List<IUser> matches = new ArrayList<IUser>();
			try {
				while (cursor.hasNext()) {
					DBObject match = cursor.next();
					matches.add(MongoUser.fromDBObject(match));
				}
			} finally {
				cursor.close();
			}
			return matches;
		} catch (MongoTimeoutException e) {
			throw new SiteWhereException("Connection to MongoDB lost.", e);
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.sitewhere.spi.user.IUserManagement#deleteUser(java.lang.String, boolean)
	 */
	public IUser deleteUser(String username, boolean force) throws SiteWhereException {
		DBObject existing = assertUser(username);
		if (force) {
			DBCollection users = getMongoClient().getUsersCollection();
			MongoPersistence.delete(users, existing);
			return MongoUser.fromDBObject(existing);
		} else {
			MongoSiteWhereEntity.setDeleted(existing, true);
			BasicDBObject query = new BasicDBObject(MongoUser.PROP_USERNAME, username);
			DBCollection users = getMongoClient().getUsersCollection();
			MongoPersistence.update(users, query, existing);
			return MongoUser.fromDBObject(existing);
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.sitewhere.spi.user.IUserManagement#createGrantedAuthority(com.sitewhere.spi
	 * .user.request. IGrantedAuthorityCreateRequest)
	 */
	public IGrantedAuthority createGrantedAuthority(IGrantedAuthorityCreateRequest request)
			throws SiteWhereException {
		IGrantedAuthority existing = getGrantedAuthorityByName(request.getAuthority());
		if (existing != null) {
			throw new SiteWhereSystemException(ErrorCode.DuplicateAuthority, ErrorLevel.ERROR,
					HttpServletResponse.SC_CONFLICT);
		}
		GrantedAuthority auth = new GrantedAuthority();
		auth.setAuthority(request.getAuthority());
		auth.setDescription(request.getDescription());

		DBCollection auths = getMongoClient().getAuthoritiesCollection();
		DBObject created = MongoGrantedAuthority.toDBObject(auth);
		MongoPersistence.insert(auths, created);
		return auth;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.sitewhere.spi.user.IUserManagement#getGrantedAuthorityByName(java.lang.String)
	 */
	public IGrantedAuthority getGrantedAuthorityByName(String name) throws SiteWhereException {
		DBObject dbAuth = getGrantedAuthorityObjectByName(name);
		if (dbAuth != null) {
			return MongoGrantedAuthority.fromDBObject(dbAuth);
		}
		return null;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.sitewhere.spi.user.IUserManagement#updateGrantedAuthority(java.lang.String,
	 * com.sitewhere.spi.user.request.IGrantedAuthorityCreateRequest)
	 */
	public IGrantedAuthority updateGrantedAuthority(String name, IGrantedAuthorityCreateRequest request)
			throws SiteWhereException {
		throw new SiteWhereException("Not implemented.");
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.sitewhere.spi.user.IUserManagement#listGrantedAuthorities(com.sitewhere.spi
	 * .user. IGrantedAuthoritySearchCriteria)
	 */
	public List<IGrantedAuthority> listGrantedAuthorities(IGrantedAuthoritySearchCriteria criteria)
			throws SiteWhereException {
		try {
			DBCollection auths = getMongoClient().getAuthoritiesCollection();
			DBCursor cursor = auths.find().sort(new BasicDBObject(MongoGrantedAuthority.PROP_AUTHORITY, 1));
			List<IGrantedAuthority> matches = new ArrayList<IGrantedAuthority>();
			try {
				while (cursor.hasNext()) {
					DBObject match = cursor.next();
					matches.add(MongoGrantedAuthority.fromDBObject(match));
				}
			} finally {
				cursor.close();
			}
			return matches;
		} catch (MongoTimeoutException e) {
			throw new SiteWhereException("Connection to MongoDB lost.", e);
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.sitewhere.spi.user.IUserManagement#deleteGrantedAuthority(java.lang.String)
	 */
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
		ITenant existing = getTenantById(request.getId());
		if (existing != null) {
			throw new SiteWhereSystemException(ErrorCode.DuplicateTenantId, ErrorLevel.ERROR);
		}

		// Use common logic so all backend implementations work the same.
		Tenant tenant = SiteWherePersistence.tenantCreateLogic(request);

		DBCollection tenants = getMongoClient().getTenantsCollection();
		DBObject created = MongoTenant.toDBObject(tenant);
		MongoPersistence.insert(tenants, created);

		return MongoTenant.fromDBObject(created);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.sitewhere.spi.user.IUserManagement#updateTenant(java.lang.String,
	 * com.sitewhere.spi.user.request.ITenantCreateRequest)
	 */
	@Override
	public ITenant updateTenant(String id, ITenantCreateRequest request) throws SiteWhereException {
		DBObject dbExisting = assertTenant(id);
		Tenant existing = MongoTenant.fromDBObject(dbExisting);

		// Use common update logic so that backend implemetations act the same way.
		SiteWherePersistence.tenantUpdateLogic(request, existing);
		DBObject updated = MongoTenant.toDBObject(existing);

		BasicDBObject query = new BasicDBObject(MongoTenant.PROP_ID, id);
		DBCollection tenants = getMongoClient().getTenantsCollection();
		MongoPersistence.update(tenants, query, updated);

		return MongoTenant.fromDBObject(updated);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.sitewhere.spi.user.IUserManagement#getTenantById(java.lang.String)
	 */
	@Override
	public ITenant getTenantById(String id) throws SiteWhereException {
		DBObject dbExisting = getTenantObjectById(id);
		if (dbExisting == null) {
			return null;
		}
		return MongoTenant.fromDBObject(dbExisting);
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
		try {
			DBCollection tenants = getMongoClient().getTenantsCollection();
			BasicDBObject query = new BasicDBObject(MongoTenant.PROP_AUTH_TOKEN, token);
			DBObject match = tenants.findOne(query);
			if (match == null) {
				return null;
			}
			return MongoTenant.fromDBObject(match);
		} catch (MongoTimeoutException e) {
			throw new SiteWhereException("Connection to MongoDB lost.", e);
		}
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
		DBCollection tenants = getMongoClient().getTenantsCollection();
		BasicDBObject dbCriteria = new BasicDBObject();
		if (criteria.getUserId() != null) {
			dbCriteria.append(MongoTenant.PROP_AUTH_USERS, criteria.getUserId());
		}
		BasicDBObject sort = new BasicDBObject(MongoTenant.PROP_NAME, 1);
		return MongoPersistence.search(ITenant.class, tenants, dbCriteria, sort, criteria);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.sitewhere.spi.user.IUserManagement#deleteTenant(java.lang.String, boolean)
	 */
	@Override
	public ITenant deleteTenant(String tenantId, boolean force) throws SiteWhereException {
		DBObject existing = assertTenant(tenantId);
		if (force) {
			DBCollection tenants = getMongoClient().getTenantsCollection();
			MongoPersistence.delete(tenants, existing);
			return MongoTenant.fromDBObject(existing);
		} else {
			MongoSiteWhereEntity.setDeleted(existing, true);
			BasicDBObject query = new BasicDBObject(MongoTenant.PROP_ID, tenantId);
			DBCollection tenants = getMongoClient().getTenantsCollection();
			MongoPersistence.update(tenants, query, existing);
			return MongoTenant.fromDBObject(existing);
		}
	}

	/**
	 * Get the {@link DBObject} for a User given username. Throw an exception if not
	 * found.
	 * 
	 * @param username
	 * @return
	 * @throws SiteWhereException
	 */
	protected DBObject assertUser(String username) throws SiteWhereException {
		DBObject match = getUserObjectByUsername(username);
		if (match == null) {
			throw new SiteWhereSystemException(ErrorCode.InvalidUsername, ErrorLevel.ERROR,
					HttpServletResponse.SC_NOT_FOUND);
		}
		return match;
	}

	/**
	 * Get the DBObject for a User given unique username.
	 * 
	 * @param username
	 * @return
	 * @throws SiteWhereException
	 */
	protected DBObject getUserObjectByUsername(String username) throws SiteWhereException {
		try {
			DBCollection users = getMongoClient().getUsersCollection();
			BasicDBObject query = new BasicDBObject(MongoUser.PROP_USERNAME, username);
			return users.findOne(query);
		} catch (MongoTimeoutException e) {
			throw new SiteWhereException("Connection to MongoDB lost.", e);
		}
	}

	/**
	 * Get the {@link DBObject} for a GrantedAuthority given name. Throw an exception if
	 * not found.
	 * 
	 * @param name
	 * @return
	 * @throws SiteWhereException
	 */
	protected DBObject assertGrantedAuthority(String name) throws SiteWhereException {
		DBObject match = getGrantedAuthorityObjectByName(name);
		if (match == null) {
			throw new SiteWhereSystemException(ErrorCode.InvalidAuthority, ErrorLevel.ERROR,
					HttpServletResponse.SC_NOT_FOUND);
		}
		return match;
	}

	/**
	 * Get the DBObject for a GrantedAuthority given unique name.
	 * 
	 * @param name
	 * @return
	 * @throws SiteWhereException
	 */
	protected DBObject getGrantedAuthorityObjectByName(String name) throws SiteWhereException {
		try {
			DBCollection auths = getMongoClient().getAuthoritiesCollection();
			BasicDBObject query = new BasicDBObject(MongoGrantedAuthority.PROP_AUTHORITY, name);
			return auths.findOne(query);
		} catch (MongoTimeoutException e) {
			throw new SiteWhereException("Connection to MongoDB lost.", e);
		}
	}

	/**
	 * Get the {@link DBObject} for a tenant given id. Throw an exception if not found.
	 * 
	 * @param id
	 * @return
	 * @throws SiteWhereException
	 */
	protected DBObject assertTenant(String id) throws SiteWhereException {
		DBObject match = getTenantObjectById(id);
		if (match == null) {
			throw new SiteWhereSystemException(ErrorCode.InvalidTenantId, ErrorLevel.ERROR,
					HttpServletResponse.SC_NOT_FOUND);
		}
		return match;
	}

	/**
	 * Get the DBObject for a Tenant given unique id.
	 * 
	 * @param id
	 * @return
	 * @throws SiteWhereException
	 */
	protected DBObject getTenantObjectById(String id) throws SiteWhereException {
		try {
			DBCollection tenants = getMongoClient().getTenantsCollection();
			BasicDBObject query = new BasicDBObject(MongoTenant.PROP_ID, id);
			return tenants.findOne(query);
		} catch (MongoTimeoutException e) {
			throw new SiteWhereException("Connection to MongoDB lost.", e);
		}
	}

	public IUserManagementMongoClient getMongoClient() {
		return mongoClient;
	}

	public void setMongoClient(IUserManagementMongoClient mongoClient) {
		this.mongoClient = mongoClient;
	}
}