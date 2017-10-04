package com.sitewhere.microservice.spi.multitenant;

import com.sitewhere.spi.server.lifecycle.ITenantLifecycleComponent;

/**
 * Engine that manages operations for a single tenant within an
 * {@link IMultitenantMicroservice}.
 * 
 * @author Derek
 */
public interface IMicroserviceTenantEngine extends ITenantLifecycleComponent {
}