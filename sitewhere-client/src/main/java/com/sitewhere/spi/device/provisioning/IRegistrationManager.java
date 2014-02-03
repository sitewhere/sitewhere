/*
 * IRegistrationManager.java 
 * --------------------------------------------------------------------------------------
 * Copyright (c) Reveal Technologies, LLC. All rights reserved. http://www.reveal-tech.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package com.sitewhere.spi.device.provisioning;

import com.sitewhere.spi.ISiteWhereLifecycle;
import com.sitewhere.spi.SiteWhereException;
import com.sitewhere.spi.device.event.request.IDeviceRegistrationRequest;

/**
 * Manages how devices are dynamically registered into the system.
 * 
 * @author Derek
 */
public interface IRegistrationManager extends ISiteWhereLifecycle {

	/**
	 * Indicates whether the registration manager allows new devices to be created as a
	 * byproduct of the registration process.
	 * 
	 * @return
	 */
	public boolean isAllowNewDevices();

	/**
	 * Indicates whether all new regsitrations can be automatically assigned to a given
	 * site. If not, the site token must be passed as part of the registration payload.
	 * 
	 * @return
	 */
	public boolean isAutoAssignSite();

	/**
	 * Gets the token used for automatic site assignment. This only applies when 'auto
	 * assign site' is set to true.
	 * 
	 * @return
	 */
	public String getAutoAssignSiteToken();

	/**
	 * Handle registration of a new device.
	 * 
	 * @param request
	 * @throws SiteWhereException
	 */
	public void handleDeviceRegistration(IDeviceRegistrationRequest request) throws SiteWhereException;
}