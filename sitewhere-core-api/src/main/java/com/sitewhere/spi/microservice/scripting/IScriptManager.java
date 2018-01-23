/*
 * Copyright (c) SiteWhere, LLC. All rights reserved. http://www.sitewhere.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package com.sitewhere.spi.microservice.scripting;

import com.sitewhere.spi.SiteWhereException;
import com.sitewhere.spi.server.lifecycle.ILifecycleComponent;

/**
 * Manages runtime scripting support for a microservice.
 * 
 * @author Derek
 */
public interface IScriptManager extends ILifecycleComponent {

    public IScriptMetadata resolve(String scriptId) throws SiteWhereException;
}