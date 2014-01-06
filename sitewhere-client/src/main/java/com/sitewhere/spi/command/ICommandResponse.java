/*
* $Id$
* --------------------------------------------------------------------------------------
* Copyright (c) Reveal Technologies, LLC. All rights reserved. http://www.reveal-tech.com
*
* The software in this package is published under the terms of the CPAL v1.0
* license, a copy of which has been included with this distribution in the
* LICENSE.txt file.
*/

package com.sitewhere.spi.command;

/**
 * Response for a command issued to the server.
 * 
 * @author dadams *
 */
public interface ICommandResponse {

	/**
	 * Get the command result.
	 * 
	 * @return
	 */
	public CommandResult getResult();

	/**
	 * Get a detail message for the result.
	 * 
	 * @return
	 */
	public String getMessage();
}
