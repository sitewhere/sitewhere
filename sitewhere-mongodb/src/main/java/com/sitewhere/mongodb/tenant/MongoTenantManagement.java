/*
 * Copyright (c) SiteWhere, LLC. All rights reserved. http://www.sitewhere.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package com.sitewhere.mongodb.tenant;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;

import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;

import com.mongodb.BasicDBList;
import com.mongodb.BasicDBObject;
import com.mongodb.DBCollection;
import com.mongodb.DBCursor;
import com.mongodb.DBObject;
import com.mongodb.MongoTimeoutException;
import com.mongodb.WriteResult;
import com.sitewhere.core.SiteWherePersistence;
import com.sitewhere.mongodb.IUserManagementMongoClient;
import com.sitewhere.mongodb.MongoPersistence;
import com.sitewhere.mongodb.common.MongoSiteWhereEntity;
import com.sitewhere.rest.model.tenant.Tenant;
import com.sitewhere.rest.model.tenant.TenantGroup;
import com.sitewhere.rest.model.tenant.TenantGroupElement;
import com.sitewhere.server.lifecycle.LifecycleComponent;
import com.sitewhere.spi.SiteWhereException;
import com.sitewhere.spi.SiteWhereSystemException;
import com.sitewhere.spi.error.ErrorCode;
import com.sitewhere.spi.error.ErrorLevel;
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
		getMongoClient().getTenantGroupsCollection().createIndex(new BasicDBObject(MongoTenantGroup.PROP_TOKEN, 1),
				new BasicDBObject("unique", true));
		getMongoClient().getTenantGroupElementsCollection()
				.createIndex(new BasicDBObject(MongoTenantGroupElement.PROP_GROUP_TOKEN, 1)
						.append(MongoTenantGroupElement.PROP_TENANT_ID, 1), new BasicDBObject("unique", true));
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
	 * com.sitewhere.spi.tenant.ITenantManagement#createTenant(com.sitewhere.spi
	 * .tenant. request.ITenantCreateRequest)
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
	 * @see
	 * com.sitewhere.spi.tenant.ITenantManagement#updateTenant(java.lang.String,
	 * com.sitewhere.spi.tenant.request.ITenantCreateRequest)
	 */
	@Override
	public ITenant updateTenant(String id, ITenantCreateRequest request) throws SiteWhereException {
		DBObject dbExisting = assertTenant(id);
		Tenant existing = MongoTenant.fromDBObject(dbExisting);

		// Use common update logic so that backend implemetations act the same
		// way.
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
	 * @see com.sitewhere.spi.tenant.ITenantManagement#getTenantById(java.lang.
	 * String)
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
	 * com.sitewhere.spi.tenant.ITenantManagement#getTenantByAuthenticationToken
	 * (java.lang .String)
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
	 * com.sitewhere.spi.tenant.ITenantManagement#listTenants(com.sitewhere.spi.
	 * search. user.ITenantSearchCriteria)
	 */
	@Override
	public ISearchResults<ITenant> listTenants(ITenantSearchCriteria criteria) throws SiteWhereException {
		DBCollection tenants = getMongoClient().getTenantsCollection();
		BasicDBObject dbCriteria = new BasicDBObject();
		if (criteria.getTextSearch() != null) {
			try {
				Pattern regex = Pattern.compile(Pattern.quote(criteria.getTextSearch()));
				DBObject idSearch = new BasicDBObject(MongoTenant.PROP_ID, regex);
				DBObject nameSearch = new BasicDBObject(MongoTenant.PROP_NAME, regex);
				BasicDBList or = new BasicDBList();
				or.add(idSearch);
				or.add(nameSearch);
				dbCriteria.append("$or", or);
			} catch (PatternSyntaxException e) {
				LOGGER.warn("Invalid regex for searching tenant list. Ignoring.");
			}
		}
		if (criteria.getUserId() != null) {
			dbCriteria.append(MongoTenant.PROP_AUTH_USERS, criteria.getUserId());
		}
		BasicDBObject sort = new BasicDBObject(MongoTenant.PROP_NAME, 1);
		ISearchResults<ITenant> list = MongoPersistence.search(ITenant.class, tenants, dbCriteria, sort, criteria);
		SiteWherePersistence.tenantListLogic(list.getResults(), criteria);
		return list;
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

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.sitewhere.spi.tenant.ITenantManagement#createTenantGroup(com.
	 * sitewhere.spi. tenant.request.ITenantGroupCreateRequest)
	 */
	@Override
	public ITenantGroup createTenantGroup(ITenantGroupCreateRequest request) throws SiteWhereException {
		// Use common logic so all backend implementations work the same.
		String uuid = ((request.getToken() != null) ? request.getToken() : UUID.randomUUID().toString());
		TenantGroup group = SiteWherePersistence.tenantGroupCreateLogic(uuid, request);

		DBCollection tgroups = getMongoClient().getTenantGroupsCollection();
		DBObject created = MongoTenantGroup.toDBObject(group);
		MongoPersistence.insert(tgroups, created);

		return MongoTenantGroup.fromDBObject(created);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.sitewhere.spi.tenant.ITenantManagement#updateTenantGroup(java.lang.
	 * String, com.sitewhere.spi.tenant.request.ITenantGroupCreateRequest)
	 */
	@Override
	public ITenantGroup updateTenantGroup(String token, ITenantGroupCreateRequest request) throws SiteWhereException {
		DBObject dbExisting = assertTenantGroup(token);
		TenantGroup existing = MongoTenantGroup.fromDBObject(dbExisting);

		// Use common update logic.
		SiteWherePersistence.tenantGroupUpdateLogic(request, existing);
		DBObject updated = MongoTenantGroup.toDBObject(existing);

		BasicDBObject query = new BasicDBObject(MongoTenantGroup.PROP_TOKEN, token);
		DBCollection tgroups = getMongoClient().getTenantGroupsCollection();
		MongoPersistence.update(tgroups, query, updated);

		return MongoTenantGroup.fromDBObject(updated);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.sitewhere.spi.tenant.ITenantManagement#getTenantGroupByToken(java.
	 * lang.String)
	 */
	@Override
	public ITenantGroup getTenantGroupByToken(String token) throws SiteWhereException {
		DBObject dbExisting = getTenantGroupObjectByToken(token);
		if (dbExisting == null) {
			return null;
		}
		return MongoTenantGroup.fromDBObject(dbExisting);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.sitewhere.spi.tenant.ITenantManagement#listTenantGroups(com.sitewhere
	 * .spi. search.ISearchCriteria)
	 */
	@Override
	public ISearchResults<ITenantGroup> listTenantGroups(ISearchCriteria criteria) throws SiteWhereException {
		DBCollection tgroups = getMongoClient().getTenantGroupsCollection();
		BasicDBObject dbCriteria = new BasicDBObject();
		BasicDBObject sort = new BasicDBObject(MongoTenantGroup.PROP_NAME, 1);
		ISearchResults<ITenantGroup> list = MongoPersistence.search(ITenantGroup.class, tgroups, dbCriteria, sort,
				criteria);
		return list;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.sitewhere.spi.tenant.ITenantManagement#deleteTenantGroup(java.lang.
	 * String, boolean)
	 */
	@Override
	public ITenantGroup deleteTenantGroup(String token, boolean force) throws SiteWhereException {
		DBObject existing = assertTenantGroup(token);
		if (force) {
			DBCollection tgroups = getMongoClient().getTenantGroupsCollection();
			MongoPersistence.delete(tgroups, existing);
			return MongoTenantGroup.fromDBObject(existing);
		} else {
			MongoSiteWhereEntity.setDeleted(existing, true);
			BasicDBObject query = new BasicDBObject(MongoTenantGroup.PROP_TOKEN, token);
			DBCollection tgroups = getMongoClient().getTenantGroupsCollection();
			MongoPersistence.update(tgroups, query, existing);
			return MongoTenantGroup.fromDBObject(existing);
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.sitewhere.spi.tenant.ITenantManagement#addTenantGroupElements(java.
	 * lang.String, java.util.List)
	 */
	@Override
	public List<ITenantGroupElement> addTenantGroupElements(String groupToken,
			List<ITenantGroupElementCreateRequest> elements) throws SiteWhereException {
		List<ITenantGroupElement> results = new ArrayList<ITenantGroupElement>();
		for (ITenantGroupElementCreateRequest request : elements) {
			TenantGroupElement element = SiteWherePersistence.tenantGroupElementCreateLogic(groupToken, request);
			DBObject created = MongoTenantGroupElement.toDBObject(element);
			MongoPersistence.insert(getMongoClient().getTenantGroupElementsCollection(), created);
			results.add(MongoTenantGroupElement.fromDBObject(created));
		}
		return results;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.sitewhere.spi.tenant.ITenantManagement#removeTenantGroupElements(java
	 * .lang. String, java.util.List)
	 */
	@Override
	public List<ITenantGroupElement> removeTenantGroupElements(String groupToken,
			List<ITenantGroupElementCreateRequest> elements) throws SiteWhereException {
		List<ITenantGroupElement> deleted = new ArrayList<ITenantGroupElement>();
		for (ITenantGroupElementCreateRequest request : elements) {
			BasicDBObject match = new BasicDBObject(MongoTenantGroupElement.PROP_GROUP_TOKEN, groupToken)
					.append(MongoTenantGroupElement.PROP_TENANT_ID, request.getTenantId());
			DBCursor found = getMongoClient().getTenantGroupElementsCollection().find(match);
			while (found.hasNext()) {
				DBObject current = found.next();
				WriteResult result = MongoPersistence.delete(getMongoClient().getTenantGroupElementsCollection(),
						current);
				if (result.getN() > 0) {
					deleted.add(MongoTenantGroupElement.fromDBObject(current));
				}
			}
		}
		return deleted;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.sitewhere.spi.tenant.ITenantManagement#listTenantGroupElements(java.
	 * lang. String, com.sitewhere.spi.search.ISearchCriteria)
	 */
	@Override
	public ISearchResults<ITenantGroupElement> listTenantGroupElements(String groupId, ISearchCriteria criteria)
			throws SiteWhereException {
		BasicDBObject match = new BasicDBObject(MongoTenantGroupElement.PROP_GROUP_TOKEN, groupId);
		BasicDBObject sort = new BasicDBObject(MongoTenantGroupElement.PROP_TENANT_ID, 1);
		return MongoPersistence.search(ITenantGroupElement.class, getMongoClient().getTenantGroupElementsCollection(),
				match, sort, criteria);
	}

	/**
	 * Get the {@link DBObject} for a tenant given id. Throw an exception if not
	 * found.
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

	/**
	 * Get the {@link DBObject} for a tenant group given token. Throw an
	 * exception if not found.
	 * 
	 * @param token
	 * @return
	 * @throws SiteWhereException
	 */
	protected DBObject assertTenantGroup(String token) throws SiteWhereException {
		DBObject match = getTenantGroupObjectByToken(token);
		if (match == null) {
			throw new SiteWhereSystemException(ErrorCode.InvalidTenantGroupId, ErrorLevel.ERROR,
					HttpServletResponse.SC_NOT_FOUND);
		}
		return match;
	}

	/**
	 * Get the DBObject for a TenantGroup given token.
	 * 
	 * @param token
	 * @return
	 * @throws SiteWhereException
	 */
	protected DBObject getTenantGroupObjectByToken(String token) throws SiteWhereException {
		try {
			DBCollection tgroups = getMongoClient().getTenantGroupsCollection();
			BasicDBObject query = new BasicDBObject(MongoTenantGroup.PROP_TOKEN, token);
			return tgroups.findOne(query);
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
