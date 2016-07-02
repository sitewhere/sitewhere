/*
 * Copyright (c) SiteWhere, LLC. All rights reserved. http://www.sitewhere.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package com.sitewhere.rest.model.tenant.request;

import com.sitewhere.spi.SiteWhereException;
import com.sitewhere.spi.tenant.ITenant;
import com.sitewhere.spi.tenant.ITenantManagement;

/**
 * Builder that supports creating tenant management entities.
 * 
 * @author Derek
 */
public class TenantManagementRequestBuilder {

	/** Device management implementation */
	private ITenantManagement tenantManagement;

	public TenantManagementRequestBuilder(ITenantManagement tenantManagement) {
		this.tenantManagement = tenantManagement;
	}

	/**
	 * Create builder for new tenant request.
	 * 
	 * @param id
	 * @param name
	 * @param authenticationToken
	 * @param logoUrl
	 * @return
	 */
	public TenantCreateRequest.Builder newTenant(String id, String name, String authenticationToken,
			String logoUrl) {
		return new TenantCreateRequest.Builder(id, name, authenticationToken, logoUrl);
	}

	/**
	 * Persist user contructed via builder.
	 * 
	 * @param builder
	 * @return
	 * @throws SiteWhereException
	 */
	public ITenant persist(TenantCreateRequest.Builder builder) throws SiteWhereException {
		return getTenantManagement().createTenant(builder.build());
	}

	/**
	 * Get tenant by unique id.
	 * 
	 * @param id
	 * @return
	 * @throws SiteWhereException
	 */
	public ITenant getTenant(String id) throws SiteWhereException {
		return getTenantManagement().getTenantById(id);
	}

	/**
	 * Indicates whether a tenant exists for the given id.
	 * 
	 * @param id
	 * @return
	 * @throws SiteWhereException
	 */
	public boolean hasTenant(String id) throws SiteWhereException {
		return getTenant(id) != null;
	}

	public ITenantManagement getTenantManagement() {
		return tenantManagement;
	}

	public void setTenantManagement(ITenantManagement tenantManagement) {
		this.tenantManagement = tenantManagement;
	}
}