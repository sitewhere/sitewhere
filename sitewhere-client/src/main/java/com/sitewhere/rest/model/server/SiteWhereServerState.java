/*
 * Copyright (c) SiteWhere, LLC. All rights reserved. http://www.sitewhere.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package com.sitewhere.rest.model.server;

import java.util.List;

import com.sitewhere.spi.server.ISiteWhereServerState;

/**
 * Implementation of {@link ISiteWhereServerState} for holding information about a running
 * SiteWhere server.
 * 
 * @author Derek
 */
public class SiteWhereServerState implements ISiteWhereServerState {

	/** General server information */
	private IGeneralInformation general;

	/** Information about JVM */
	private IJavaInformation java;

	public IGeneralInformation getGeneral() {
		return general;
	}

	public void setGeneral(IGeneralInformation general) {
		this.general = general;
	}

	public IJavaInformation getJava() {
		return java;
	}

	public void setJava(IJavaInformation java) {
		this.java = java;
	}

	public static class GeneralInformation implements IGeneralInformation {

		public String edition;

		public String editionIdentifier;

		public String versionIdentifier;

		public String buildTimestamp;

		public Long uptime;

		public String operatingSystemName;

		public String operatingSystemVersion;

		public String getEdition() {
			return edition;
		}

		public void setEdition(String edition) {
			this.edition = edition;
		}

		public String getEditionIdentifier() {
			return editionIdentifier;
		}

		public void setEditionIdentifier(String editionIdentifier) {
			this.editionIdentifier = editionIdentifier;
		}

		public String getVersionIdentifier() {
			return versionIdentifier;
		}

		public void setVersionIdentifier(String versionIdentifier) {
			this.versionIdentifier = versionIdentifier;
		}

		public String getBuildTimestamp() {
			return buildTimestamp;
		}

		public void setBuildTimestamp(String buildTimestamp) {
			this.buildTimestamp = buildTimestamp;
		}

		public Long getUptime() {
			return uptime;
		}

		public void setUptime(Long uptime) {
			this.uptime = uptime;
		}

		public String getOperatingSystemName() {
			return operatingSystemName;
		}

		public void setOperatingSystemName(String operatingSystemName) {
			this.operatingSystemName = operatingSystemName;
		}

		public String getOperatingSystemVersion() {
			return operatingSystemVersion;
		}

		public void setOperatingSystemVersion(String operatingSystemVersion) {
			this.operatingSystemVersion = operatingSystemVersion;
		}
	}

	public static class JavaInformation implements IJavaInformation {

		private String jvmVendor;

		private String jvmVersion;

		private Long jvmFreeMemory;

		private List<Long> jvmFreeMemoryHistory;

		private Long jvmTotalMemory;

		private List<Long> jvmTotalMemoryHistory;

		private Long jvmMaxMemory;

		public String getJvmVendor() {
			return jvmVendor;
		}

		public void setJvmVendor(String jvmVendor) {
			this.jvmVendor = jvmVendor;
		}

		public String getJvmVersion() {
			return jvmVersion;
		}

		public void setJvmVersion(String jvmVersion) {
			this.jvmVersion = jvmVersion;
		}

		public Long getJvmFreeMemory() {
			return jvmFreeMemory;
		}

		public void setJvmFreeMemory(Long jvmFreeMemory) {
			this.jvmFreeMemory = jvmFreeMemory;
		}

		public List<Long> getJvmFreeMemoryHistory() {
			return jvmFreeMemoryHistory;
		}

		public void setJvmFreeMemoryHistory(List<Long> jvmFreeMemoryHistory) {
			this.jvmFreeMemoryHistory = jvmFreeMemoryHistory;
		}

		public Long getJvmTotalMemory() {
			return jvmTotalMemory;
		}

		public void setJvmTotalMemory(Long jvmTotalMemory) {
			this.jvmTotalMemory = jvmTotalMemory;
		}

		public List<Long> getJvmTotalMemoryHistory() {
			return jvmTotalMemoryHistory;
		}

		public void setJvmTotalMemoryHistory(List<Long> jvmTotalMemoryHistory) {
			this.jvmTotalMemoryHistory = jvmTotalMemoryHistory;
		}

		public Long getJvmMaxMemory() {
			return jvmMaxMemory;
		}

		public void setJvmMaxMemory(Long jvmMaxMemory) {
			this.jvmMaxMemory = jvmMaxMemory;
		}
	}
}