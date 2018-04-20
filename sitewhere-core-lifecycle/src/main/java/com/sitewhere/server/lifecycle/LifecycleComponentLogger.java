/*
 * Copyright (c) SiteWhere, LLC. All rights reserved. http://www.sitewhere.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package com.sitewhere.server.lifecycle;

import java.util.UUID;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.sitewhere.rest.model.microservice.logging.LoggedException;
import com.sitewhere.rest.model.microservice.logging.MicroserviceLogMessage;
import com.sitewhere.spi.SiteWhereException;
import com.sitewhere.spi.microservice.IMicroservice;
import com.sitewhere.spi.microservice.logging.LogLevel;
import com.sitewhere.spi.microservice.multitenant.IMicroserviceTenantEngine;
import com.sitewhere.spi.server.lifecycle.ILifecycleComponent;
import com.sitewhere.spi.server.lifecycle.ILifecycleComponentLogger;
import com.sitewhere.spi.server.lifecycle.ITenantEngineLifecycleComponent;

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

    /** Log level override */
    private LogLevel logLevelOverride = null;

    public LifecycleComponentLogger(ILifecycleComponent lifecycleComponent) {
	this.lifecycleComponent = lifecycleComponent;
	this.logger = LogFactory.getLog(lifecycleComponent.getClass());
    }

    /**
     * Log a message to centralized logging for the microservice.
     * 
     * @param level
     * @param message
     * @param e
     */
    protected void log(LogLevel level, Object message, Throwable e) {
	// Elevate log level if requested.
	if ((getLogLevelOverride() != null) && (level.getLevel() < getLogLevelOverride().getLevel())) {
	    level = getLogLevelOverride();
	    message = "[Elevated] " + message;
	}
	forwardToLogImpl(level, message, e);

	MicroserviceLogMessage log = new MicroserviceLogMessage();
	IMicroservice<?> microservice = getLifecycleComponent().getMicroservice();
	if (microservice == null) {
	    getLogger().error("Microservice was null for component '" + getLifecycleComponent().getClass().getName()
		    + "'. Unable to forward log message.");
	    return;
	}
	if (microservice.getMicroserviceLogProducer() != null) {
	    log.setMicroserviceIdentifier(microservice.getIdentifier().getPath());
	    log.setMicroserviceContainerId(microservice.getHostname());
	    if (getLifecycleComponent() instanceof ITenantEngineLifecycleComponent) {
		IMicroserviceTenantEngine engine = ((ITenantEngineLifecycleComponent) getLifecycleComponent())
			.getTenantEngine();
		if (engine != null) {
		    UUID tenantId = engine.getTenant().getId();
		    log.setTenantId(tenantId);
		}
	    }
	    log.setLogLevel(level);
	    log.setMessageText(message.toString());
	    if (e != null) {
		LoggedException logex = new LoggedException();
		logex.setMessageText(e.getMessage() != null ? e.getMessage() : e.getClass().getName());
		log.setException(logex);
	    }
	    try {
		microservice.getMicroserviceLogProducer().send(log);
	    } catch (SiteWhereException e1) {
		e1.printStackTrace();
	    }
	}
    }

    /**
     * Forward log to underlying implementation.
     * 
     * @param level
     * @param message
     * @param t
     */
    protected void forwardToLogImpl(LogLevel level, Object message, Throwable t) {
	switch (level) {
	case Trace: {
	    if (t != null) {
		getLogger().trace(message, t);
	    } else {
		getLogger().trace(message);
	    }
	    break;
	}
	case Debug: {
	    if (t != null) {
		getLogger().debug(message, t);
	    } else {
		getLogger().debug(message);
	    }
	    break;
	}
	case Information: {
	    if (t != null) {
		getLogger().info(message, t);
	    } else {
		getLogger().info(message);
	    }
	    break;
	}
	case Warning: {
	    if (t != null) {
		getLogger().warn(message, t);
	    } else {
		getLogger().warn(message);
	    }
	    break;
	}
	case Error: {
	    if (t != null) {
		getLogger().error(message, t);
	    } else {
		getLogger().error(message);
	    }
	    break;
	}
	case Fatal: {
	    if (t != null) {
		getLogger().fatal(message, t);
	    } else {
		getLogger().fatal(message);
	    }
	    break;
	}
	}
    }

    /*
     * @see org.apache.commons.logging.Log#debug(java.lang.Object)
     */
    @Override
    public void debug(Object message) {
	log(LogLevel.Debug, message, null);
    }

    /*
     * @see org.apache.commons.logging.Log#debug(java.lang.Object,
     * java.lang.Throwable)
     */
    @Override
    public void debug(Object message, Throwable t) {
	log(LogLevel.Debug, message, t);
    }

    /*
     * @see org.apache.commons.logging.Log#error(java.lang.Object)
     */
    @Override
    public void error(Object message) {
	log(LogLevel.Error, message, null);
    }

    /*
     * @see org.apache.commons.logging.Log#error(java.lang.Object,
     * java.lang.Throwable)
     */
    @Override
    public void error(Object message, Throwable t) {
	log(LogLevel.Error, message, t);
    }

    /*
     * @see org.apache.commons.logging.Log#fatal(java.lang.Object)
     */
    @Override
    public void fatal(Object message) {
	log(LogLevel.Fatal, message, null);
    }

    /*
     * @see org.apache.commons.logging.Log#fatal(java.lang.Object,
     * java.lang.Throwable)
     */
    @Override
    public void fatal(Object message, Throwable t) {
	log(LogLevel.Fatal, message, t);
    }

    /*
     * @see org.apache.commons.logging.Log#info(java.lang.Object)
     */
    @Override
    public void info(Object message) {
	log(LogLevel.Information, message, null);
    }

    /*
     * @see org.apache.commons.logging.Log#info(java.lang.Object,
     * java.lang.Throwable)
     */
    @Override
    public void info(Object message, Throwable t) {
	log(LogLevel.Information, message, t);
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
	log(LogLevel.Trace, message, null);
    }

    /*
     * @see org.apache.commons.logging.Log#trace(java.lang.Object,
     * java.lang.Throwable)
     */
    @Override
    public void trace(Object message, Throwable t) {
	log(LogLevel.Trace, message, t);
    }

    /*
     * @see org.apache.commons.logging.Log#warn(java.lang.Object)
     */
    @Override
    public void warn(Object message) {
	log(LogLevel.Warning, message, null);
    }

    /*
     * @see org.apache.commons.logging.Log#warn(java.lang.Object,
     * java.lang.Throwable)
     */
    @Override
    public void warn(Object message, Throwable t) {
	log(LogLevel.Warning, message, t);
    }

    /*
     * @see com.sitewhere.spi.server.lifecycle.ILifecycleComponentLogger#
     * getLifecycleComponent()
     */
    @Override
    public ILifecycleComponent getLifecycleComponent() {
	return lifecycleComponent;
    }

    public LogLevel getLogLevelOverride() {
	return logLevelOverride;
    }

    /*
     * @see com.sitewhere.spi.server.lifecycle.ILifecycleComponentLogger#
     * setLogLevelOverride(com.sitewhere.spi.microservice.logging.LogLevel)
     */
    @Override
    public void setLogLevelOverride(LogLevel logLevelOverride) {
	this.logLevelOverride = logLevelOverride;
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