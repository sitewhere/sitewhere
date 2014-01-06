/*
 * $Id$
 * --------------------------------------------------------------------------------------
 * Copyright (c) Reveal Technologies, LLC. All rights reserved. http://www.reveal-tech.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */

package com.sitewhere.spi.asset;

import java.util.Set;

/**
 * Unique item to which a device may be associated.
 * 
 * @author dadams
 */
public interface IAsset extends Comparable<IAsset> {

	/**
	 * Unique asset id.
	 * 
	 * @return
	 */
	public String getId();

	/**
	 * Get asset name.
	 * 
	 * @return
	 */
	public String getName();

	/**
	 * Get the asset type indicator.
	 * 
	 * @return
	 */
	public AssetType getType();

	/**
	 * Get list of property names.
	 * 
	 * @return
	 */
	public Set<String> getPropertyNames();

	/**
	 * Add a property. If there is an existing property with the same name, it is replaced.
	 * 
	 * @param property
	 */
	public void setProperty(String name, String value);

	/**
	 * Get an existing value by name.
	 * 
	 * @param name
	 * @return
	 */
	public String getProperty(String name);
}