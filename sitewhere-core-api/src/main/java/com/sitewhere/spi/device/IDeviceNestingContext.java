/*
 * Copyright (c) SiteWhere, LLC. All rights reserved. http://www.sitewhere.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package com.sitewhere.spi.device;

import com.sitewhere.spi.device.element.IDeviceElementSchema;

/**
 * Provides context information for addressing a nested device.
 * 
 * @author Derek
 */
public interface IDeviceNestingContext {

    /**
     * Get gateway device that should be contacted.
     * 
     * @return
     */
    public IDevice getGateway();

    /**
     * Get nested device being addressed. In standalone devices, this value will
     * be null.
     * 
     * @return
     */
    public IDevice getNested();

    /**
     * Get path in parent device {@link IDeviceElementSchema} mapped to target
     * device.
     * 
     * @return
     */
    public String getPath();
}