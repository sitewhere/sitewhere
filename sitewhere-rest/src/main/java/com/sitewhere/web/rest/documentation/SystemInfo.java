/*
 * Copyright (c) SiteWhere, LLC. All rights reserved. http://www.sitewhere.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package com.sitewhere.web.rest.documentation;

import com.sitewhere.rest.model.server.SiteWhereServerRuntime;
import com.sitewhere.rest.model.server.SiteWhereServerRuntime.GeneralInformation;
import com.sitewhere.rest.model.server.SiteWhereServerRuntime.JavaInformation;
import com.sitewhere.rest.model.system.Version;
import com.sitewhere.spi.SiteWhereException;

/**
 * Examples of REST payloads for various system methods.
 * 
 * @author Derek
 */
public class SystemInfo {

	public static class GetVersionResponse {

		public Object generate() throws SiteWhereException {
			Version version = new Version();
			version.setVersionIdentifier("1.3.0");
			version.setEdition("Community Edition");
			version.setEditionIdentifier("CE");
			version.setBuildTimestamp("20151231120000");
			return version;
		}
	}

	public static class GetServerRuntimeResponse {

		public Object generate() throws SiteWhereException {
			SiteWhereServerRuntime state = new SiteWhereServerRuntime();

			GeneralInformation general = new GeneralInformation();
			general.setVersionIdentifier("1.3.0");
			general.setEdition("Community Edition");
			general.setEditionIdentifier("CE");
			general.setBuildTimestamp("20151231120000");
			general.setOperatingSystemName("Windows 8.1");
			general.setOperatingSystemVersion("6.3");
			general.setUptime((long) 107297);
			state.setGeneral(general);

			JavaInformation java = new JavaInformation();
			java.setJvmVendor("Oracle Corporation");
			java.setJvmVersion("1.7.0_79");
			java.setJvmTotalMemory((long) 576192512);
			java.setJvmFreeMemory((long) 275942376);
			java.setJvmMaxMemory((long) 954728448);
			state.setJava(java);
			return state;
		}
	}
}