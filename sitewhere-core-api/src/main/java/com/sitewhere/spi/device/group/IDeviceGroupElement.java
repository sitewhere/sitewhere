/*
 * Copyright (c) SiteWhere, LLC. All rights reserved. http://www.sitewhere.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package com.sitewhere.spi.device.group;

import java.util.List;

/**
 * Interface for an entry in an {@link IDeviceGroup}.
 * 
 * @author Derek
 */
public interface IDeviceGroupElement {

	/**
	 * Get token for parent group.
	 * 
	 * @return
	 */
	public String getGroupToken();

	/**
	 * Get index that corresponds to this entry.
	 * 
	 * @return
	 */
	public long getIndex();

	/**
	 * Get group element type.
	 * 
	 * @return
	 */
	public GroupElementType getType();

	/**
	 * Get element id (relative to element type).
	 * 
	 * @return
	 */
	public String getElementId();

	/**
	 * Get list of roles associated with element.
	 * 
	 * @return
	 */
	public List<String> getRoles();
}