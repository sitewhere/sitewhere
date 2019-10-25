/*
 * Copyright (c) SiteWhere, LLC. All rights reserved. http://www.sitewhere.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package com.sitewhere.microservice.scripting;

import com.google.common.base.CaseFormat;
import com.sitewhere.spi.microservice.multitenant.IMicroserviceTenantEngine;
import com.sitewhere.spi.microservice.scripting.IScriptContext;

/**
 * Provides context for scripts specific to a tenant engine.
 */
public class TenantEngineScriptContext implements IScriptContext {

    /** Base path for tenant scripts */
    private static final String TENANT_PATH = "tenant";

    /** Tenant engine for context */
    private IMicroserviceTenantEngine tenantEngine;

    /** Computed base path */
    private String basePath;

    public TenantEngineScriptContext(IMicroserviceTenantEngine tenantEngine) {
	this.tenantEngine = tenantEngine;
	String subpath = CaseFormat.LOWER_HYPHEN.to(CaseFormat.LOWER_CAMEL,
		getTenantEngine().getMicroservice().getIdentifier().getPath());
	String tenant = CaseFormat.LOWER_HYPHEN.to(CaseFormat.LOWER_CAMEL, getTenantEngine().getTenant().getToken());
	this.basePath = String.format("%s/%s/%s", TENANT_PATH, tenant, subpath);
    }

    /*
     * @see com.sitewhere.spi.microservice.scripting.IScriptContext#getBasePath()
     */
    @Override
    public String getBasePath() {
	return basePath;
    }

    protected IMicroserviceTenantEngine getTenantEngine() {
	return tenantEngine;
    }
}
