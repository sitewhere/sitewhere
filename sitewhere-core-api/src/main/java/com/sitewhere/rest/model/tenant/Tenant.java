/*
 * Copyright (c) SiteWhere, LLC. All rights reserved. http://www.sitewhere.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package com.sitewhere.rest.model.tenant;

import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.sitewhere.rest.model.common.BrandedEntity;
import com.sitewhere.spi.tenant.ITenant;

/**
 * Default implementation of {@link ITenant} interface used for REST services.
 * 
 * @author Derek
 */
@JsonInclude(Include.NON_NULL)
public class Tenant extends BrandedEntity implements ITenant {

    /** Serial version UUID */
    private static final long serialVersionUID = -353489785570975056L;

    /** Tenant name */
    private String name;

    /** Logo URL */
    private String logoUrl;

    /** Device authentication token */
    private String authenticationToken;

    /** List of user ids authorized to access tenant */
    private List<String> authorizedUserIds = new ArrayList<String>();

    /** Tenant template id */
    private String tenantTemplateId;

    /** Dataset template id */
    private String datasetTemplateId;

    /*
     * (non-Javadoc)
     * 
     * @see com.sitewhere.spi.user.ITenant#getName()
     */
    @Override
    public String getName() {
	return name;
    }

    public void setName(String name) {
	this.name = name;
    }

    /*
     * @see com.sitewhere.spi.tenant.ITenant#getLogoUrl()
     */
    public String getLogoUrl() {
	return logoUrl;
    }

    public void setLogoUrl(String logoUrl) {
	this.logoUrl = logoUrl;
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.sitewhere.spi.user.ITenant#getAuthenticationToken()
     */
    @Override
    public String getAuthenticationToken() {
	return authenticationToken;
    }

    public void setAuthenticationToken(String authenticationToken) {
	this.authenticationToken = authenticationToken;
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.sitewhere.spi.user.ITenant#getAuthorizedUserIds()
     */
    @Override
    public List<String> getAuthorizedUserIds() {
	return authorizedUserIds;
    }

    public void setAuthorizedUserIds(List<String> authorizedUserIds) {
	this.authorizedUserIds = authorizedUserIds;
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.sitewhere.spi.tenant.ITenant#getTenantTemplateId()
     */
    @Override
    public String getTenantTemplateId() {
	return tenantTemplateId;
    }

    public void setTenantTemplateId(String tenantTemplateId) {
	this.tenantTemplateId = tenantTemplateId;
    }

    /*
     * @see com.sitewhere.spi.tenant.ITenant#getDatasetTemplateId()
     */
    @Override
    public String getDatasetTemplateId() {
	return datasetTemplateId;
    }

    public void setDatasetTemplateId(String datasetTemplateId) {
	this.datasetTemplateId = datasetTemplateId;
    }
}