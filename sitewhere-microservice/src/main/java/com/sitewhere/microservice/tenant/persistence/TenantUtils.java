/*
 * Copyright (c) SiteWhere, LLC. All rights reserved. http://www.sitewhere.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package com.sitewhere.microservice.tenant.persistence;

import com.sitewhere.rest.model.tenant.Tenant;
import com.sitewhere.spi.SiteWhereException;
import com.sitewhere.spi.tenant.ITenant;

public class TenantUtils {

    /**
     * Copy fields from source to target.
     * 
     * @param source
     * @param target
     */
    public static void copy(ITenant source, Tenant target) throws SiteWhereException {
	target.setToken(source.getToken());
	target.setName(source.getName());
	target.setAuthenticationToken(source.getAuthenticationToken());
	target.setAuthorizedUserIds(source.getAuthorizedUserIds());
	target.setConfigurationTemplateId(source.getConfigurationTemplateId());
	target.setDatasetTemplateId(source.getDatasetTemplateId());
	target.setBackgroundColor(source.getBackgroundColor());
	target.setForegroundColor(source.getForegroundColor());
	target.setBorderColor(source.getBorderColor());
	target.setIcon(source.getIcon());
	target.setImageUrl(source.getImageUrl());
	target.setMetadata(source.getMetadata());
    }

    /**
     * Create a copy of the API object.
     * 
     * @param source
     * @return
     * @throws SiteWhereException
     */
    public static Tenant copy(ITenant source) throws SiteWhereException {
	Tenant tenant = new Tenant();
	TenantUtils.copy(source, tenant);
	return tenant;
    }
}
