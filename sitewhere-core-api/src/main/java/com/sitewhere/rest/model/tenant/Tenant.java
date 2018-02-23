/*
 * Copyright (c) SiteWhere, LLC. All rights reserved. http://www.sitewhere.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package com.sitewhere.rest.model.tenant;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.sitewhere.rest.model.common.MetadataProviderEntity;
import com.sitewhere.spi.server.ITenantRuntimeState;
import com.sitewhere.spi.tenant.ITenant;

/**
 * Default implementation of {@link ITenant} interface used for REST services.
 * 
 * @author Derek
 */
@JsonInclude(Include.NON_NULL)
public class Tenant extends MetadataProviderEntity implements ITenant, Serializable {

    /** Serial version UUID */
    private static final long serialVersionUID = -353489785570975056L;

    /** Unique tenant id */
    private UUID id;

    /** Reference token */
    private String token;

    /** Tenant name */
    private String name;

    /** Device authentication token */
    private String authenticationToken;

    /** Tenant logo URL */
    private String logoUrl;

    /** List of user ids authorized to access tenant */
    private List<String> authorizedUserIds = new ArrayList<String>();

    /** Tenant template id */
    private String tenantTemplateId;

    /** Runtime state of tenant engine */
    private ITenantRuntimeState engineState;

    /*
     * @see com.sitewhere.spi.common.ISiteWhereEntity#getId()
     */
    @Override
    public UUID getId() {
	return id;
    }

    public void setId(UUID id) {
	this.id = id;
    }

    /*
     * @see com.sitewhere.spi.common.ISiteWhereEntity#getToken()
     */
    @Override
    public String getToken() {
	return token;
    }

    public void setToken(String token) {
	this.token = token;
    }

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
     * @see com.sitewhere.spi.user.ITenant#getLogoUrl()
     */
    @Override
    public String getLogoUrl() {
	return logoUrl;
    }

    public void setLogoUrl(String logoUrl) {
	this.logoUrl = logoUrl;
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
     * (non-Javadoc)
     * 
     * @see com.sitewhere.spi.user.ITenant#getEngineState()
     */
    @Override
    public ITenantRuntimeState getEngineState() {
	return engineState;
    }

    public void setEngineState(ITenantRuntimeState engineState) {
	this.engineState = engineState;
    }
}