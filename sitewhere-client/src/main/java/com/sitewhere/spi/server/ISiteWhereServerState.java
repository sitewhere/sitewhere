/*
 * Copyright (c) SiteWhere, LLC. All rights reserved. http://www.sitewhere.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package com.sitewhere.spi.server;

/**
 * Holds information about current state of SiteWhere server.
 * 
 * @author Derek
 */
public interface ISiteWhereServerState {

	public IGeneralInformation getGeneral();

	public IJavaInformation getJava();

	/**
	 * General server information.
	 */
	public static interface IGeneralInformation {

		/**
		 * Get full edition name.
		 */
		public String getEdition();

		/**
		 * Get the short identifier for edition.
		 * 
		 * @return
		 */
		public String getEditionIdentifier();

		/**
		 * Gets the Maven version identifier.
		 * 
		 * @return
		 */
		public String getVersionIdentifier();

		/**
		 * Gets the build timestamp.
		 * 
		 * @return
		 */
		public String getBuildTimestamp();

		/**
		 * Get the amount of time in milliseconds the server has been up.
		 * 
		 * @return
		 */
		public Long getUptime();

		/**
		 * Get operating system name.
		 * 
		 * @return
		 */
		public String getOperatingSystemName();

		/**
		 * Get operating system version.
		 * 
		 * @return
		 */
		public String getOperatingSystemVersion();
	}

	/**
	 * Get information about Java VM.
	 */
	public static interface IJavaInformation {

		/**
		 * Get the JVM vendor.
		 * 
		 * @return
		 */
		public String getJvmVendor();

		/**
		 * Get the JVM version.
		 * 
		 * @return
		 */
		public String getJvmVersion();

		public Long getJvmTotalMemory();

		public Long getJvmFreeMemory();

		public Long getJvmMaxMemory();
	}
}