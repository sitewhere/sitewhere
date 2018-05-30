/*
 * Copyright (c) SiteWhere, LLC. All rights reserved. http://www.sitewhere.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package com.sitewhere.spi.microservice.multitenant;

/**
 * Management interfaces for Multi-tenant Microservices.
 * 
 * @author Jorge Villaverde
 */
public interface IMultitenantManagement {

    /**
     * Checks whether a tenant engine is available.
     * 
     * @return true if tenant engine is available.
     */
    public boolean checkTenantEngineAvailable();
}
