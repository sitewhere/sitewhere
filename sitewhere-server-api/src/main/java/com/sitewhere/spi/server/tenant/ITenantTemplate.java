package com.sitewhere.spi.server.tenant;

/**
 * Contains information about a template that can be used to populate a new
 * tenant.
 * 
 * @author Derek
 */
public interface ITenantTemplate {

    /**
     * Get unique template id.
     * 
     * @return
     */
    public String getId();

    /**
     * Get display name used in UI.
     * 
     * @return
     */
    public String getName();
}