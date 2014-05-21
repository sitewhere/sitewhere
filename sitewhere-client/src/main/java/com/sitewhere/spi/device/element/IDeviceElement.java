/*
 * IDeviceElement.java 
 * --------------------------------------------------------------------------------------
 * Copyright (c) Reveal Technologies, LLC. All rights reserved. http://www.reveal-tech.com
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
	 * Get relative path to element from parent.
	 * 
	 * @return
	 */
	public String getPath();
}