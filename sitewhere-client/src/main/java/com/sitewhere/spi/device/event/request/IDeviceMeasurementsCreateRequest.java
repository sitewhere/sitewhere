/*
 * Copyright (c) SiteWhere, LLC. All rights reserved. http://www.sitewhere.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package com.sitewhere.spi.device.event.request;

import com.sitewhere.spi.device.event.IMeasurementsProvider;

/**
 * Interface for arguments needed to create device measurements.
 * 
 * @author Derek
 */
public interface IDeviceMeasurementsCreateRequest extends IDeviceEventCreateRequest, IMeasurementsProvider {
}