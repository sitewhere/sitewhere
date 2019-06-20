/*
 * Copyright (c) SiteWhere, LLC. All rights reserved. http://www.sitewhere.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package com.sitewhere.spi.microservice.multitenant;

/**
 * Contains information about a template that can be used to configure a new
 * tenant.
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
     * Get template description.
     * 
     * @return
     */
    public String getDescription();
}