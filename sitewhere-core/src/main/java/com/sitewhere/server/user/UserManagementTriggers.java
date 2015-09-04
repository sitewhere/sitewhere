/*
 * Copyright (c) SiteWhere, LLC. All rights reserved. http://www.sitewhere.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package com.sitewhere.server.user;

import com.sitewhere.SiteWhere;
import com.sitewhere.spi.SiteWhereException;
import com.sitewhere.spi.user.ITenant;
import com.sitewhere.spi.user.IUserManagement;
import com.sitewhere.spi.user.request.ITenantCreateRequest;

/**
 * System behaviors that are triggered by calls to the user management APIs.
 * 
 * @author Derek
 */
public class UserManagementTriggers extends UserManagementDecorator {

	public UserManagementTriggers(IUserManagement delegate) {
		super(delegate);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.sitewhere.server.user.UserManagementDecorator#createTenant(com.sitewhere.spi
	 * .user.request.ITenantCreateRequest)
	 */
	@Override
	public ITenant createTenant(ITenantCreateRequest request) throws SiteWhereException {
		return super.createTenant(request);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.sitewhere.server.user.UserManagementDecorator#updateTenant(java.lang.String,
	 * com.sitewhere.spi.user.request.ITenantCreateRequest)
	 */
	@Override
	public ITenant updateTenant(String id, ITenantCreateRequest request) throws SiteWhereException {
		ITenant updated = super.updateTenant(id, request);
		SiteWhere.getServer().onTenantInformationUpdated(updated);
		return updated;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.sitewhere.server.user.UserManagementDecorator#deleteTenant(java.lang.String,
	 * boolean)
	 */
	@Override
	public ITenant deleteTenant(String tenantId, boolean force) throws SiteWhereException {
		return super.deleteTenant(tenantId, force);
	}
}