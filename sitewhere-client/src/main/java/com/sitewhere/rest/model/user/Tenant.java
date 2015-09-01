/*
 * Copyright (c) SiteWhere, LLC. All rights reserved. http://www.sitewhere.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package com.sitewhere.rest.model.user;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.sitewhere.rest.model.common.MetadataProviderEntity;
import com.sitewhere.spi.server.ISiteWhereTenantEngineState;
import com.sitewhere.spi.user.ITenant;

/**
 * Default implementation of {@link ITenant} interface used for REST services.
 * 
 * @author Derek
 */
@JsonInclude(Include.NON_NULL)
public class Tenant extends MetadataProviderEntity implements ITenant, Serializable {

	/** Serial version UUID */
	private static final long serialVersionUID = -353489785570975056L;

	/** Unqiue tenant id */
	private String id;

	/** Tenant name */
	private String name;

	/** Device authentication token */
	private String authenticationToken;

	/** Tenant logo URL */
	private String logoUrl;

	/** List of user ids authorized to access tenant */
	private List<String> authorizedUserIds = new ArrayList<String>();

	/** SiteWhere engine configuration for tenant */
	private String engineConfiguration;

	/** Runtime state of tenant engine */
	private ISiteWhereTenantEngineState engineState;

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.sitewhere.spi.user.ITenant#getId()
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
	 * @see com.sitewhere.spi.user.ITenant#getName()
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
	 * @see com.sitewhere.spi.user.ITenant#getAuthenticationToken()
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
	 * @see com.sitewhere.spi.user.ITenant#getLogoUrl()
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
	 * @see com.sitewhere.spi.user.ITenant#getAuthorizedUserIds()
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
	 * @see com.sitewhere.spi.user.ITenant#getEngineConfiguration()
	 */
	public String getEngineConfiguration() {
		return engineConfiguration;
	}

	public void setEngineConfiguration(String engineConfiguration) {
		this.engineConfiguration = engineConfiguration;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.sitewhere.spi.user.ITenant#getEngineState()
	 */
	public ISiteWhereTenantEngineState getEngineState() {
		return engineState;
	}

	public void setEngineState(ISiteWhereTenantEngineState engineState) {
		this.engineState = engineState;
	}
}