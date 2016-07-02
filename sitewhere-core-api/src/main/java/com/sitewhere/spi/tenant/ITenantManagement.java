/*
 * Copyright (c) SiteWhere, LLC. All rights reserved. http://www.sitewhere.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package com.sitewhere.spi.tenant;

import java.util.List;

import com.sitewhere.spi.SiteWhereException;
import com.sitewhere.spi.search.ISearchCriteria;
import com.sitewhere.spi.search.ISearchResults;
import com.sitewhere.spi.search.user.ITenantSearchCriteria;
import com.sitewhere.spi.server.lifecycle.ILifecycleComponent;
import com.sitewhere.spi.tenant.request.ITenantCreateRequest;
import com.sitewhere.spi.tenant.request.ITenantGroupCreateRequest;
import com.sitewhere.spi.tenant.request.ITenantGroupElementCreateRequest;

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

	/**
	 * Create a new tenant group.
	 * 
	 * @param request
	 * @return
	 * @throws SiteWhereException
	 */
	public ITenantGroup createTenantGroup(ITenantGroupCreateRequest request) throws SiteWhereException;

	/**
	 * Update an existing tenant group.
	 * 
	 * @param id
	 * @param request
	 * @return
	 * @throws SiteWhereException
	 */
	public ITenantGroup updateTenantGroup(String id, ITenantGroupCreateRequest request)
			throws SiteWhereException;

	/**
	 * Get a tenant group by unique id.
	 * 
	 * @param id
	 * @return
	 * @throws SiteWhereException
	 */
	public ITenantGroup getTenantGroupById(String id) throws SiteWhereException;

	/**
	 * List tenant groups that meet the given criteria.
	 * 
	 * @param criteria
	 * @return
	 * @throws SiteWhereException
	 */
	public ISearchResults<ITenantGroup> listTenantGroups(ISearchCriteria criteria) throws SiteWhereException;

	/**
	 * Delete an existing tenant group based on its unique id.
	 * 
	 * @param groupId
	 * @param force
	 * @return
	 * @throws SiteWhereException
	 */
	public ITenantGroup deleteTenantGroup(String groupId, boolean force) throws SiteWhereException;

	/**
	 * Add elements to a tenant group.
	 * 
	 * @param groupId
	 * @param elements
	 * @return
	 * @throws SiteWhereException
	 */
	public List<ITenantGroupElement> addTenantGroupElements(String groupId,
			List<ITenantGroupElementCreateRequest> elements) throws SiteWhereException;

	/**
	 * Remove elements from a tenant group.
	 * 
	 * @param groupToken
	 * @param elements
	 * @return
	 * @throws SiteWhereException
	 */
	public List<ITenantGroupElement> removeTenantGroupElements(String groupId,
			List<ITenantGroupElementCreateRequest> elements) throws SiteWhereException;

	/**
	 * List elements in a tenant group.
	 * 
	 * @param groupToken
	 * @param criteria
	 * @return
	 * @throws SiteWhereException
	 */
	public ISearchResults<ITenantGroupElement> listTenantGroupElements(String groupId,
			ISearchCriteria criteria) throws SiteWhereException;

}