/*
 * VersionHelper.java 
 * --------------------------------------------------------------------------------------
 * Copyright (c) Reveal Technologies, LLC. All rights reserved. http://www.reveal-tech.com
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

	/**
	 * Hacky method of accessing a version file that is created at build time.
	 * 
	 * @return
	 * @throws SiteWhereException
	 */
	public static IVersion getVersion() {
		try {
			Class<?> clazz = Class.forName("com.sitewhere.Version");
			IVersion version = (IVersion) clazz.newInstance();
			return version;
		} catch (ClassNotFoundException e) {
			throw new RuntimeException(e);
		} catch (InstantiationException e) {
			throw new RuntimeException(e);
		} catch (IllegalAccessException e) {
			throw new RuntimeException(e);
		}
	}
}