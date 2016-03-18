/*
 * Copyright (c) SiteWhere, LLC. All rights reserved. http://www.sitewhere.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package com.sitewhere.server.tenant;

import com.sitewhere.SiteWhere;
import com.sitewhere.spi.SiteWhereException;
import com.sitewhere.spi.tenant.ITenant;
import com.sitewhere.spi.tenant.ITenantManagement;
import com.sitewhere.spi.tenant.request.ITenantCreateRequest;

/**
 * System behaviors that are triggered by calls to the tenant management APIs.
 * 
 * @author Derek
 */
public class TenantManagementTriggers extends TenantManagementDecorator {

	public TenantManagementTriggers(ITenantManagement delegate) {
		super(delegate);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.sitewhere.server.tenant.TenantManagementDecorator#updateTenant(java.lang.
	 * String, com.sitewhere.spi.tenant.request.ITenantCreateRequest)
	 */
	@Override
	public ITenant updateTenant(String id, ITenantCreateRequest request) throws SiteWhereException {
		ITenant updated = super.updateTenant(id, request);
		SiteWhere.getServer().onTenantInformationUpdated(updated);
		return updated;
	}
}