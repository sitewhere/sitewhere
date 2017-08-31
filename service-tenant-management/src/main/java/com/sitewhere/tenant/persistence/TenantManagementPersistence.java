package com.sitewhere.tenant.persistence;

import java.util.List;

import com.sitewhere.SiteWhere;
import com.sitewhere.persistence.Persistence;
import com.sitewhere.rest.model.common.MetadataProvider;
import com.sitewhere.rest.model.tenant.Tenant;
import com.sitewhere.spi.SiteWhereException;
import com.sitewhere.spi.error.ErrorCode;
import com.sitewhere.spi.search.user.ITenantSearchCriteria;
import com.sitewhere.spi.server.tenant.ISiteWhereTenantEngine;
import com.sitewhere.spi.tenant.ITenant;
import com.sitewhere.spi.tenant.request.ITenantCreateRequest;

/**
 * Persistence logic for tenant management components.
 * 
 * @author Derek
 */
public class TenantManagementPersistence extends Persistence {

    /**
     * Common logic for creating a tenant.
     * 
     * @param request
     * @return
     * @throws SiteWhereException
     */
    public static Tenant tenantCreateLogic(ITenantCreateRequest request) throws SiteWhereException {
	Tenant tenant = new Tenant();

	// Id is required.
	requireFormat(request.getId(), "[\\w]*", ErrorCode.TenantIdFormat);
	tenant.setId(request.getId());

	// Name is required.
	require(request.getName());
	tenant.setName(request.getName());

	// Logo is required.
	require(request.getLogoUrl());
	tenant.setLogoUrl(request.getLogoUrl());

	// Auth token is required.
	require(request.getAuthenticationToken());
	tenant.setAuthenticationToken(request.getAuthenticationToken());

	// Tenant template is required.
	require(request.getTenantTemplateId());
	tenant.setTenantTemplateId(request.getTenantTemplateId());

	tenant.getAuthorizedUserIds().addAll(request.getAuthorizedUserIds());

	MetadataProvider.copy(request.getMetadata(), tenant);
	Persistence.initializeEntityMetadata(tenant);

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
	if ((request.getId() != null) && (!request.getId().equals(existing.getId()))) {
	    throw new SiteWhereException("Can not change the id of an existing tenant.");
	}

	if (request.getTenantTemplateId() != null) {
	    if (!request.getTenantTemplateId().equals(existing.getTenantTemplateId())) {
		throw new SiteWhereException("Can not change the template of an existing tenant.");
	    }
	}

	if (request.getName() != null) {
	    existing.setName(request.getName());
	}

	if (request.getLogoUrl() != null) {
	    existing.setLogoUrl(request.getLogoUrl());
	}

	if (request.getAuthenticationToken() != null) {
	    existing.setAuthenticationToken(request.getAuthenticationToken());
	}

	if (request.getAuthorizedUserIds() != null) {
	    existing.getAuthorizedUserIds().clear();
	    existing.getAuthorizedUserIds().addAll(request.getAuthorizedUserIds());
	}

	if (request.getMetadata() != null) {
	    existing.getMetadata().clear();
	    MetadataProvider.copy(request.getMetadata(), existing);
	}
	Persistence.setUpdatedEntityMetadata(existing);

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
	    for (ITenant tenant : results) {
		ISiteWhereTenantEngine engine = SiteWhere.getServer().getTenantEngine(tenant.getId());
		if (engine != null) {
		    ((Tenant) tenant).setEngineState(engine.getEngineState());
		}
	    }
	}
    }
}