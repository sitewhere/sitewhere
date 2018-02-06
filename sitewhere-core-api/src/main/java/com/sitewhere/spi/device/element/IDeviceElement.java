/*
 * Copyright (c) SiteWhere, LLC. All rights reserved. http://www.sitewhere.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package com.sitewhere.spi.device.element;

/**
 * Common base class for elements in the {@link IDeviceElementSchema} hierarchy.
 * 
 * @author Derek
 */
public interface IDeviceElement {

    /**
     * Get human-readable name for element.
     * 
     * @return
     */
    public String getName();

    /**
     * Get relative path to element from parent.
     * 
     * @return
     */
    public String getPath();
}