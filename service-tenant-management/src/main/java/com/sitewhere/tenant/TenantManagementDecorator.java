/*
 * Copyright (c) SiteWhere, LLC. All rights reserved. http://www.sitewhere.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package com.sitewhere.tenant;

import com.sitewhere.server.lifecycle.LifecycleComponentDecorator;
import com.sitewhere.spi.SiteWhereException;
import com.sitewhere.spi.search.ISearchResults;
import com.sitewhere.spi.search.user.ITenantSearchCriteria;
import com.sitewhere.spi.tenant.ITenant;
import com.sitewhere.spi.tenant.ITenantManagement;
import com.sitewhere.spi.tenant.request.ITenantCreateRequest;

/**
 * Uses decorator pattern to allow behaviors to be injected around tenant
 * management API calls.
 * 
 * @author Derek
 */
public class TenantManagementDecorator extends LifecycleComponentDecorator implements ITenantManagement {

    /** Delegate */
    private ITenantManagement delegate;

    public TenantManagementDecorator(ITenantManagement delegate) {
	super(delegate);
	this.delegate = delegate;
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
	return delegate.createTenant(request);
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
	return delegate.updateTenant(id, request);
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.sitewhere.spi.tenant.ITenantManagement#getTenantById(java.lang.
     * String)
     */
    @Override
    public ITenant getTenantById(String id) throws SiteWhereException {
	return delegate.getTenantById(id);
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
	return delegate.getTenantByAuthenticationToken(token);
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
	return delegate.listTenants(criteria);
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
	return delegate.deleteTenant(tenantId, force);
    }
}