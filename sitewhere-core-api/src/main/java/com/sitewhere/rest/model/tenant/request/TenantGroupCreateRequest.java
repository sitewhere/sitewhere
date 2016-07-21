/*
 * Copyright (c) SiteWhere, LLC. All rights reserved. http://www.sitewhere.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package com.sitewhere.rest.model.tenant.request;

import java.io.Serializable;
import java.util.HashMap;

import com.sitewhere.rest.model.common.MetadataProvider;
import com.sitewhere.spi.tenant.request.ITenantGroupCreateRequest;

/**
 * Model object for attributes needed to create a tenant group.
 * 
 * @author Derek
 */
public class TenantGroupCreateRequest extends MetadataProvider implements ITenantGroupCreateRequest, Serializable {

	/** Serial version UID */
	private static final long serialVersionUID = -6593183022611071721L;

	/** Unqiue token. Leave null to auto-generate */
	private String token;

	/** Group name */
	private String name;

	/** Group description */
	private String description;

	/** Group image URL */
	private String imageUrl;

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.sitewhere.spi.tenant.request.ITenantGroupCreateRequest#getToken()
	 */
	public String getToken() {
		return token;
	}

	public void setToken(String token) {
		this.token = token;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.sitewhere.spi.tenant.request.ITenantGroupCreateRequest#getName()
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
	 * @see
	 * com.sitewhere.spi.tenant.request.ITenantGroupCreateRequest#getDescription
	 * ()
	 */
	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.sitewhere.spi.tenant.request.ITenantGroupCreateRequest#getImageUrl()
	 */
	public String getImageUrl() {
		return imageUrl;
	}

	public void setImageUrl(String imageUrl) {
		this.imageUrl = imageUrl;
	}

	public static class Builder {

		/** Request being built */
		private TenantGroupCreateRequest request = new TenantGroupCreateRequest();

		public Builder(String name) {
			request.setName(name);
		}

		public Builder(String token, String name) {
			request.setToken(token);
			request.setName(name);
		}

		public Builder withDescription(String description) {
			request.setDescription(description);
			return this;
		}

		public Builder withImageUrl(String imageUrl) {
			request.setImageUrl(imageUrl);
			return this;
		}

		public Builder metadata(String name, String value) {
			if (request.getMetadata() == null) {
				request.setMetadata(new HashMap<String, String>());
			}
			request.getMetadata().put(name, value);
			return this;
		}

		public TenantGroupCreateRequest build() {
			return request;
		}
	}
}