/*
 * Copyright (c) SiteWhere, LLC. All rights reserved. http://www.sitewhere.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package com.sitewhere.spi.microservice.multitenant;

import com.sitewhere.spi.server.lifecycle.ITenantEngineLifecycleComponent;

/**
 * Bootstraps tenant engine with data based on configured dataset template.
 */
public interface IDatasetBootstrapManager extends ITenantEngineLifecycleComponent {

    /**
     * Indicates whether tenant engine is bootstrapped.
     * 
     * @return
     */
    public boolean isTenantEngineBootstrapped();
}
