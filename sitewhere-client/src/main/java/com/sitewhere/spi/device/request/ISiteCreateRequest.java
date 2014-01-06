/*
 * ISiteCreateRequest.java 
 * --------------------------------------------------------------------------------------
 * Copyright (c) Reveal Technologies, LLC. All rights reserved. http://www.reveal-tech.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package com.sitewhere.spi.device.request;

import com.sitewhere.spi.common.IMetadataProvider;
import com.sitewhere.spi.device.ISiteMapData;

/**
 * Interface for arguments needed to create a site.
 * 
 * @author Derek
 */
public interface ISiteCreateRequest extends IMetadataProvider {

	/**
	 * Get site name.
	 * 
	 * @return
	 */
	public String getName();

	/**
	 * Get site description.
	 * 
	 * @return
	 */
	public String getDescription();

	/**
	 * Get URL for site logo image.
	 * 
	 * @return
	 */
	public String getImageUrl();

	/**
	 * Get map information.
	 * 
	 * @return
	 */
	public ISiteMapData getMap();
}