/*
 * $Id$
 * --------------------------------------------------------------------------------------
 * Copyright (c) Reveal Technologies, LLC. All rights reserved. http://www.reveal-tech.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */

package com.sitewhere.spi.device;

import com.sitewhere.spi.common.IMetadataProviderEntity;

/**
 * A construct that groups together related device assignments and provides common
 * settings for them.
 * 
 * @author Derek
 */
public interface ISite extends IMetadataProviderEntity {

	/**
	 * Get unique token.
	 * 
	 * @return
	 */
	public String getToken();

	/**
	 * Get the site name.
	 * 
	 * @return
	 */
	public String getName();

	/**
	 * Get the description.
	 * 
	 * @return
	 */
	public String getDescription();

	/**
	 * Get the image URL.
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