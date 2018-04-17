/*
 * Copyright (c) SiteWhere, LLC. All rights reserved. http://www.sitewhere.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package com.sitewhere.rest.model.microservice.logging;

import java.util.UUID;

import com.sitewhere.spi.microservice.MicroserviceIdentifier;
import com.sitewhere.spi.microservice.logging.ILoggedException;
import com.sitewhere.spi.microservice.logging.IMicroserviceLogMessage;
import com.sitewhere.spi.microservice.logging.LogLevel;

/**
 * Log message that includes metadata about microservice that generated it.
 * 
 * @author Derek
 */
public class MicroserviceLogMessage implements IMicroserviceLogMessage {

    /** Microservice identifier */
    private MicroserviceIdentifier microserviceIdentifier;

    /** Microservice container id */
    private String microserviceContainerId;

    /** Tenant id */
    private UUID tenantId;

    /** Timestamp */
    private long timestamp;

    /** Log level */
    private LogLevel logLevel;

    /** Message text */
    private String messageText;

    /** Exception */
    private ILoggedException exception;

    /*
     * @see com.sitewhere.spi.microservice.logging.IMicroserviceLogMessage#
     * getMicroserviceIdentifier()
     */
    @Override
    public MicroserviceIdentifier getMicroserviceIdentifier() {
	return microserviceIdentifier;
    }

    public void setMicroserviceIdentifier(MicroserviceIdentifier microserviceIdentifier) {
	this.microserviceIdentifier = microserviceIdentifier;
    }

    /*
     * @see com.sitewhere.spi.microservice.logging.IMicroserviceLogMessage#
     * getMicroserviceContainerId()
     */
    @Override
    public String getMicroserviceContainerId() {
	return microserviceContainerId;
    }

    public void setMicroserviceContainerId(String microserviceContainerId) {
	this.microserviceContainerId = microserviceContainerId;
    }

    /*
     * @see
     * com.sitewhere.spi.microservice.logging.IMicroserviceLogMessage#getTenantId()
     */
    @Override
    public UUID getTenantId() {
	return tenantId;
    }

    public void setTenantId(UUID tenantId) {
	this.tenantId = tenantId;
    }

    /*
     * @see
     * com.sitewhere.spi.microservice.logging.IMicroserviceLogMessage#getTimestamp()
     */
    @Override
    public long getTimestamp() {
	return timestamp;
    }

    public void setTimestamp(long timestamp) {
	this.timestamp = timestamp;
    }

    /*
     * @see
     * com.sitewhere.spi.microservice.logging.IMicroserviceLogMessage#getLogLevel()
     */
    @Override
    public LogLevel getLogLevel() {
	return logLevel;
    }

    public void setLogLevel(LogLevel logLevel) {
	this.logLevel = logLevel;
    }

    /*
     * @see
     * com.sitewhere.spi.microservice.logging.IMicroserviceLogMessage#getMessageText
     * ()
     */
    @Override
    public String getMessageText() {
	return messageText;
    }

    public void setMessageText(String messageText) {
	this.messageText = messageText;
    }

    /*
     * @see
     * com.sitewhere.spi.microservice.logging.IMicroserviceLogMessage#getException()
     */
    @Override
    public ILoggedException getException() {
	return exception;
    }

    public void setException(ILoggedException exception) {
	this.exception = exception;
    }
}
