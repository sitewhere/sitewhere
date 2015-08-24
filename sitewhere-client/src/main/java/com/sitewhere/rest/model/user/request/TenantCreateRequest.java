/*
 * Copyright (c) SiteWhere, LLC. All rights reserved. http://www.sitewhere.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package com.sitewhere.rest.model.user.request;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import com.sitewhere.rest.model.common.MetadataProvider;
import com.sitewhere.spi.user.request.ITenantCreateRequest;

/**
 * Default implementation of {@link ITenantCreateRequest} for use in REST services.
 * 
 * @author Derek
 */
public class TenantCreateRequest extends MetadataProvider implements ITenantCreateRequest, Serializable {

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

	/** SiteWhere engine configuration */
	private String engineConfiguration;

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
	 * @see com.sitewhere.spi.user.request.ITenantCreateRequest#getAuthenticationToken()
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
	 * @see com.sitewhere.spi.user.request.ITenantCreateRequest#getAuthorizedUserIds()
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
	 * @see com.sitewhere.spi.user.request.ITenantCreateRequest#getEngineConfiguration()
	 */
	public String getEngineConfiguration() {
		return engineConfiguration;
	}

	public void setEngineConfiguration(String engineConfiguration) {
		this.engineConfiguration = engineConfiguration;
	}
}