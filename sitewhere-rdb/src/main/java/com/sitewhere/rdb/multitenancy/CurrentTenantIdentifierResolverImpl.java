package com.sitewhere.rdb.multitenancy;

import org.hibernate.context.spi.CurrentTenantIdentifierResolver;
import org.springframework.stereotype.Component;

/**
 * CurrentTenantIdentifierResolver implementation
 *
 * Simeon Chen
 */
@Component
public class CurrentTenantIdentifierResolverImpl implements CurrentTenantIdentifierResolver {
    @Override
    public String resolveCurrentTenantIdentifier() {
        if (TenantContext.getCurrentTenant() != null) {
            return TenantContext.getCurrentTenant();
        }
        return "tenancy_1";
    }

    @Override
    public boolean validateExistingCurrentSessions() {
        return false;
    }
}
