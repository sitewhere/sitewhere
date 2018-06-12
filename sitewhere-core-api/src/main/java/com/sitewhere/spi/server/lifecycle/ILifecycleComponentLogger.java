/*
 * Copyright (c) SiteWhere, LLC. All rights reserved. http://www.sitewhere.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package com.sitewhere.spi.server.lifecycle;

import org.slf4j.Logger;

import com.sitewhere.spi.microservice.logging.LogLevel;

/**
 * Logging interface which allows lifecycle component metadata to be captured
 * along with other logging information.
 * 
 * @author Derek
 */
public interface ILifecycleComponentLogger extends Logger {

    /**
     * Get lifeycle component associated with logger.
     * 
     * @return
     */
    public ILifecycleComponent getLifecycleComponent();

    /**
     * Any log output below this level for the lifecycle component will be elevated
     * to this level.
     * 
     * @param level
     */
    public void setLogLevelOverride(LogLevel level);

    /**
     * Trace log internationalization message.
     * 
     * @param key
     * @param args
     */
    public void trace(Enum<?> key, Object... args);

    /**
     * Trace log internationalization message.
     * 
     * @param e
     * @param key
     * @param args
     */
    public void trace(Throwable e, Enum<?> key, Object... args);

    /**
     * Debug log internationalization message.
     * 
     * @param key
     * @param args
     */
    public void debug(Enum<?> key, Object... args);

    /**
     * Debug log internationalization message.
     * 
     * @param e
     * @param key
     * @param args
     */
    public void debug(Throwable e, Enum<?> key, Object... args);

    /**
     * Information log internationalization message.
     * 
     * @param key
     * @param args
     */
    public void info(Enum<?> key, Object... args);

    /**
     * Information log internationalization message.
     * 
     * @param e
     * @param key
     * @param args
     */
    public void info(Throwable e, Enum<?> key, Object... args);

    /**
     * Warning log internationalization message.
     * 
     * @param key
     * @param args
     */
    public void warn(Enum<?> key, Object... args);

    /**
     * Warning log internationalization message.
     * 
     * @param e
     * @param key
     * @param args
     */
    public void warn(Throwable e, Enum<?> key, Object... args);

    /**
     * Error log internationalization message.
     * 
     * @param key
     * @param args
     */
    public void error(Enum<?> key, Object... args);

    /**
     * Error log internationalization message.
     * 
     * @param e
     * @param key
     * @param args
     */
    public void error(Throwable e, Enum<?> key, Object... args);

    /**
     * Use localized version instead.
     */
    // @Deprecated
    public void debug(String message);

    /**
     * Use localized version instead.
     */
    // @Deprecated
    public void info(String message);

    /**
     * Use localized version instead.
     */
    // @Deprecated
    public void warn(String message, Throwable t);

    /**
     * Use localized version instead.
     */
    // @Deprecated
    public void warn(String message);

    /**
     * Use localized version instead.
     */
    // @Deprecated
    public void error(String message, Throwable t);

    /**
     * Use localized version instead.
     */
    // @Deprecated
    public void error(String message);
}