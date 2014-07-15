/*
 * Version.java 
 * --------------------------------------------------------------------------------------
 * Copyright (c) Reveal Technologies, LLC. All rights reserved. http://www.reveal-tech.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package com.sitewhere.rest.model.system;

import com.sitewhere.spi.system.IVersion;

/**
 * Model object used to read version information from REST call.
 * 
 * @author Derek
 */
public class Version implements IVersion {

	/** Full edition */
	private String edition;

	/** Edition identifier */
	private String editionIdentifier;

	/** Version identifier */
	private String versionIdentifier;

	/** Build timestamp */
	private String buildTimestamp;

	public Version() {
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.sitewhere.spi.system.IVersion#getEdition()
	 */
	public String getEdition() {
		return edition;
	}

	public void setEdition(String edition) {
		this.edition = edition;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.sitewhere.spi.system.IVersion#getEditionIdentifier()
	 */
	public String getEditionIdentifier() {
		return editionIdentifier;
	}

	public void setEditionIdentifier(String editionIdentifier) {
		this.editionIdentifier = editionIdentifier;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.sitewhere.spi.system.IVersion#getVersionIdentifier()
	 */
	public String getVersionIdentifier() {
		return versionIdentifier;
	}

	public void setVersionIdentifier(String versionIdentifier) {
		this.versionIdentifier = versionIdentifier;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.sitewhere.spi.system.IVersion#getBuildTimestamp()
	 */
	public String getBuildTimestamp() {
		return buildTimestamp;
	}

	public void setBuildTimestamp(String buildTimestamp) {
		this.buildTimestamp = buildTimestamp;
	}
}