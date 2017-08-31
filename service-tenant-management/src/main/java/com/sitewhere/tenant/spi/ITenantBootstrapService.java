package com.sitewhere.tenant.spi;

import com.sitewhere.spi.server.lifecycle.ITenantLifecycleComponent;

/**
 * Service that bootstraps a newly created tenant with sample data included in
 * the tenant template.
 * 
 * @author Derek
 */
public interface ITenantBootstrapService extends ITenantLifecycleComponent {
}