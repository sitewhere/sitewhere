/*
 * Copyright (c) SiteWhere, LLC. All rights reserved. http://www.sitewhere.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package com.sitewhere.rest.model.device.command;

import com.sitewhere.spi.device.command.IRegistrationFailureCommand;
import com.sitewhere.spi.device.command.RegistrationFailureReason;
import com.sitewhere.spi.device.command.SystemCommandType;

/**
 * Default implementation of {@link IRegistrationFailureCommand}.
 * 
 * @author Derek
 */
public class RegistrationFailureCommand extends SystemCommand implements IRegistrationFailureCommand {

    /** Serial version UID */
    private static final long serialVersionUID = -2141636538202966306L;

    /** Failure reason */
    private RegistrationFailureReason reason;

    /** Error message */
    private String errorMessage;

    public RegistrationFailureCommand() {
	super(SystemCommandType.RegistrationFailure);
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * com.sitewhere.spi.device.command.IRegistrationFailureCommand#getReason()
     */
    public RegistrationFailureReason getReason() {
	return reason;
    }

    public void setReason(RegistrationFailureReason reason) {
	this.reason = reason;
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.sitewhere.spi.device.command.IRegistrationFailureCommand#
     * getErrorMessage()
     */
    public String getErrorMessage() {
	return errorMessage;
    }

    public void setErrorMessage(String errorMessage) {
	this.errorMessage = errorMessage;
    }
}