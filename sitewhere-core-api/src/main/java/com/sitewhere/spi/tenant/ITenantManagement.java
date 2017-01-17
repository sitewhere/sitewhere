/*
 * Copyright (c) SiteWhere, LLC. All rights reserved. http://www.sitewhere.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package com.sitewhere.spi.tenant;

import com.sitewhere.spi.SiteWhereException;
import com.sitewhere.spi.search.ISearchResults;
import com.sitewhere.spi.search.user.ITenantSearchCriteria;
import com.sitewhere.spi.server.lifecycle.ILifecycleComponent;
import com.sitewhere.spi.tenant.request.ITenantCreateRequest;

/**
 * Interface for tenant management operations.
 * 
 * @author Derek
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
    public ITenant updateTenant(String id, ITenantCreateRequest request) throws SiteWhereException;

    /**
     * Get a tenant by tenant id.
     * 
     * @param id
     * @return
     * @throws SiteWhereException
     */
    public ITenant getTenantById(String id) throws SiteWhereException;

    /**
     * Get a tenant by authentication token sent by devices.
     * 
     * @param token
     * @return
     * @throws SiteWhereException
     */
    public ITenant getTenantByAuthenticationToken(String token) throws SiteWhereException;

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
     * @param force
     * @return
     * @throws SiteWhereException
     */
    public ITenant deleteTenant(String tenantId, boolean force) throws SiteWhereException;
}