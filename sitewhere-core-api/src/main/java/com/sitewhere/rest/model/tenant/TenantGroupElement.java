/*
 * Copyright (c) SiteWhere, LLC. All rights reserved. http://www.sitewhere.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package com.sitewhere.rest.model.tenant;

import java.io.Serializable;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.sitewhere.spi.tenant.ITenantGroupElement;

/**
 * Model object for an element in a tenant group.
 * 
 * @author Derek
 */
@JsonInclude(Include.NON_NULL)
public class TenantGroupElement implements ITenantGroupElement, Serializable {

	/** Serial version UID */
	private static final long serialVersionUID = -4104618841338632112L;

	/** Tenant group associated with element */
	private String tenantGroupId;

	/** Tenant associated with element */
	private String tenantId;

	/** FIELDS BELOW DEPEND ON MARSHALING PARAMETERS */

	/** Device specification */
	private Tenant tenant;

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.sitewhere.spi.tenant.ITenantGroupElement#getTenantGroupId()
	 */
	public String getTenantGroupId() {
		return tenantGroupId;
	}

	public void setTenantGroupId(String tenantGroupId) {
		this.tenantGroupId = tenantGroupId;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.sitewhere.spi.tenant.ITenantGroupElement#getTenantId()
	 */
	public String getTenantId() {
		return tenantId;
	}

	public void setTenantId(String tenantId) {
		this.tenantId = tenantId;
	}

	public Tenant getTenant() {
		return tenant;
	}

	public void setTenant(Tenant tenant) {
		this.tenant = tenant;
	}
}