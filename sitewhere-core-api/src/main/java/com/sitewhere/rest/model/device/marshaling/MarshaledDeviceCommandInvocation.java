/*
 * Copyright (c) SiteWhere, LLC. All rights reserved. http://www.sitewhere.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package com.sitewhere.rest.model.device.marshaling;

import com.sitewhere.rest.model.device.command.DeviceCommand;
import com.sitewhere.rest.model.device.event.DeviceCommandInvocation;

/**
 * Extends {@link DeviceCommandInvocation} to support fields that can be
 * included on REST calls.
 * 
 * @author Derek
 */
public class MarshaledDeviceCommandInvocation extends DeviceCommandInvocation {

    /** Serial version UID */
    private static final long serialVersionUID = 2111536821151803479L;

    /** Command that was invoked */
    private DeviceCommand command;

    /** HTML representation of invocation */
    private String asHtml;

    public DeviceCommand getCommand() {
	return command;
    }

    public void setCommand(DeviceCommand command) {
	this.command = command;
    }

    public String getAsHtml() {
	return asHtml;
    }

    public void setAsHtml(String asHtml) {
	this.asHtml = asHtml;
    }
}