/*
 * Copyright (c) SiteWhere, LLC. All rights reserved. http://www.sitewhere.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package com.sitewhere.version;

import com.sitewhere.spi.SiteWhereException;
import com.sitewhere.spi.system.IVersion;

/**
 * Used to access implementation class which is only included after Maven build.
 * 
 * @author Derek
 */
public class VersionHelper {

	/** Classname for Community Edition version information */
	private static final String CE_CLASSNAME = "com.sitewhere.Version";

	/** Classname for Enterprise Edition version information */
	private static final String EE_CLASSNAME = "com.sitewhere.ee.Version";

	/**
	 * Hacky method of accessing a version file that is created at build time.
	 * 
	 * @return
	 * @throws SiteWhereException
	 */
	public static IVersion getVersion() {
		Class<?> clazz;
		try {
			clazz = Class.forName(EE_CLASSNAME);
		} catch (ClassNotFoundException e) {
			try {
				clazz = Class.forName(CE_CLASSNAME);
			} catch (ClassNotFoundException ex) {
				throw new RuntimeException("Neither EE or CE version information was found on classpath.");
			}
		}
		try {
			IVersion version = (IVersion) clazz.newInstance();
			return version;
		} catch (InstantiationException e) {
			throw new RuntimeException(e);
		} catch (IllegalAccessException e) {
			throw new RuntimeException(e);
		}
	}
}