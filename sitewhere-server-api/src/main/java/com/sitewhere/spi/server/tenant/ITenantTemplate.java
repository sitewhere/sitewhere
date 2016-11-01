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

    /**
     * Get model initializer information.
     * 
     * @return
     */
    public Initializers getInitializers();

    /**
     * Model initializers information.
     * 
     * @author Derek
     */
    public static interface Initializers {

	/**
	 * Get device management initializer Groovy script location.
	 * 
	 * @return
	 */
	public String getDeviceManagement();
    }
}