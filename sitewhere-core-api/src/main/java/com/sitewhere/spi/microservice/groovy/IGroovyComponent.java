/*
 * Copyright (c) SiteWhere, LLC. All rights reserved. http://www.sitewhere.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package com.sitewhere.spi.microservice.groovy;

import com.sitewhere.spi.SiteWhereException;
import com.sitewhere.spi.server.lifecycle.ILifecycleComponent;

import groovy.lang.Binding;

/**
 * Component that leverages a Groovy script.
 */
public interface IGroovyComponent extends ILifecycleComponent {

    /**
     * Get id of script that will be executed.
     * 
     * @return
     */
    public String getScriptId();

    /**
     * Get number of threads used for script processing.
     * 
     * @return
     */
    public int getNumThreads();

    /**
     * Create base binding for a given lifecycle component.
     * 
     * @param component
     * @return
     * @throws SiteWhereException
     */
    public Binding createBindingFor(ILifecycleComponent component) throws SiteWhereException;

    /**
     * Run script with the given binding and potentially return a result.
     * 
     * @param binding
     * @return
     * @throws SiteWhereException
     */
    public Object run(Binding binding) throws SiteWhereException;
}