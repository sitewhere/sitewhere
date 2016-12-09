package com.sitewhere.spi.server.groovy;

import com.sitewhere.spi.server.lifecycle.ILifecycleComponent;

import groovy.util.GroovyScriptEngine;

/**
 * Supports access to a common Groovy configuration for core server components.
 * 
 * @author Derek
 */
public interface IGroovyConfiguration extends ILifecycleComponent {

    /**
     * Get Groovy script engine.
     * 
     * @return
     */
    public GroovyScriptEngine getGroovyScriptEngine();
}