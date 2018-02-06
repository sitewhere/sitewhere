/*
 * Copyright (c) SiteWhere, LLC. All rights reserved. http://www.sitewhere.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package com.sitewhere.spi.security;

import org.springframework.security.core.Authentication;

import com.sitewhere.spi.tenant.ITenant;

/**
 * Extends Spring {@link Authentication} with ability to carry a tenant token.
 * 
 * @author Derek
 */
public interface ITenantAwareAuthentication extends Authentication {

    /**
     * Get tenant.
     * 
     * @return
     */
    public ITenant getTenant();

    /**
     * Set tenant.
     * 
     * @param tenant
     */
    public void setTenant(ITenant tenant);
}