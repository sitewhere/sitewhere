/*
 * DeviceSlot.java 
 * --------------------------------------------------------------------------------------
 * Copyright (c) Reveal Technologies, LLC. All rights reserved. http://www.reveal-tech.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package com.sitewhere.rest.model.device.element;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.sitewhere.spi.device.element.IDeviceSlot;

/**
 * Default implementation of {@link IDeviceSlot}.
 * 
 * @author Derek
 */
@JsonInclude(Include.NON_NULL)
public class DeviceSlot extends DeviceElement implements IDeviceSlot {

	public DeviceSlot() {
	}
}