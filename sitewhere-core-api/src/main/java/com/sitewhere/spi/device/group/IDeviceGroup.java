/*
 * Copyright (c) SiteWhere, LLC. All rights reserved. http://www.sitewhere.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package com.sitewhere.spi.device.group;

import java.util.List;

import com.sitewhere.spi.common.IAccessible;
import com.sitewhere.spi.common.IImageProvider;
import com.sitewhere.spi.common.ISiteWhereEntity;

/**
 * Interface for a group of related devices.
 * 
 * @author Derek
 */
public interface IDeviceGroup extends ISiteWhereEntity, IAccessible, IImageProvider {

    /**
     * Get list of roles associated with element.
     * 
     * @return
     */
    public List<String> getRoles();
}