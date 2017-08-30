/*
 * Copyright (c) SiteWhere, LLC. All rights reserved. http://www.sitewhere.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package com.sitewhere.tenantmanagement.mongodb;

import java.util.List;

import org.bson.Document;

import com.sitewhere.mongodb.MongoConverter;
import com.sitewhere.mongodb.common.MongoMetadataProvider;
import com.sitewhere.mongodb.common.MongoSiteWhereEntity;
import com.sitewhere.rest.model.tenant.Tenant;
import com.sitewhere.spi.tenant.ITenant;

/**
 * Provides methods for loading and storing {@link Tenant} data in MongoDB.
 * 
 * @author Derek
 */
public class MongoTenant implements MongoConverter<ITenant> {

    /** Property for tenant id */
    public static final String PROP_ID = "id";

    /** Property for tenant name */
    public static final String PROP_NAME = "name";

    /** Property for authentication token */
    public static final String PROP_AUTH_TOKEN = "auth";

    /** Property for logo URL */
    public static final String PROP_LOGO_URL = "logo";

    /** Property for authorized users */
    public static final String PROP_AUTH_USERS = "users";

    /** Property for authorized users */
    public static final String PROP_TEMPLATE_ID = "templateId";

    /*
     * (non-Javadoc)
     * 
     * @see com.sitewhere.mongodb.MongoConverter#convert(java.lang.Object)
     */
    @Override
    public Document convert(ITenant source) {
	return MongoTenant.toDocument(source);
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.sitewhere.mongodb.MongoConverter#convert(org.bson.Document)
     */
    @Override
    public ITenant convert(Document source) {
	return MongoTenant.fromDocument(source);
    }

    /**
     * Copy information from SPI into Mongo {@link Document}.
     * 
     * @param source
     * @param target
     */
    public static void toDocument(ITenant source, Document target) {
	target.append(PROP_ID, source.getId());
	target.append(PROP_NAME, source.getName());
	target.append(PROP_AUTH_TOKEN, source.getAuthenticationToken());
	target.append(PROP_LOGO_URL, source.getLogoUrl());
	target.append(PROP_AUTH_USERS, source.getAuthorizedUserIds());
	target.append(PROP_TEMPLATE_ID, source.getTenantTemplateId());

	MongoSiteWhereEntity.toDocument(source, target);
	MongoMetadataProvider.toDocument(source, target);
    }

    /**
     * Copy information from Mongo {@link Document} to model object.
     * 
     * @param source
     * @param target
     */
    @SuppressWarnings("unchecked")
    public static void fromDocument(Document source, Tenant target) {
	String id = (String) source.get(PROP_ID);
	String name = (String) source.get(PROP_NAME);
	String authToken = (String) source.get(PROP_AUTH_TOKEN);
	String logo = (String) source.get(PROP_LOGO_URL);
	List<String> authUsers = (List<String>) source.get(PROP_AUTH_USERS);
	String templateId = (String) source.get(PROP_TEMPLATE_ID);

	target.setId(id);
	target.setName(name);
	target.setAuthenticationToken(authToken);
	target.setLogoUrl(logo);
	target.setAuthorizedUserIds(authUsers);
	target.setTenantTemplateId(templateId);

	MongoSiteWhereEntity.fromDocument(source, target);
	MongoMetadataProvider.fromDocument(source, target);
    }

    /**
     * Convert SPI object to Mongo {@link Document}.
     * 
     * @param source
     * @return
     */
    public static Document toDocument(ITenant source) {
	Document result = new Document();
	MongoTenant.toDocument(source, result);
	return result;
    }

    /**
     * Convert a {@link Document} into the SPI equivalent.
     * 
     * @param source
     * @return
     */
    public static Tenant fromDocument(Document source) {
	Tenant result = new Tenant();
	MongoTenant.fromDocument(source, result);
	return result;
    }
}