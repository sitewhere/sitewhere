package com.sitewhere.spi.server.groovy;

import com.sitewhere.spi.server.lifecycle.ITenantLifecycleComponent;

import groovy.util.GroovyScriptEngine;

/**
 * Supports access to a common Groovy configuration for tenant components.
 * 
 * @author Derek
 */
public interface ITenantGroovyConfiguration extends ITenantLifecycleComponent {

    /**
     * Get Groovy script engine.
     * 
     * @return
     */
    public GroovyScriptEngine getGroovyScriptEngine();
}