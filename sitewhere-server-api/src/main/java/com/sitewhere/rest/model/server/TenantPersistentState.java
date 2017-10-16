/*
 * Copyright (c) SiteWhere, LLC. All rights reserved. http://www.sitewhere.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package com.sitewhere.rest.model.server;

import com.sitewhere.spi.server.lifecycle.LifecycleStatus;
import com.sitewhere.spi.server.tenant.ITenantPersistentState;

/**
 * Model implementation for {@link ITenantPersistentState}.
 * 
 * @author Derek
 */
public class TenantPersistentState implements ITenantPersistentState {

    /** Desired state for tenant engine */
    private LifecycleStatus desiredState;

    /** Last known state for tenant engine */
    private LifecycleStatus lastKnownState;

    /*
     * (non-Javadoc)
     * 
     * @see
     * com.sitewhere.spi.server.tenant.ITenantPersistentState#getDesiredState()
     */
    public LifecycleStatus getDesiredState() {
	return desiredState;
    }

    public void setDesiredState(LifecycleStatus desiredState) {
	this.desiredState = desiredState;
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * com.sitewhere.spi.server.tenant.ITenantPersistentState#getLastKnownState(
     * )
     */
    public LifecycleStatus getLastKnownState() {
	return lastKnownState;
    }

    public void setLastKnownState(LifecycleStatus lastKnownState) {
	this.lastKnownState = lastKnownState;
    }

    /**
     * Copy from API object.
     * 
     * @param api
     * @return
     */
    public static TenantPersistentState copy(ITenantPersistentState api) {
	TenantPersistentState state = new TenantPersistentState();
	state.setDesiredState(api.getDesiredState());
	state.setLastKnownState(api.getLastKnownState());
	return state;
    }
}