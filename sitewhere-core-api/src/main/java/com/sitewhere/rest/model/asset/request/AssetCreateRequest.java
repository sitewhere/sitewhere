/*
 * Copyright (c) SiteWhere, LLC. All rights reserved. http://www.sitewhere.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package com.sitewhere.rest.model.asset.request;

import java.util.HashMap;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.sitewhere.rest.model.common.request.PersistentEntityCreateRequest;
import com.sitewhere.spi.asset.request.IAssetCreateRequest;

/**
 * REST model implementation of {@link IAssetCreateRequest}.
 * 
 * @author Derek
 */
@JsonInclude(Include.NON_NULL)
public class AssetCreateRequest extends PersistentEntityCreateRequest implements IAssetCreateRequest {

    /** Serial version UID */
    private static final long serialVersionUID = -3557255561907642778L;

    /** Asset type token */
    private String assetTypeToken;

    /** Asset name */
    private String name;

    /** Asset image url */
    private String imageUrl;

    /*
     * @see com.sitewhere.spi.asset.request.IAssetCreateRequest#getAssetTypeToken()
     */
    @Override
    public String getAssetTypeToken() {
	return assetTypeToken;
    }

    public void setAssetTypeToken(String assetTypeToken) {
	this.assetTypeToken = assetTypeToken;
    }

    /*
     * @see com.sitewhere.spi.asset.request.IAssetCreateRequest#getName()
     */
    @Override
    public String getName() {
	return name;
    }

    public void setName(String name) {
	this.name = name;
    }

    /*
     * @see com.sitewhere.spi.asset.request.IAssetCreateRequest#getImageUrl()
     */
    @Override
    public String getImageUrl() {
	return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
	this.imageUrl = imageUrl;
    }

    public static class Builder {

	/** Request being built */
	private AssetCreateRequest request = new AssetCreateRequest();

	public Builder(String token, String assetTypeToken, String name) {
	    request.setToken(token);
	    request.setAssetTypeToken(assetTypeToken);
	    request.setName(name);
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

	public AssetCreateRequest build() {
	    return request;
	}
    }
}