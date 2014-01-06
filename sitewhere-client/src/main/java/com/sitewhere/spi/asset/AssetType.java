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

/**
 * Indicates the general type of an asset.
 * 
 * @author dadams
 */
public enum AssetType {

	/** Device asset (hardware used for monitoring) */
	Device,

	/** Asset is a person */
	Person,

	/** Asset is a piece of hardware */
	Hardware;
}