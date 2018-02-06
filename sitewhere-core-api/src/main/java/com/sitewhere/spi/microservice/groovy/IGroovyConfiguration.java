/*
 * Copyright (c) SiteWhere, LLC. All rights reserved. http://www.sitewhere.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package com.sitewhere.spi.microservice.groovy;

import com.sitewhere.spi.SiteWhereException;
import com.sitewhere.spi.microservice.scripting.IScriptMetadata;
import com.sitewhere.spi.server.lifecycle.ILifecycleComponent;

import groovy.lang.Binding;

/**
 * Provides common Groovy configuration for core server components.
 * 
 * @author Derek
 */
public interface IGroovyConfiguration extends ILifecycleComponent {

    /**
     * Run a Groovy script with the given binding and potentially return a result.
     * 
     * @param scriptPath
     * @param binding
     * @return
     * @throws SiteWhereException
     */
    public Object run(String scriptPath, Binding binding) throws SiteWhereException;

    /**
     * Run a Groovy script with the given binding and potentially return a result.
     * 
     * @param script
     * @param binding
     * @return
     * @throws SiteWhereException
     */
    public Object run(IScriptMetadata script, Binding binding) throws SiteWhereException;
}