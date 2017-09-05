/*
 * Copyright (c) SiteWhere, LLC. All rights reserved. http://www.sitewhere.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package com.sitewhere.rest.model.device.command;

import com.sitewhere.spi.device.command.IRegistrationAckCommand;
import com.sitewhere.spi.device.command.RegistrationSuccessReason;
import com.sitewhere.spi.device.command.SystemCommandType;

/**
 * Default implementation of {@link IRegistrationAckCommand}.
 * 
 * @author Derek
 */
public class RegistrationAckCommand extends SystemCommand implements IRegistrationAckCommand {

    /** Serial version UID */
    private static final long serialVersionUID = 1831724152286696862L;

    /** Success reason */
    private RegistrationSuccessReason reason;

    public RegistrationAckCommand() {
	super(SystemCommandType.RegistrationAck);
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.sitewhere.spi.device.command.IRegistrationAckCommand#getReason()
     */
    public RegistrationSuccessReason getReason() {
	return reason;
    }

    public void setReason(RegistrationSuccessReason reason) {
	this.reason = reason;
    }
}