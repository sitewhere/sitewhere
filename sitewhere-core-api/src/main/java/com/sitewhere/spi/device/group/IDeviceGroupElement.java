/*
 * Copyright (c) SiteWhere, LLC. All rights reserved. http://www.sitewhere.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package com.sitewhere.spi.device.group;

import java.util.List;
import java.util.UUID;

/**
 * Interface for an entry in an {@link IDeviceGroup}.
 * 
 * @author Derek
 */
public interface IDeviceGroupElement {

    /**
     * Get unique element id.
     * 
     * @return
     */
    public UUID getId();

    /**
     * Get id for parent group.
     * 
     * @return
     */
    public UUID getGroupId();

    /**
     * Get id of device (only one of device id or nested group id should be
     * specified).
     * 
     * @return
     */
    public UUID getDeviceId();

    /**
     * Get id of nested group (only one of device id or nested group id should be
     * specified).
     * 
     * @return
     */
    public UUID getNestedGroupId();

    /**
     * Get list of roles associated with element.
     * 
     * @return
     */
    public List<String> getRoles();
}