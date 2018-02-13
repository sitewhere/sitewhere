/*
 * Copyright (c) SiteWhere, LLC. All rights reserved. http://www.sitewhere.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package com.sitewhere.spi.device.symbology;

import java.net.URI;

import com.sitewhere.spi.SiteWhereException;
import com.sitewhere.spi.area.IArea;
import com.sitewhere.spi.device.IDevice;
import com.sitewhere.spi.device.IDeviceAssignment;
import com.sitewhere.spi.device.IDeviceType;

/**
 * Translates a SiteWhere entity into a unique URL that identifies it.
 * 
 * @author Derek
 */
public interface IEntityUriProvider {

    /**
     * Get unique identifier for an area.
     * 
     * @param area
     * @return
     * @throws SiteWhereException
     */
    public URI getAreaIdentifier(IArea area) throws SiteWhereException;

    /**
     * Get unique identifier for a device type.
     * 
     * @param deviceType
     * @return
     * @throws SiteWhereException
     */
    public URI getDeviceTypeIdentifier(IDeviceType deviceType) throws SiteWhereException;

    /**
     * Get unique identifier for a device.
     * 
     * @param device
     * @return
     * @throws SiteWhereException
     */
    public URI getDeviceIdentifier(IDevice device) throws SiteWhereException;

    /**
     * Get unique identifier for a device assignment.
     * 
     * @param assignment
     * @return
     * @throws SiteWhereException
     */
    public URI getDeviceAssignmentIdentifier(IDeviceAssignment assignment) throws SiteWhereException;
}