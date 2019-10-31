/*
 * Copyright (c) SiteWhere, LLC. All rights reserved. http://www.sitewhere.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package com.sitewhere.server.lifecycle.parameters;

import java.util.HashMap;
import java.util.Map;

import com.sitewhere.spi.SiteWhereException;
import com.sitewhere.spi.microservice.multitenant.IMicroserviceTenantEngine;
import com.sitewhere.spi.server.lifecycle.ILifecycleComponent;
import com.sitewhere.spi.server.lifecycle.ITenantEngineLifecycleComponent;

/**
 * Resolves parameter values that contain variables that should be substituted
 * at runtime.
 */
public class ValueResolver {

    /** Prefix for variables */
    private static final String PREFIX = "[[";

    /** Suffix for variables */
    private static final String SUFFIX = "]]";

    /** Tenant id marker */
    private static final String TENANT_ID = "tenant.id";

    /**
     * Resolve variable expressions within a value.
     * 
     * @param value
     * @param context
     * @return
     * @throws SiteWhereException
     */
    public static String resolve(String value, ILifecycleComponent context) throws SiteWhereException {
	IMicroserviceTenantEngine engine = (context instanceof ITenantEngineLifecycleComponent)
		? ((ITenantEngineLifecycleComponent) context).getTenantEngine()
		: null;
	if ((engine == null) && (value.indexOf(asVariable(TENANT_ID)) != -1)) {
	    throw new SiteWhereException("Unable to resolve reference to tenant id in a global component.");
	}

	String result = value;
	Map<String, String> variables = new HashMap<String, String>();
	if (engine != null) {
	    variables.put(TENANT_ID, engine.getTenant().getToken());
	}
	for (String key : variables.keySet()) {
	    result = result.replace(asVariable(key), variables.get(key));
	}
	return result;
    }

    /**
     * Get key value as it would look as a variable.
     * 
     * @param key
     * @return
     */
    protected static String asVariable(String key) {
	return PREFIX + key + SUFFIX;
    }
}
