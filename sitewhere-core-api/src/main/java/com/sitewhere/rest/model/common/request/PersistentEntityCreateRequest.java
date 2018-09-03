/*
 * Copyright (c) SiteWhere, LLC. All rights reserved. http://www.sitewhere.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package com.sitewhere.rest.model.common.request;

import com.sitewhere.rest.model.common.MetadataProvider;
import com.sitewhere.spi.common.request.IPersistentEntityCreateRequest;

/**
 * Base class for fields needed to create a persistent entity.
 * 
 * @author Derek
 */
public class PersistentEntityCreateRequest extends MetadataProvider implements IPersistentEntityCreateRequest {

    /** Serial version UID */
    private static final long serialVersionUID = 8334896105601825091L;

    /** Token */
    private String token;

    /*
     * @see
     * com.sitewhere.spi.common.request.IPersistentEntityCreateRequest#getToken()
     */
    @Override
    public String getToken() {
	return token;
    }

    public void setToken(String token) {
	this.token = token;
    }
}
