/*
 * Copyright (c) SiteWhere, LLC. All rights reserved. http://www.sitewhere.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package com.sitewhere.rest.model.server;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.sitewhere.spi.server.ITenantRuntimeState;
import com.sitewhere.spi.server.ITenantEngineComponent;
import com.sitewhere.spi.server.lifecycle.LifecycleStatus;

/**
 * Contains runtime information about a tenant engine.
 * 
 * @author Derek
 */
@JsonInclude(Include.NON_NULL)
public class TenantRuntimeState implements ITenantRuntimeState {

    /** Serial version UID */
    private static final long serialVersionUID = 2078848111680197247L;

    /** Lifecycle status */
    private LifecycleStatus lifecycleStatus;

    /** Hierarchy of tenant engine components */
    private List<ITenantEngineComponent> componentHierarchyState;

    /** Flag indicating whether changes are staged */
    private boolean staged;

    /*
     * (non-Javadoc)
     * 
     * @see com.sitewhere.spi.server.ITenantRuntimeState#getLifecycleStatus()
     */
    public LifecycleStatus getLifecycleStatus() {
	return lifecycleStatus;
    }

    public void setLifecycleStatus(LifecycleStatus lifecycleStatus) {
	this.lifecycleStatus = lifecycleStatus;
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * com.sitewhere.spi.server.ITenantRuntimeState#getComponentHierarchyState()
     */
    public List<ITenantEngineComponent> getComponentHierarchyState() {
	return componentHierarchyState;
    }

    public void setComponentHierarchyState(List<ITenantEngineComponent> componentHierarchyState) {
	this.componentHierarchyState = componentHierarchyState;
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.sitewhere.spi.server.ITenantRuntimeState#isStaged()
     */
    public boolean isStaged() {
	return staged;
    }

    public void setStaged(boolean staged) {
	this.staged = staged;
    }
}