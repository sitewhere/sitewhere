/*
 * Copyright (c) SiteWhere, LLC. All rights reserved. http://www.sitewhere.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package com.sitewhere.rest.model.server;

import com.sitewhere.spi.server.ITenantEngineComponent;
import com.sitewhere.spi.server.lifecycle.LifecycleComponentType;
import com.sitewhere.spi.server.lifecycle.LifecycleStatus;

/**
 * Used for marshaling information about the component hierarchy so that it can
 * be rendered in a tree.
 * 
 * @author Derek
 */
public class TenantEngineComponent implements ITenantEngineComponent {

    /** Serial version UID */
    private static final long serialVersionUID = -5074018616141612387L;

    /** Component id */
    private String id;

    /** Component name */
    private String name;

    /** Component type */
    private LifecycleComponentType type;

    /** Component status */
    private LifecycleStatus status;

    /** Parent component id */
    private String parentId;

    /*
     * (non-Javadoc)
     * 
     * @see com.sitewhere.spi.server.ITenantEngineComponent#getId()
     */
    public String getId() {
	return id;
    }

    public void setId(String id) {
	this.id = id;
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.sitewhere.spi.server.ITenantEngineComponent#getName()
     */
    public String getName() {
	return name;
    }

    public void setName(String name) {
	this.name = name;
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.sitewhere.spi.server.ITenantEngineComponent#getType()
     */
    public LifecycleComponentType getType() {
	return type;
    }

    public void setType(LifecycleComponentType type) {
	this.type = type;
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.sitewhere.spi.server.ITenantEngineComponent#getStatus()
     */
    public LifecycleStatus getStatus() {
	return status;
    }

    public void setStatus(LifecycleStatus status) {
	this.status = status;
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.sitewhere.spi.server.ITenantEngineComponent#getParentId()
     */
    public String getParentId() {
	return parentId;
    }

    public void setParentId(String parentId) {
	this.parentId = parentId;
    }
}