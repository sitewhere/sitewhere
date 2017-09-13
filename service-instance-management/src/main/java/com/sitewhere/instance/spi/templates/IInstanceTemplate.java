package com.sitewhere.instance.spi.templates;

import java.util.List;

/**
 * Contains information about a template that can be used to populate a new
 * SiteWhere instance.
 * 
 * @author Derek
 */
public interface IInstanceTemplate {

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
	 * Get user management initializer Groovy script locations.
	 * 
	 * @return
	 */
	public List<String> getUserManagement();

	/**
	 * Get tenant management initializer Groovy script locations.
	 * 
	 * @return
	 */
	public List<String> getTenantManagement();
    }
}