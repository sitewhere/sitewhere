/*
 * Copyright (c) SiteWhere, LLC. All rights reserved. http://www.sitewhere.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
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