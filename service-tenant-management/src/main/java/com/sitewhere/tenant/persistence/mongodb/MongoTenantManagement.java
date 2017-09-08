/*
 * Copyright (c) SiteWhere, LLC. All rights reserved. http://www.sitewhere.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package com.sitewhere.tenant.persistence.mongodb;

import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.bson.Document;

import com.mongodb.BasicDBList;
import com.mongodb.BasicDBObject;
import com.mongodb.DBObject;
import com.mongodb.MongoTimeoutException;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.model.IndexOptions;
import com.sitewhere.mongodb.IMongoConverterLookup;
import com.sitewhere.mongodb.MongoPersistence;
import com.sitewhere.mongodb.common.MongoSiteWhereEntity;
import com.sitewhere.rest.model.tenant.Tenant;
import com.sitewhere.server.lifecycle.LifecycleComponent;
import com.sitewhere.spi.SiteWhereException;
import com.sitewhere.spi.SiteWhereSystemException;
import com.sitewhere.spi.error.ErrorCode;
import com.sitewhere.spi.error.ErrorLevel;
import com.sitewhere.spi.search.ISearchResults;
import com.sitewhere.spi.search.user.ITenantSearchCriteria;
import com.sitewhere.spi.server.lifecycle.ILifecycleProgressMonitor;
import com.sitewhere.spi.server.lifecycle.LifecycleComponentType;
import com.sitewhere.spi.tenant.ITenant;
import com.sitewhere.spi.tenant.ITenantManagement;
import com.sitewhere.spi.tenant.request.ITenantCreateRequest;
import com.sitewhere.tenant.persistence.TenantManagementPersistenceLogic;

/**
 * Tenant management implementation that uses MongoDB for persistence.
 * 
 * @author dadams
 */
public class MongoTenantManagement extends LifecycleComponent implements ITenantManagement {

    /** Static logger instance */
    private static Logger LOGGER = LogManager.getLogger();

    /** Converter lookup */
    private static IMongoConverterLookup LOOKUP = new MongoConverters();

    /** Injected with global SiteWhere Mongo client */
    private ITenantManagementMongoClient mongoClient;

    public MongoTenantManagement() {
	super(LifecycleComponentType.DataStore);
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * com.sitewhere.server.lifecycle.LifecycleComponent#start(com.sitewhere.spi
     * .server.lifecycle.ILifecycleProgressMonitor)
     */
    public void start(ILifecycleProgressMonitor monitor) throws SiteWhereException {
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
	getMongoClient().getTenantsCollection().createIndex(new Document(MongoTenant.PROP_ID, 1),
		new IndexOptions().unique(true));
	getMongoClient().getTenantsCollection().createIndex(new Document(MongoTenant.PROP_AUTH_TOKEN, 1),
		new IndexOptions().unique(true));
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
	// Use common logic so all backend implementations work the same.
	Tenant tenant = TenantManagementPersistenceLogic.tenantCreateLogic(request);

	MongoCollection<Document> tenants = getMongoClient().getTenantsCollection();
	Document created = MongoTenant.toDocument(tenant);
	MongoPersistence.insert(tenants, created, ErrorCode.DuplicateTenantId);

	return MongoTenant.fromDocument(created);
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
	Document dbExisting = assertTenant(id);
	Tenant existing = MongoTenant.fromDocument(dbExisting);

	// Common update logic so that backend implemetations act the same way.
	TenantManagementPersistenceLogic.tenantUpdateLogic(request, existing);
	Document updated = MongoTenant.toDocument(existing);

	Document query = new Document(MongoTenant.PROP_ID, id);
	MongoCollection<Document> tenants = getMongoClient().getTenantsCollection();
	MongoPersistence.update(tenants, query, updated);

	return MongoTenant.fromDocument(updated);
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.sitewhere.spi.tenant.ITenantManagement#getTenantById(java.lang.
     * String)
     */
    @Override
    public ITenant getTenantById(String id) throws SiteWhereException {
	Document dbExisting = getTenantDocumentById(id);
	if (dbExisting == null) {
	    return null;
	}
	return MongoTenant.fromDocument(dbExisting);
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
	    MongoCollection<Document> tenants = getMongoClient().getTenantsCollection();
	    Document query = new Document(MongoTenant.PROP_AUTH_TOKEN, token);
	    Document match = tenants.find(query).first();
	    if (match == null) {
		return null;
	    }
	    return MongoTenant.fromDocument(match);
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
	MongoCollection<Document> tenants = getMongoClient().getTenantsCollection();
	Document dbCriteria = new Document();
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
	Document sort = new Document(MongoTenant.PROP_NAME, 1);
	ISearchResults<ITenant> list = MongoPersistence.search(ITenant.class, tenants, dbCriteria, sort, criteria,
		LOOKUP);
	TenantManagementPersistenceLogic.tenantListLogic(list.getResults(), criteria);
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
	Document existing = assertTenant(tenantId);
	if (force) {
	    MongoCollection<Document> tenants = getMongoClient().getTenantsCollection();
	    MongoPersistence.delete(tenants, existing);
	    getMongoClient().deleteTenantData(tenantId);
	    return MongoTenant.fromDocument(existing);
	} else {
	    MongoSiteWhereEntity.setDeleted(existing, true);
	    Document query = new Document(MongoTenant.PROP_ID, tenantId);
	    MongoCollection<Document> tenants = getMongoClient().getTenantsCollection();
	    MongoPersistence.update(tenants, query, existing);
	    return MongoTenant.fromDocument(existing);
	}
    }

    /**
     * Get the {@link Document} for a tenant given id. Throw an exception if not
     * found.
     * 
     * @param id
     * @return
     * @throws SiteWhereException
     */
    protected Document assertTenant(String id) throws SiteWhereException {
	Document match = getTenantDocumentById(id);
	if (match == null) {
	    throw new SiteWhereSystemException(ErrorCode.InvalidTenantId, ErrorLevel.ERROR);
	}
	return match;
    }

    /**
     * Get the {@link Document} for a Tenant given unique id.
     * 
     * @param id
     * @return
     * @throws SiteWhereException
     */
    protected Document getTenantDocumentById(String id) throws SiteWhereException {
	try {
	    MongoCollection<Document> tenants = getMongoClient().getTenantsCollection();
	    Document query = new Document(MongoTenant.PROP_ID, id);
	    return tenants.find(query).first();
	} catch (MongoTimeoutException e) {
	    throw new SiteWhereException("Connection to MongoDB lost.", e);
	}
    }

    public ITenantManagementMongoClient getMongoClient() {
	return mongoClient;
    }

    public void setMongoClient(ITenantManagementMongoClient mongoClient) {
	this.mongoClient = mongoClient;
    }
}
