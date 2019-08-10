/*
 * Copyright (c) SiteWhere, LLC. All rights reserved. http://www.sitewhere.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package com.sitewhere.spi.tenant;

import java.util.UUID;

import com.sitewhere.spi.SiteWhereException;
import com.sitewhere.spi.search.ISearchResults;
import com.sitewhere.spi.search.tenant.ITenantSearchCriteria;
import com.sitewhere.spi.server.lifecycle.ILifecycleComponent;
import com.sitewhere.spi.tenant.request.ITenantCreateRequest;

/**
 * Interface for tenant management operations.
 */
public interface ITenantManagement extends ILifecycleComponent {

    /**
     * Create a new tenant.
     * 
     * @param request
     * @return
     * @throws SiteWhereException
     */
    public ITenant createTenant(ITenantCreateRequest request) throws SiteWhereException;

    /**
     * Update an existing tenant.
     * 
     * @param id
     * @param request
     * @return
     * @throws SiteWhereException
     */
    public ITenant updateTenant(UUID id, ITenantCreateRequest request) throws SiteWhereException;

    /**
     * Get a tenant by tenant id.
     * 
     * @param id
     * @return
     * @throws SiteWhereException
     */
    public ITenant getTenant(UUID id) throws SiteWhereException;

    /**
     * Get tenant by reference token.
     * 
     * @param token
     * @return
     * @throws SiteWhereException
     */
    public ITenant getTenantByToken(String token) throws SiteWhereException;

    /**
     * Find all tenants that match the given criteria.
     * 
     * @param criteria
     * @return
     * @throws SiteWhereException
     */
    public ISearchResults<ITenant> listTenants(ITenantSearchCriteria criteria) throws SiteWhereException;

    /**
     * Delete an existing tenant.
     * 
     * @param tenantId
     * @return
     * @throws SiteWhereException
     */
    public ITenant deleteTenant(UUID tenantId) throws SiteWhereException;
}