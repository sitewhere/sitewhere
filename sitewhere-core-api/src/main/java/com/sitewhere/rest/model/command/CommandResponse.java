/*
 * Copyright (c) SiteWhere, LLC. All rights reserved. http://www.sitewhere.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package com.sitewhere.rest.model.command;

import com.sitewhere.spi.command.CommandResult;
import com.sitewhere.spi.command.ICommandResponse;

/**
 * Model object for a command response.
 * 
 * @author dadams
 */
public class CommandResponse implements ICommandResponse {

    /** Serial version UID */
    private static final long serialVersionUID = 2189097956869085792L;

    /** Command result */
    private CommandResult result;

    /** Detail message */
    private String message;

    public CommandResponse() {
    }

    public CommandResponse(CommandResult result, String message) {
	this.result = result;
	this.message = message;
    }

    public CommandResult getResult() {
	return result;
    }

    public void setResult(CommandResult result) {
	this.result = result;
    }

    public String getMessage() {
	return message;
    }

    public void setMessage(String message) {
	this.message = message;
    }

    /**
     * Copy an SPI object to one that can marshaled.
     * 
     * @param input
     * @return
     */
    public static CommandResponse copy(ICommandResponse input) {
	CommandResponse response = new CommandResponse();
	response.setMessage(input.getMessage());
	response.setResult(input.getResult());
	return response;
    }
}