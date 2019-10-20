/*
 * Copyright (c) SiteWhere, LLC. All rights reserved. http://www.sitewhere.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package com.sitewhere.microservice.tenant.persistence;

import java.util.List;

import com.sitewhere.persistence.Persistence;
import com.sitewhere.rest.model.tenant.Tenant;
import com.sitewhere.spi.SiteWhereException;
import com.sitewhere.spi.error.ErrorCode;
import com.sitewhere.spi.search.tenant.ITenantSearchCriteria;
import com.sitewhere.spi.tenant.ITenant;
import com.sitewhere.spi.tenant.request.ITenantCreateRequest;

/**
 * Persistence logic for tenant management components.
 * 
 * @author Derek
 */
public class TenantManagementPersistenceLogic extends Persistence {

    /**
     * Common logic for creating a tenant.
     * 
     * @param request
     * @return
     * @throws SiteWhereException
     */
    public static Tenant tenantCreateLogic(ITenantCreateRequest request) throws SiteWhereException {
	Tenant tenant = new Tenant();

	// Validate tenant token.
	requireFormat("Token", tenant.getToken(), "^[\\w-]+$", ErrorCode.TenantIdFormat);

	// Name is required.
	require("Name", request.getName());
	tenant.setName(request.getName());

	// Auth token is required.
	require("Auth Token", request.getAuthenticationToken());
	tenant.setAuthenticationToken(request.getAuthenticationToken());

	// Tenant template is required.
	require("Configuration template Id", request.getConfigurationTemplateId());
	tenant.setConfigurationTemplateId(request.getConfigurationTemplateId());

	// Tenant template is required.
	require("Dataset template Id", request.getDatasetTemplateId());
	tenant.setDatasetTemplateId(request.getDatasetTemplateId());

	tenant.getAuthorizedUserIds().addAll(request.getAuthorizedUserIds());

	return tenant;
    }

    /**
     * Common logic for updating an existing tenant.
     * 
     * @param request
     * @param existing
     * @return
     * @throws SiteWhereException
     */
    public static Tenant tenantUpdateLogic(ITenantCreateRequest request, Tenant existing) throws SiteWhereException {
	if (request.getConfigurationTemplateId() != null) {
	    if (!request.getConfigurationTemplateId().equals(existing.getConfigurationTemplateId())) {
		throw new SiteWhereException("Can not change the tenant template of an existing tenant.");
	    }
	}
	if (request.getDatasetTemplateId() != null) {
	    if (!request.getDatasetTemplateId().equals(existing.getDatasetTemplateId())) {
		throw new SiteWhereException("Can not change the dataset template of an existing tenant.");
	    }
	}

	if (request.getName() != null) {
	    existing.setName(request.getName());
	}

	if (request.getAuthenticationToken() != null) {
	    existing.setAuthenticationToken(request.getAuthenticationToken());
	}

	if (request.getAuthorizedUserIds() != null) {
	    existing.getAuthorizedUserIds().clear();
	    existing.getAuthorizedUserIds().addAll(request.getAuthorizedUserIds());
	}

	return existing;
    }

    /**
     * Common tenant list logic.
     * 
     * @param results
     * @param criteria
     * @throws SiteWhereException
     */
    public static void tenantListLogic(List<ITenant> results, ITenantSearchCriteria criteria)
	    throws SiteWhereException {
	if (criteria.isIncludeRuntimeInfo()) {
	    // for (ITenant tenant : results) {
	    // ISiteWhereTenantEngine engine =
	    // SiteWhere.getServer().getTenantEngine(tenant.getId());
	    // if (engine != null) {
	    // ((Tenant) tenant).setEngineState(engine.getEngineState());
	    // }
	    // }
	}
    }
}