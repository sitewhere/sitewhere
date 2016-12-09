/*
 * Copyright (c) SiteWhere, LLC. All rights reserved. http://www.sitewhere.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package com.sitewhere.server.tenant;

import java.util.List;

import com.sitewhere.server.lifecycle.LifecycleComponentDecorator;
import com.sitewhere.spi.SiteWhereException;
import com.sitewhere.spi.search.ISearchCriteria;
import com.sitewhere.spi.search.ISearchResults;
import com.sitewhere.spi.search.user.ITenantSearchCriteria;
import com.sitewhere.spi.tenant.ITenant;
import com.sitewhere.spi.tenant.ITenantGroup;
import com.sitewhere.spi.tenant.ITenantGroupElement;
import com.sitewhere.spi.tenant.ITenantManagement;
import com.sitewhere.spi.tenant.request.ITenantCreateRequest;
import com.sitewhere.spi.tenant.request.ITenantGroupCreateRequest;
import com.sitewhere.spi.tenant.request.ITenantGroupElementCreateRequest;

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

    /*
     * (non-Javadoc)
     * 
     * @see com.sitewhere.spi.tenant.ITenantManagement#createTenantGroup(com.
     * sitewhere.spi. tenant.request.ITenantGroupCreateRequest)
     */
    @Override
    public ITenantGroup createTenantGroup(ITenantGroupCreateRequest request) throws SiteWhereException {
	return delegate.createTenantGroup(request);
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * com.sitewhere.spi.tenant.ITenantManagement#updateTenantGroup(java.lang.
     * String, com.sitewhere.spi.tenant.request.ITenantGroupCreateRequest)
     */
    @Override
    public ITenantGroup updateTenantGroup(String id, ITenantGroupCreateRequest request) throws SiteWhereException {
	return delegate.updateTenantGroup(id, request);
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
	return delegate.getTenantGroupByToken(token);
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
	return delegate.listTenantGroups(criteria);
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * com.sitewhere.spi.tenant.ITenantManagement#deleteTenantGroup(java.lang.
     * String, boolean)
     */
    @Override
    public ITenantGroup deleteTenantGroup(String groupId, boolean force) throws SiteWhereException {
	return delegate.deleteTenantGroup(groupId, force);
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * com.sitewhere.spi.tenant.ITenantManagement#addTenantGroupElements(java.
     * lang.String, java.util.List)
     */
    @Override
    public List<ITenantGroupElement> addTenantGroupElements(String groupId,
	    List<ITenantGroupElementCreateRequest> elements) throws SiteWhereException {
	return delegate.addTenantGroupElements(groupId, elements);
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * com.sitewhere.spi.tenant.ITenantManagement#removeTenantGroupElements(java
     * .lang. String, java.util.List)
     */
    @Override
    public List<ITenantGroupElement> removeTenantGroupElements(String groupId,
	    List<ITenantGroupElementCreateRequest> elements) throws SiteWhereException {
	return delegate.removeTenantGroupElements(groupId, elements);
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
	return delegate.listTenantGroupElements(groupId, criteria);
    }
}