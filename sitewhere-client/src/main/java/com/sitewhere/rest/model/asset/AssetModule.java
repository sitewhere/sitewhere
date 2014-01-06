/*
* $Id$
* --------------------------------------------------------------------------------------
* Copyright (c) Reveal Technologies, LLC. All rights reserved. http://www.reveal-tech.com
*
* The software in this package is published under the terms of the CPAL v1.0
* license, a copy of which has been included with this distribution in the
* LICENSE.txt file.
*/

package com.sitewhere.rest.model.asset;

import com.sitewhere.spi.asset.IAssetModule;

/**
 * Model object used to show asset module information externally.
 * 
 * @author dadams
 */
public class AssetModule {

	/** Module id */
	private String id;

	/** Module name */
	private String name;

	public AssetModule() {
	}

	public AssetModule(String id, String name) {
		this.id = id;
		this.name = name;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	/**
	 * Create a copy of an SPI object. Used by web services for marshaling.
	 * 
	 * @param input
	 * @return
	 */
	public static AssetModule copy(IAssetModule<?> input) {
		AssetModule module = new AssetModule();
		module.setId(input.getId());
		module.setName(input.getName());
		return module;
	}
}