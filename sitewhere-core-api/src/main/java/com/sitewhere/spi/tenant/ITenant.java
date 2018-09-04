/*
 * Copyright (c) SiteWhere, LLC. All rights reserved. http://www.sitewhere.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package com.sitewhere.spi.tenant;

import java.util.List;

import com.sitewhere.spi.common.IBrandedEntity;

/**
 * Interface for information about a tenant.
 * 
 * @author Derek
 */
public interface ITenant extends IBrandedEntity {

    /**
     * Get tenant name.
     * 
     * @return
     */
    public String getName();

    /**
     * Get token that devices pass to identify tenant.
     * 
     * @return
     */
    public String getAuthenticationToken();

    /**
     * Get list of users authorized to access the tenant.
     * 
     * @return
     */
    public List<String> getAuthorizedUserIds();

    /**
     * Get id of template used to create tenant.
     * 
     * @return
     */
    public String getTenantTemplateId();

    /**
     * Get id of dataset template used to populate tenant.
     * 
     * @return
     */
    public String getDatasetTemplateId();
}