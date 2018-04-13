/*
 * Copyright (c) SiteWhere, LLC. All rights reserved. http://www.sitewhere.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package com.sitewhere.server.lifecycle;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.sitewhere.spi.server.lifecycle.ILifecycleComponent;
import com.sitewhere.spi.server.lifecycle.ILifecycleComponentLogger;

/**
 * Logger that supports gathering extra microservice metadata in addition to
 * standard logging.
 * 
 * @author Derek
 */
public class LifecycleComponentLogger implements ILifecycleComponentLogger {

    /** Static logger instance */
    private Log logger;

    /** Lifecycle component for logger */
    private ILifecycleComponent lifecycleComponent;

    public LifecycleComponentLogger(ILifecycleComponent lifecycleComponent) {
	this.lifecycleComponent = lifecycleComponent;
	this.logger = LogFactory.getLog(lifecycleComponent.getClass());
    }

    /*
     * @see org.apache.commons.logging.Log#debug(java.lang.Object)
     */
    @Override
    public void debug(Object message) {
	getLogger().debug(message);
    }

    /*
     * @see org.apache.commons.logging.Log#debug(java.lang.Object,
     * java.lang.Throwable)
     */
    @Override
    public void debug(Object message, Throwable t) {
	getLogger().debug(message, t);
    }

    /*
     * @see org.apache.commons.logging.Log#error(java.lang.Object)
     */
    @Override
    public void error(Object message) {
	getLogger().error(message);
    }

    /*
     * @see org.apache.commons.logging.Log#error(java.lang.Object,
     * java.lang.Throwable)
     */
    @Override
    public void error(Object message, Throwable t) {
	getLogger().error(message, t);
    }

    /*
     * @see org.apache.commons.logging.Log#fatal(java.lang.Object)
     */
    @Override
    public void fatal(Object message) {
	getLogger().fatal(message);
    }

    /*
     * @see org.apache.commons.logging.Log#fatal(java.lang.Object,
     * java.lang.Throwable)
     */
    @Override
    public void fatal(Object message, Throwable t) {
	getLogger().fatal(message, t);
    }

    /*
     * @see org.apache.commons.logging.Log#info(java.lang.Object)
     */
    @Override
    public void info(Object message) {
	getLogger().info(message);
    }

    /*
     * @see org.apache.commons.logging.Log#info(java.lang.Object,
     * java.lang.Throwable)
     */
    @Override
    public void info(Object message, Throwable t) {
	getLogger().info(message, t);
    }

    /*
     * @see org.apache.commons.logging.Log#isDebugEnabled()
     */
    @Override
    public boolean isDebugEnabled() {
	return getLogger().isDebugEnabled();
    }

    /*
     * @see org.apache.commons.logging.Log#isErrorEnabled()
     */
    @Override
    public boolean isErrorEnabled() {
	return getLogger().isErrorEnabled();
    }

    /*
     * @see org.apache.commons.logging.Log#isFatalEnabled()
     */
    @Override
    public boolean isFatalEnabled() {
	return getLogger().isFatalEnabled();
    }

    /*
     * @see org.apache.commons.logging.Log#isInfoEnabled()
     */
    @Override
    public boolean isInfoEnabled() {
	return getLogger().isInfoEnabled();
    }

    /*
     * @see org.apache.commons.logging.Log#isTraceEnabled()
     */
    @Override
    public boolean isTraceEnabled() {
	return getLogger().isTraceEnabled();
    }

    /*
     * @see org.apache.commons.logging.Log#isWarnEnabled()
     */
    @Override
    public boolean isWarnEnabled() {
	return getLogger().isWarnEnabled();
    }

    /*
     * @see org.apache.commons.logging.Log#trace(java.lang.Object)
     */
    @Override
    public void trace(Object message) {
	getLogger().trace(message);
    }

    /*
     * @see org.apache.commons.logging.Log#trace(java.lang.Object,
     * java.lang.Throwable)
     */
    @Override
    public void trace(Object message, Throwable t) {
	getLogger().trace(message, t);
    }

    /*
     * @see org.apache.commons.logging.Log#warn(java.lang.Object)
     */
    @Override
    public void warn(Object message) {
	getLogger().warn(message);
    }

    /*
     * @see org.apache.commons.logging.Log#warn(java.lang.Object,
     * java.lang.Throwable)
     */
    @Override
    public void warn(Object message, Throwable t) {
	getLogger().warn(message, t);
    }

    /*
     * @see com.sitewhere.spi.server.lifecycle.ILifecycleComponentLogger#
     * getLifecycleComponent()
     */
    @Override
    public ILifecycleComponent getLifecycleComponent() {
	return lifecycleComponent;
    }

    public void setLifecycleComponent(ILifecycleComponent lifecycleComponent) {
	this.lifecycleComponent = lifecycleComponent;
    }

    public Log getLogger() {
	return logger;
    }

    public void setLogger(Log logger) {
	this.logger = logger;
    }
}