/*
 * Copyright (c) SiteWhere, LLC. All rights reserved. http://www.sitewhere.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package com.sitewhere.rest.model.tenant.request;

import com.sitewhere.spi.tenant.ITenantGroupElement;
import com.sitewhere.spi.tenant.request.ITenantGroupElementCreateRequest;

/**
 * Model object for attributes needed to create a tenant group element.
 * 
 * @author Derek
 */
public class TenantGroupElementCreateRequest implements ITenantGroupElementCreateRequest {

    /** Serial version UID */
    private static final long serialVersionUID = 1931203730982333819L;

    /** Tenant associated with element */
    private String tenantId;

    /*
     * (non-Javadoc)
     * 
     * @see com.sitewhere.spi.tenant.request.ITenantGroupElementCreateRequest#
     * getTenantId()
     */
    public String getTenantId() {
	return tenantId;
    }

    public void setTenantId(String tenantId) {
	this.tenantId = tenantId;
    }

    public static class Builder {

	/** Request being built */
	private TenantGroupElementCreateRequest request = new TenantGroupElementCreateRequest();

	public Builder(String tenantId) {
	    request.setTenantId(tenantId);
	}

	public Builder(ITenantGroupElement existing) {
	    request.setTenantId(existing.getTenantId());
	}

	public TenantGroupElementCreateRequest build() {
	    return request;
	}
    }
}