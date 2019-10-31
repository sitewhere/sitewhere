/*
 * Copyright (c) SiteWhere, LLC. All rights reserved. http://www.sitewhere.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package com.sitewhere.spi.server.lifecycle;

/**
 * Marker interface for beans that implement
 * {@link ITenantEngineLifecycleComponent} and can be discovered at runtime by
 * introspecting the application context. Allows beans to be registered with the
 * tenant engine without having a well-known name.
 */
public interface IDiscoverableTenantLifecycleComponent extends ITenantEngineLifecycleComponent {

    /**
     * Indicates whether the component is required.
     * 
     * @return
     */
    public boolean isRequired();
}