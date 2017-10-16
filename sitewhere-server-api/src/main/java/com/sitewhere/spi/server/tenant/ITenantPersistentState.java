/*
 * Copyright (c) SiteWhere, LLC. All rights reserved. http://www.sitewhere.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package com.sitewhere.spi.server.tenant;

import com.sitewhere.spi.server.lifecycle.LifecycleStatus;

/**
 * Contains information about a tenant that is persisted across tenant restarts.
 * 
 * @author Derek
 */
public interface ITenantPersistentState {

    /**
     * Get desired state for the tenant.
     * 
     * @return
     */
    public LifecycleStatus getDesiredState();

    /**
     * Get last known state for tenant.
     * 
     * @return
     */
    public LifecycleStatus getLastKnownState();
}