/*
 * Copyright (c) SiteWhere, LLC. All rights reserved. http://www.sitewhere.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package com.sitewhere.rest.model.tenant.request;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.sitewhere.rest.model.common.MetadataProvider;
import com.sitewhere.spi.tenant.ITenant;
import com.sitewhere.spi.tenant.request.ITenantCreateRequest;

/**
 * Default implementation of {@link ITenantCreateRequest} for use in REST
 * services.
 * 
 * @author Derek
 */
@JsonInclude(Include.NON_NULL)
public class TenantCreateRequest extends MetadataProvider implements ITenantCreateRequest {

    /** Serial version UID */
    private static final long serialVersionUID = -5706275554835627264L;

    /** Unique tenant id */
    private String id;

    /** Tenant name */
    private String name;

    /** Device authentication token */
    private String authenticationToken;

    /** Tenant logo URL */
    private String logoUrl;

    /** List of users authorized for access */
    private List<String> authorizedUserIds;

    /** Tenant template id */
    private String tenantTemplateId;

    /*
     * (non-Javadoc)
     * 
     * @see com.sitewhere.spi.user.request.ITenantCreateRequest#getId()
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
     * @see com.sitewhere.spi.user.request.ITenantCreateRequest#getName()
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
     * @see com.sitewhere.spi.user.request.ITenantCreateRequest#
     * getAuthenticationToken()
     */
    public String getAuthenticationToken() {
	return authenticationToken;
    }

    public void setAuthenticationToken(String authenticationToken) {
	this.authenticationToken = authenticationToken;
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.sitewhere.spi.user.request.ITenantCreateRequest#getLogoUrl()
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
     * @see
     * com.sitewhere.spi.user.request.ITenantCreateRequest#getAuthorizedUserIds(
     * )
     */
    public List<String> getAuthorizedUserIds() {
	return authorizedUserIds;
    }

    public void setAuthorizedUserIds(List<String> authorizedUserIds) {
	this.authorizedUserIds = authorizedUserIds;
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * com.sitewhere.spi.tenant.request.ITenantCreateRequest#getTenantTemplateId
     * ()
     */
    public String getTenantTemplateId() {
	return tenantTemplateId;
    }

    public void setTenantTemplateId(String tenantTemplateId) {
	this.tenantTemplateId = tenantTemplateId;
    }

    public static class Builder {

	/** Request being built */
	private TenantCreateRequest request = new TenantCreateRequest();

	public Builder(String id, String name, String authenticationToken, String logoUrl, String tenantTemplateId) {
	    request.setId(id);
	    request.setName(name);
	    request.setAuthenticationToken(authenticationToken);
	    request.setLogoUrl(logoUrl);
	    request.setTenantTemplateId(tenantTemplateId);
	}

	public Builder(ITenant existing) {
	    request.setId(existing.getId());
	    request.setName(existing.getName());
	    request.setLogoUrl(existing.getLogoUrl());
	    request.setAuthenticationToken(existing.getAuthenticationToken());
	    request.setAuthorizedUserIds(existing.getAuthorizedUserIds());
	    request.setTenantTemplateId(existing.getTenantTemplateId());
	    request.setMetadata(existing.getMetadata());
	}

	public Builder withAuthorizedUserId(String userId) {
	    if (request.getAuthorizedUserIds() == null) {
		request.setAuthorizedUserIds(new ArrayList<String>());
	    }
	    request.getAuthorizedUserIds().add(userId);
	    return this;
	}

	public Builder metadata(String name, String value) {
	    if (request.getMetadata() == null) {
		request.setMetadata(new HashMap<String, String>());
	    }
	    request.getMetadata().put(name, value);
	    return this;
	}

	public TenantCreateRequest build() {
	    return request;
	}
    }
}