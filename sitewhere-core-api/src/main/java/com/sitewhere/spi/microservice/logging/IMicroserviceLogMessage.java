/*
 * Copyright (c) SiteWhere, LLC. All rights reserved. http://www.sitewhere.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package com.sitewhere.spi.microservice.logging;

import java.util.UUID;

import com.sitewhere.spi.microservice.MicroserviceIdentifier;

/**
 * Log message that includes metadata about microservice that generated it.
 * 
 * @author Derek
 */
public interface IMicroserviceLogMessage {

    /**
     * Get identifier for microservice type that generated the message.
     * 
     * @return
     */
    public MicroserviceIdentifier getMicroserviceIdentifier();

    /**
     * Get unique container id for microservice instance.
     * 
     * @return
     */
    public String getMicroserviceContainerId();

    /**
     * Get tenant id if applicable.
     * 
     * @return
     */
    public UUID getTenantId();

    /**
     * Get timestamp value.
     * 
     * @return
     */
    public long getTimestamp();

    /**
     * Get log level for message.
     * 
     * @return
     */
    public LogLevel getLogLevel();

    /**
     * Get message content text.
     * 
     * @return
     */
    public String getMessageText();

    /**
     * Get exception information if available.
     * 
     * @return
     */
    public ILoggedException getException();
}