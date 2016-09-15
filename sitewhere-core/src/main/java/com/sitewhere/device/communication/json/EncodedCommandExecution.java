/*
 * Copyright (c) SiteWhere, LLC. All rights reserved. http://www.sitewhere.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package com.sitewhere.device.communication.json;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.sitewhere.spi.device.IDeviceAssignment;
import com.sitewhere.spi.device.IDeviceNestingContext;
import com.sitewhere.spi.device.command.IDeviceCommandExecution;
import com.sitewhere.spi.device.command.ISystemCommand;

/**
 * Wraps data sent when executing a command so it can be marshaled to JSON.
 * 
 * @author Derek
 */
@JsonInclude(Include.NON_NULL)
public class EncodedCommandExecution {

    /** Command information */
    private IDeviceCommandExecution command;

    /** System command information */
    private ISystemCommand systemCommand;

    /** Device nesting information */
    private IDeviceNestingContext nestingContext;

    /** Device assignment information */
    private IDeviceAssignment assignment;

    public EncodedCommandExecution(IDeviceCommandExecution command, IDeviceNestingContext nestingContext,
	    IDeviceAssignment assignment) {
	this.command = command;
	this.nestingContext = nestingContext;
	this.assignment = assignment;
    }

    public EncodedCommandExecution(ISystemCommand systemCommand, IDeviceNestingContext nestingContext,
	    IDeviceAssignment assignment) {
	this.systemCommand = systemCommand;
	this.nestingContext = nestingContext;
	this.assignment = assignment;
    }

    public IDeviceCommandExecution getCommand() {
	return command;
    }

    public void setCommand(IDeviceCommandExecution command) {
	this.command = command;
    }

    public ISystemCommand getSystemCommand() {
	return systemCommand;
    }

    public void setSystemCommand(ISystemCommand systemCommand) {
	this.systemCommand = systemCommand;
    }

    public IDeviceNestingContext getNestingContext() {
	return nestingContext;
    }

    public void setNestingContext(IDeviceNestingContext nestingContext) {
	this.nestingContext = nestingContext;
    }

    public IDeviceAssignment getAssignment() {
	return assignment;
    }

    public void setAssignment(IDeviceAssignment assignment) {
	this.assignment = assignment;
    }
}