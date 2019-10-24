/*
 * Copyright (c) SiteWhere, LLC. All rights reserved. http://www.sitewhere.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package com.sitewhere.rdb.multitenancy;

import org.hibernate.context.spi.CurrentTenantIdentifierResolver;

/**
 * Tenant resolver
 *
 * Simeon Chen
 */
public class TenantIdentifierResolverImpl implements CurrentTenantIdentifierResolver {

	// Default tenant Id
	private static String DEFAULT_TENANT_ID = "tenancy_1";

	@Override
	public String resolveCurrentTenantIdentifier() {
		String currentTenantId = MultiTenantContext.getTenantId();
		return (currentTenantId != null) ? currentTenantId : DEFAULT_TENANT_ID;
	}

	@Override
	public boolean validateExistingCurrentSessions() {
		return true;
	}
}