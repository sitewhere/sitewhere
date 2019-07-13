/*
 * Copyright (c) SiteWhere, LLC. All rights reserved. http://www.sitewhere.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package com.sitewhere.microservice.tenant.persistence;

import com.sitewhere.rest.model.common.BrandedEntity;
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
	target.setName(source.getName());
	target.setAuthenticationToken(source.getAuthenticationToken());
	target.setAuthorizedUserIds(source.getAuthorizedUserIds());
	target.setTenantTemplateId(source.getTenantTemplateId());
	target.setDatasetTemplateId(source.getDatasetTemplateId());
	BrandedEntity.copy(source, target);
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
	Tenant.copy(source, tenant);
	return tenant;
    }
}
