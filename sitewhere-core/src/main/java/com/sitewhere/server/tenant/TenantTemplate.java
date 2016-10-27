package com.sitewhere.server.tenant;

import com.sitewhere.spi.server.tenant.ITenantTemplate;

/**
 * Model object for an {@link ITenantTemplate}.
 * 
 * @author Derek
 */
public class TenantTemplate implements ITenantTemplate {

    /** Template id */
    private String id;

    /** Template name */
    private String name;

    /*
     * (non-Javadoc)
     * 
     * @see com.sitewhere.spi.server.tenant.ITenantTemplate#getId()
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
     * @see com.sitewhere.spi.server.tenant.ITenantTemplate#getName()
     */
    public String getName() {
	return name;
    }

    public void setName(String name) {
	this.name = name;
    }
}