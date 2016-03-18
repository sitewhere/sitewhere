/*
 * Copyright (c) SiteWhere, LLC. All rights reserved. http://www.sitewhere.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package com.sitewhere.mongodb.tenant;

import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;

import com.mongodb.BasicDBObject;
import com.mongodb.DBCollection;
import com.mongodb.DBObject;
import com.mongodb.MongoTimeoutException;
import com.sitewhere.core.SiteWherePersistence;
import com.sitewhere.mongodb.IUserManagementMongoClient;
import com.sitewhere.mongodb.MongoPersistence;
import com.sitewhere.mongodb.common.MongoSiteWhereEntity;
import com.sitewhere.mongodb.user.MongoTenant;
import com.sitewhere.rest.model.user.Tenant;
import com.sitewhere.server.lifecycle.LifecycleComponent;
import com.sitewhere.spi.SiteWhereException;
import com.sitewhere.spi.SiteWhereSystemException;
import com.sitewhere.spi.error.ErrorCode;
import com.sitewhere.spi.error.ErrorLevel;
import com.sitewhere.spi.search.ISearchResults;
import com.sitewhere.spi.search.user.ITenantSearchCriteria;
import com.sitewhere.spi.server.lifecycle.LifecycleComponentType;
import com.sitewhere.spi.tenant.ITenant;
import com.sitewhere.spi.tenant.ITenantManagement;
import com.sitewhere.spi.tenant.request.ITenantCreateRequest;

/**
 * Tenant management implementation that uses MongoDB for persistence.
 * 
 * @author dadams
 */
public class MongoTenantManagement extends LifecycleComponent implements ITenantManagement {

	/** Static logger instance */
	private static Logger LOGGER = Logger.getLogger(MongoTenantManagement.class);

	/** Injected with global SiteWhere Mongo client */
	private IUserManagementMongoClient mongoClient;

	public MongoTenantManagement() {
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
		getMongoClient().getTenantsCollection().createIndex(new BasicDBObject(MongoTenant.PROP_ID, 1),
				new BasicDBObject("unique", true));
		getMongoClient().getTenantsCollection().createIndex(new BasicDBObject(MongoTenant.PROP_AUTH_TOKEN, 1),
				new BasicDBObject("unique", true));
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
	 * com.sitewhere.spi.tenant.ITenantManagement#createTenant(com.sitewhere.spi.tenant.
	 * request.ITenantCreateRequest)
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
	 * @see com.sitewhere.spi.tenant.ITenantManagement#updateTenant(java.lang.String,
	 * com.sitewhere.spi.tenant.request.ITenantCreateRequest)
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
	 * @see com.sitewhere.spi.tenant.ITenantManagement#getTenantById(java.lang.String)
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
	 * com.sitewhere.spi.tenant.ITenantManagement#getTenantByAuthenticationToken(java.lang
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
	 * com.sitewhere.spi.tenant.ITenantManagement#listTenants(com.sitewhere.spi.search.
	 * user.ITenantSearchCriteria)
	 */
	@Override
	public ISearchResults<ITenant> listTenants(ITenantSearchCriteria criteria) throws SiteWhereException {
		DBCollection tenants = getMongoClient().getTenantsCollection();
		BasicDBObject dbCriteria = new BasicDBObject();
		if (criteria.getUserId() != null) {
			dbCriteria.append(MongoTenant.PROP_AUTH_USERS, criteria.getUserId());
		}
		BasicDBObject sort = new BasicDBObject(MongoTenant.PROP_NAME, 1);
		ISearchResults<ITenant> list =
				MongoPersistence.search(ITenant.class, tenants, dbCriteria, sort, criteria);
		SiteWherePersistence.tenantListLogic(list.getResults(), criteria);
		return list;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.sitewhere.spi.tenant.ITenantManagement#deleteTenant(java.lang.String,
	 * boolean)
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
