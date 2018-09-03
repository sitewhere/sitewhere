/*
 * Copyright (c) SiteWhere, LLC. All rights reserved. http://www.sitewhere.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package com.sitewhere.spi.device.event;

import com.sitewhere.spi.common.IAccessible;
import com.sitewhere.spi.common.IColorProvider;
import com.sitewhere.spi.common.IIconProvider;
import com.sitewhere.spi.common.IPersistentEntity;

/**
 * Provides extra metadata about measurement events that have a common function.
 * 
 * @author Derek
 */
public interface IDeviceMeasurementClassifier extends IPersistentEntity, IAccessible, IColorProvider, IIconProvider {

    /**
     * Get unit associated with measurement.
     * 
     * @return
     */
    public String getUnit();
}