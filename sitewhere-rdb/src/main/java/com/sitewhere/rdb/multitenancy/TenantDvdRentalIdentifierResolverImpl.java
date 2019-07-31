package com.sitewhere.rdb.multitenancy;

import org.hibernate.context.spi.CurrentTenantIdentifierResolver;

/**
 * Tenant resolver
 *
 * Simeon Chen
 */
public class TenantDvdRentalIdentifierResolverImpl implements CurrentTenantIdentifierResolver {

	// Default tenant Id
	private static String DEFAULT_TENANT_ID = "tenancy_1";

	@Override
	public String resolveCurrentTenantIdentifier() {
		String currentTenantId = DvdRentalTenantContext.getTenantId();
		return (currentTenantId != null) ? currentTenantId : DEFAULT_TENANT_ID;
	}

	@Override
	public boolean validateExistingCurrentSessions() {
		return true;
	}
}