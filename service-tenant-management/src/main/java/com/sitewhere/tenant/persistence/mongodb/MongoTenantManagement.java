/*
 * Copyright (c) SiteWhere, LLC. All rights reserved. http://www.sitewhere.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package com.sitewhere.tenant.persistence.mongodb;

import java.util.UUID;
import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;

import org.bson.Document;

import com.mongodb.BasicDBList;
import com.mongodb.BasicDBObject;
import com.mongodb.DBObject;
import com.mongodb.MongoClientException;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.model.IndexOptions;
import com.sitewhere.mongodb.IMongoConverterLookup;
import com.sitewhere.mongodb.MongoPersistence;
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

    /**
     * Ensure that expected collection indexes exist.
     * 
     * @throws SiteWhereException
     */
    protected void ensureIndexes() throws SiteWhereException {
	getMongoClient().getTenantsCollection().createIndex(new Document(MongoTenant.PROP_TOKEN, 1),
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
     * @see com.sitewhere.spi.tenant.ITenantManagement#updateTenant(java.util.UUID,
     * com.sitewhere.spi.tenant.request.ITenantCreateRequest)
     */
    @Override
    public ITenant updateTenant(UUID id, ITenantCreateRequest request) throws SiteWhereException {
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
     * @see com.sitewhere.spi.tenant.ITenantManagement#getTenant(java.util.UUID)
     */
    @Override
    public ITenant getTenant(UUID id) throws SiteWhereException {
	Document dbExisting = getTenantDocumentById(id);
	if (dbExisting == null) {
	    return null;
	}
	return MongoTenant.fromDocument(dbExisting);
    }

    /*
     * @see
     * com.sitewhere.spi.tenant.ITenantManagement#getTenantByToken(java.lang.String)
     */
    @Override
    public ITenant getTenantByToken(String token) throws SiteWhereException {
	try {
	    MongoCollection<Document> tenants = getMongoClient().getTenantsCollection();
	    Document query = new Document(MongoTenant.PROP_TOKEN, token);
	    Document dbTenant = tenants.find(query).first();
	    if (dbTenant != null) {
		return MongoTenant.fromDocument(dbTenant);
	    }
	    return null;
	} catch (MongoClientException e) {
	    throw MongoPersistence.handleClientException(e);
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
		getLogger().warn("Invalid regex for searching tenant list. Ignoring.");
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
     * @see com.sitewhere.spi.tenant.ITenantManagement#deleteTenant(java.util.UUID)
     */
    @Override
    public ITenant deleteTenant(UUID tenantId) throws SiteWhereException {
	Document existing = assertTenant(tenantId);
	MongoCollection<Document> tenants = getMongoClient().getTenantsCollection();
	MongoPersistence.delete(tenants, existing);
	return MongoTenant.fromDocument(existing);
    }

    /**
     * Get the {@link Document} for a tenant given id. Throw an exception if not
     * found.
     * 
     * @param id
     * @return
     * @throws SiteWhereException
     */
    protected Document assertTenant(UUID id) throws SiteWhereException {
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
    protected Document getTenantDocumentById(UUID id) throws SiteWhereException {
	try {
	    MongoCollection<Document> tenants = getMongoClient().getTenantsCollection();
	    Document query = new Document(MongoTenant.PROP_ID, id);
	    return tenants.find(query).first();
	} catch (MongoClientException e) {
	    throw MongoPersistence.handleClientException(e);
	}
    }

    public ITenantManagementMongoClient getMongoClient() {
	return mongoClient;
    }

    public void setMongoClient(ITenantManagementMongoClient mongoClient) {
	this.mongoClient = mongoClient;
    }
}
