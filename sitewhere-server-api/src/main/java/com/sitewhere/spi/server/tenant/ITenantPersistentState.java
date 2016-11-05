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