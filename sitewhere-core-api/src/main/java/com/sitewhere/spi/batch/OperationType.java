/*
 * Copyright (c) SiteWhere, LLC. All rights reserved. http://www.sitewhere.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package com.sitewhere.spi.batch;

/**
 * Indicates the type of operation being performed in an
 * {@link IBatchOperation}.
 * 
 * @author Derek
 */
public enum OperationType {

    /** Operation invokes a command on multiple devices */
    InvokeCommand("Batch Command Invocation"),

    /** Operation updates firmware on multiple devices */
    UpdateFirmware("Batch Firmware Update");

    /** Operation description */
    private String description;

    private OperationType(String description) {
	this.description = description;
    }

    public String getDescription() {
	return description;
    }

    public void setDescription(String description) {
	this.description = description;
    }
}