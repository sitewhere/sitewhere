/*
 * Copyright (c) SiteWhere, LLC. All rights reserved. http://www.sitewhere.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package com.sitewhere.spi.device;

import java.util.Map;

import com.sitewhere.spi.SiteWhereException;

/**
 * Provides simple methods for invoking commonly used SiteWhere actions.
 * 
 * @author Derek
 */
public interface IDeviceActions {

    /**
     * Create a new location for the given assignment.
     * 
     * @param assignmentToken
     * @param latitude
     * @param longitude
     * @param elevation
     * @param updateState
     * @throws SiteWhereException
     */
    public void createLocation(String assignmentToken, double latitude, double longitude, double elevation,
	    boolean updateState) throws SiteWhereException;

    /**
     * Send command to a device.
     * 
     * @param assignmentToken
     * @param command
     * @param parameters
     * @throws SiteWhereException
     */
    public void sendCommand(String assignmentToken, String command, Map<String, String> parameters)
	    throws SiteWhereException;
}