/*
 * Copyright (c) SiteWhere, LLC. All rights reserved. http://www.sitewhere.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package com.sitewhere.spi.command;

import java.io.Serializable;

/**
 * Response for a command issued to the server.
 * 
 * @author dadams *
 */
public interface ICommandResponse extends Serializable {

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
