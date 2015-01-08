/*
 * Copyright (c) SiteWhere, LLC. All rights reserved. http://www.sitewhere.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package com.sitewhere.spi.system;

/**
 * System components that may be managed via REST services.
 * 
 * @author Derek
 */
public enum SystemComponents {

	/** Controls all provisioning components */
	DeviceProvisioning("provisioning"),

	/** Controls the batch operation manager */
	BatchOperationManager("batch");

	/** Short name */
	private String shortName;

	private SystemComponents(String shortName) {
		this.shortName = shortName;
	}

	public static SystemComponents getByShortName(String shortName) {
		for (SystemComponents value : SystemComponents.values()) {
			if (value.getShortName().equalsIgnoreCase(shortName)) {
				return value;
			}
		}
		return null;
	}

	public String getShortName() {
		return shortName;
	}

	public void setShortName(String shortName) {
		this.shortName = shortName;
	}
}