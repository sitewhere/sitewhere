/*
 * Copyright (c) SiteWhere, LLC. All rights reserved. http://www.sitewhere.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package com.sitewhere.microservice.scripting;

import com.sitewhere.spi.microservice.scripting.IScriptContext;

/**
 * Context for scripts scoped at the instance level.
 */
public class InstanceScriptContext implements IScriptContext {

    /** Base path for instance scripts */
    private static final String BASE_PATH = "instance";

    /*
     * @see com.sitewhere.spi.microservice.scripting.IScriptContext#getBasePath()
     */
    @Override
    public String getBasePath() {
	return BASE_PATH;
    }
}
