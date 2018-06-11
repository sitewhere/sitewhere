/*
 * Copyright (c) SiteWhere, LLC. All rights reserved. http://www.sitewhere.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package com.sitewhere.server.lifecycle;

import java.util.Locale;
import java.util.UUID;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.Marker;
import org.slf4j.cal10n.LocLogger;
import org.slf4j.cal10n.LocLoggerFactory;

import com.sitewhere.rest.model.microservice.logging.LoggedException;
import com.sitewhere.rest.model.microservice.logging.MicroserviceLogMessage;
import com.sitewhere.spi.SiteWhereException;
import com.sitewhere.spi.microservice.IMicroservice;
import com.sitewhere.spi.microservice.logging.LogLevel;
import com.sitewhere.spi.microservice.multitenant.IMicroserviceTenantEngine;
import com.sitewhere.spi.server.lifecycle.ILifecycleComponent;
import com.sitewhere.spi.server.lifecycle.ILifecycleComponentLogger;
import com.sitewhere.spi.server.lifecycle.ITenantEngineLifecycleComponent;

import ch.qos.cal10n.IMessageConveyor;
import ch.qos.cal10n.MessageConveyor;


/**
 * Logger that supports gathering extra microservice metadata in addition to
 * standard logging.
 * 
 * @author Derek
 */
public class LifecycleComponentLogger implements ILifecycleComponentLogger {

    /** Logger name */
    private static final String LOGGER_NAME = "sitewhere";

    /** Static logger instance */
    private Logger logger;

    /** Lifecycle component for logger */
    private ILifecycleComponent lifecycleComponent;

    /** Log level override */
    private LogLevel logLevelOverride = null;
    
    /**
     * Locale used for i18n
     */
    private Locale locale = Locale.getDefault();
    
    /**
     * message conveyor for a given locale
     */
    private IMessageConveyor  messageConveyor = new MessageConveyor(locale);
    
    /**
     * Localize Logger Factory
     */
    private LocLoggerFactory locLoggerFactory = new LocLoggerFactory(messageConveyor);
    
    /**
     * Localize Logger
     */
    private LocLogger localizeLogger = locLoggerFactory.getLocLogger(this.getClass());
    

    public LifecycleComponentLogger(ILifecycleComponent lifecycleComponent) {
	this.lifecycleComponent = lifecycleComponent;
	this.logger = LoggerFactory.getLogger(lifecycleComponent.getClass());
	this.localizeLogger = locLoggerFactory.getLocLogger(lifecycleComponent.getClass());
    }

    /**
     * Log a message to centralized logging for the microservice.
     * 
     * @param level
     * @param message
     * @param e
     */
    protected void log(LogLevel level, String message, Throwable e) {
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
    protected void forwardToLogImpl(LogLevel level, String message, Throwable t) {
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
	}
    }

    /*
     * @see org.slf4j.Logger#getName()
     */
    @Override
    public String getName() {
	return LOGGER_NAME;
    }

    /*
     * @see org.slf4j.Logger#debug(java.lang.String)
     */
    @Override
    public void debug(String message) {
	log(LogLevel.Debug, message, null);
    }

    /*
     * @see org.slf4j.Logger#debug(java.lang.String, java.lang.Throwable)
     */
    @Override
    public void debug(String message, Throwable t) {
	log(LogLevel.Debug, message, t);
    }

    /*
     * @see org.slf4j.Logger#debug(java.lang.String, java.lang.Object)
     */
    @Override
    public void debug(String format, Object arg) {
    }

    /*
     * @see org.slf4j.Logger#debug(java.lang.String, java.lang.Object,
     * java.lang.Object)
     */
    @Override
    public void debug(String format, Object arg1, Object arg2) {
    }

    /*
     * @see org.slf4j.Logger#debug(java.lang.String, java.lang.Object[])
     */
    @Override
    public void debug(String format, Object... arguments) {
    }

    /*
     * @see org.slf4j.Logger#debug(org.slf4j.Marker, java.lang.String)
     */
    @Override
    public void debug(Marker marker, String msg) {
    }

    /*
     * @see org.slf4j.Logger#debug(org.slf4j.Marker, java.lang.String,
     * java.lang.Object)
     */
    @Override
    public void debug(Marker marker, String format, Object arg) {
    }

    /*
     * @see org.slf4j.Logger#debug(org.slf4j.Marker, java.lang.String,
     * java.lang.Object, java.lang.Object)
     */
    @Override
    public void debug(Marker marker, String format, Object arg1, Object arg2) {
    }

    /*
     * @see org.slf4j.Logger#debug(org.slf4j.Marker, java.lang.String,
     * java.lang.Object[])
     */
    @Override
    public void debug(Marker marker, String format, Object... arguments) {
    }

    /*
     * @see org.slf4j.Logger#debug(org.slf4j.Marker, java.lang.String,
     * java.lang.Throwable)
     */
    @Override
    public void debug(Marker marker, String msg, Throwable t) {
    }

    /*
     * @see org.slf4j.Logger#error(java.lang.String)
     */
    @Override
    public void error(String message) {
	log(LogLevel.Error, message, null);
    }

    /*
     * @see org.slf4j.Logger#error(java.lang.String, java.lang.Throwable)
     */
    @Override
    public void error(String message, Throwable t) {
	log(LogLevel.Error, message, t);
    }

    /*
     * @see org.slf4j.Logger#error(java.lang.String, java.lang.Object)
     */
    @Override
    public void error(String format, Object arg) {
    }

    /*
     * @see org.slf4j.Logger#error(java.lang.String, java.lang.Object,
     * java.lang.Object)
     */
    @Override
    public void error(String format, Object arg1, Object arg2) {
    }

    /*
     * @see org.slf4j.Logger#error(java.lang.String, java.lang.Object[])
     */
    @Override
    public void error(String format, Object... arguments) {
    }

    /*
     * @see org.slf4j.Logger#error(org.slf4j.Marker, java.lang.String)
     */
    @Override
    public void error(Marker marker, String msg) {
    }

    /*
     * @see org.slf4j.Logger#error(org.slf4j.Marker, java.lang.String,
     * java.lang.Object)
     */
    @Override
    public void error(Marker marker, String format, Object arg) {
    }

    /*
     * @see org.slf4j.Logger#error(org.slf4j.Marker, java.lang.String,
     * java.lang.Object, java.lang.Object)
     */
    @Override
    public void error(Marker marker, String format, Object arg1, Object arg2) {
    }

    /*
     * @see org.slf4j.Logger#error(org.slf4j.Marker, java.lang.String,
     * java.lang.Object[])
     */
    @Override
    public void error(Marker marker, String format, Object... arguments) {
    }

    /*
     * @see org.slf4j.Logger#error(org.slf4j.Marker, java.lang.String,
     * java.lang.Throwable)
     */
    @Override
    public void error(Marker marker, String msg, Throwable t) {
    }

    /*
     * @see org.slf4j.Logger#info(java.lang.String)
     */
    @Override
    public void info(String message) {
	log(LogLevel.Information, message, null);
    }

    /*
     * @see org.slf4j.Logger#info(java.lang.String, java.lang.Throwable)
     */
    @Override
    public void info(String message, Throwable t) {
	log(LogLevel.Information, message, t);
    }

    /*
     * @see org.slf4j.Logger#info(java.lang.String, java.lang.Object)
     */
    @Override
    public void info(String format, Object arg) {
    }

    /*
     * @see org.slf4j.Logger#info(java.lang.String, java.lang.Object,
     * java.lang.Object)
     */
    @Override
    public void info(String format, Object arg1, Object arg2) {
    }

    /*
     * @see org.slf4j.Logger#info(java.lang.String, java.lang.Object[])
     */
    @Override
    public void info(String format, Object... arguments) {
    }

    /*
     * @see org.slf4j.Logger#info(org.slf4j.Marker, java.lang.String)
     */
    @Override
    public void info(Marker marker, String msg) {
    }

    /*
     * @see org.slf4j.Logger#info(org.slf4j.Marker, java.lang.String,
     * java.lang.Object)
     */
    @Override
    public void info(Marker marker, String format, Object arg) {
    }

    /*
     * @see org.slf4j.Logger#info(org.slf4j.Marker, java.lang.String,
     * java.lang.Object, java.lang.Object)
     */
    @Override
    public void info(Marker marker, String format, Object arg1, Object arg2) {
    }

    /*
     * @see org.slf4j.Logger#info(org.slf4j.Marker, java.lang.String,
     * java.lang.Object[])
     */
    @Override
    public void info(Marker marker, String format, Object... arguments) {
    }

    /*
     * @see org.slf4j.Logger#info(org.slf4j.Marker, java.lang.String,
     * java.lang.Throwable)
     */
    @Override
    public void info(Marker marker, String msg, Throwable t) {
    }

    /*
     * @see org.slf4j.Logger#isDebugEnabled()
     */
    @Override
    public boolean isDebugEnabled() {
	return getLogger().isDebugEnabled();
    }

    /*
     * @see org.slf4j.Logger#isDebugEnabled(org.slf4j.Marker)
     */
    @Override
    public boolean isDebugEnabled(Marker marker) {
	return getLogger().isDebugEnabled(marker);
    }

    /*
     * @see org.apache.commons.logging.Log#isErrorEnabled()
     */
    @Override
    public boolean isErrorEnabled() {
	return getLogger().isErrorEnabled();
    }

    /*
     * @see org.slf4j.Logger#isErrorEnabled(org.slf4j.Marker)
     */
    @Override
    public boolean isErrorEnabled(Marker marker) {
	return getLogger().isErrorEnabled(marker);
    }

    /*
     * @see org.slf4j.Logger#isInfoEnabled()
     */
    @Override
    public boolean isInfoEnabled() {
	return getLogger().isInfoEnabled();
    }

    /*
     * @see org.slf4j.Logger#isInfoEnabled(org.slf4j.Marker)
     */
    @Override
    public boolean isInfoEnabled(Marker marker) {
	return getLogger().isInfoEnabled(marker);
    }

    /*
     * @see org.slf4j.Logger#isTraceEnabled()
     */
    @Override
    public boolean isTraceEnabled() {
	return getLogger().isTraceEnabled();
    }

    /*
     * @see org.slf4j.Logger#isTraceEnabled(org.slf4j.Marker)
     */
    @Override
    public boolean isTraceEnabled(Marker marker) {
	return getLogger().isTraceEnabled(marker);
    }

    /*
     * @see org.slf4j.Logger#isWarnEnabled()
     */
    @Override
    public boolean isWarnEnabled() {
	return getLogger().isWarnEnabled();
    }

    /*
     * @see org.slf4j.Logger#isWarnEnabled(org.slf4j.Marker)
     */
    @Override
    public boolean isWarnEnabled(Marker marker) {
	return getLogger().isWarnEnabled(marker);
    }

    /*
     * @see org.slf4j.Logger#trace(java.lang.String)
     */
    @Override
    public void trace(String message) {
	log(LogLevel.Trace, message, null);
    }

    /*
     * @see org.slf4j.Logger#trace(java.lang.String, java.lang.Throwable)
     */
    @Override
    public void trace(String message, Throwable t) {
	log(LogLevel.Trace, message, t);
    }

    /*
     * @see org.slf4j.Logger#trace(java.lang.String, java.lang.Object)
     */
    @Override
    public void trace(String format, Object arg) {
    }

    /*
     * @see org.slf4j.Logger#trace(java.lang.String, java.lang.Object,
     * java.lang.Object)
     */
    @Override
    public void trace(String format, Object arg1, Object arg2) {
    }

    /*
     * @see org.slf4j.Logger#trace(java.lang.String, java.lang.Object[])
     */
    @Override
    public void trace(String format, Object... arguments) {
    }

    /*
     * @see org.slf4j.Logger#trace(org.slf4j.Marker, java.lang.String)
     */
    @Override
    public void trace(Marker marker, String msg) {
    }

    /*
     * @see org.slf4j.Logger#trace(org.slf4j.Marker, java.lang.String,
     * java.lang.Object)
     */
    @Override
    public void trace(Marker marker, String format, Object arg) {
    }

    /*
     * @see org.slf4j.Logger#trace(org.slf4j.Marker, java.lang.String,
     * java.lang.Object, java.lang.Object)
     */
    @Override
    public void trace(Marker marker, String format, Object arg1, Object arg2) {
    }

    /*
     * @see org.slf4j.Logger#trace(org.slf4j.Marker, java.lang.String,
     * java.lang.Object[])
     */
    @Override
    public void trace(Marker marker, String format, Object... argArray) {
    }

    /*
     * @see org.slf4j.Logger#trace(org.slf4j.Marker, java.lang.String,
     * java.lang.Throwable)
     */
    @Override
    public void trace(Marker marker, String msg, Throwable t) {
    }

    /*
     * @see org.slf4j.Logger#warn(java.lang.String)
     */
    @Override
    public void warn(String message) {
	log(LogLevel.Warning, message, null);
    }

    /*
     * @see org.slf4j.Logger#warn(java.lang.String, java.lang.Throwable)
     */
    @Override
    public void warn(String message, Throwable t) {
	log(LogLevel.Warning, message, t);
    }

    /*
     * @see org.slf4j.Logger#warn(java.lang.String, java.lang.Object)
     */
    @Override
    public void warn(String format, Object arg) {
    }

    /*
     * @see org.slf4j.Logger#warn(java.lang.String, java.lang.Object[])
     */
    @Override
    public void warn(String format, Object... arguments) {
    }

    /*
     * @see org.slf4j.Logger#warn(java.lang.String, java.lang.Object,
     * java.lang.Object)
     */
    @Override
    public void warn(String format, Object arg1, Object arg2) {
    }

    /*
     * @see org.slf4j.Logger#warn(org.slf4j.Marker, java.lang.String)
     */
    @Override
    public void warn(Marker marker, String msg) {
    }

    /*
     * @see org.slf4j.Logger#warn(org.slf4j.Marker, java.lang.String,
     * java.lang.Object)
     */
    @Override
    public void warn(Marker marker, String format, Object arg) {
    }

    /*
     * @see org.slf4j.Logger#warn(org.slf4j.Marker, java.lang.String,
     * java.lang.Object, java.lang.Object)
     */
    @Override
    public void warn(Marker marker, String format, Object arg1, Object arg2) {
    }

    /*
     * @see org.slf4j.Logger#warn(org.slf4j.Marker, java.lang.String,
     * java.lang.Object[])
     */
    @Override
    public void warn(Marker marker, String format, Object... arguments) {
    }

    /*
     * @see org.slf4j.Logger#warn(org.slf4j.Marker, java.lang.String,
     * java.lang.Throwable)
     */
    @Override
    public void warn(Marker marker, String msg, Throwable t) {
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

    public Logger getLogger() {
	return logger;
    }

    public void setLogger(Logger logger) {
	this.logger = logger;
    }

    @Override
    public void trace(Enum<?> key, Object... args) {
	this.localizeLogger.trace(key, args);
    }

    @Override
    public void debug(Enum<?> key, Object... args) {
	this.localizeLogger.debug(key, args);
    }

    @Override
    public void info(Enum<?> key, Object... args) {
	this.localizeLogger.info(key, args);
    }

    @Override
    public void warn(Enum<?> key, Object... args) {
	this.localizeLogger.error(key, args);
    }

    @Override
    public void error(Enum<?> key, Object... args) {
	this.localizeLogger.error(key, args);
    }
    
}