/*
 * NetworksController.java 
 * --------------------------------------------------------------------------------------
 * Copyright (c) Reveal Technologies, LLC. All rights reserved. http://www.reveal-tech.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package com.sitewhere.web.rest.controllers;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.sitewhere.rest.model.device.group.DeviceGroup;
import com.sitewhere.rest.model.device.request.DeviceGroupCreateRequest;
import com.sitewhere.server.SiteWhereServer;
import com.sitewhere.spi.SiteWhereException;
import com.sitewhere.spi.device.group.IDeviceGroup;
import com.wordnik.swagger.annotations.Api;
import com.wordnik.swagger.annotations.ApiOperation;

/**
 * Controller for device group operations.
 * 
 * @author Derek Adams
 */
@Controller
@RequestMapping(value = "/devicegroups")
@Api(value = "", description = "Operations related to SiteWhere device groups.")
public class DeviceGroupsController extends SiteWhereController {

	/**
	 * Create a device group.
	 * 
	 * @param request
	 * @return
	 */
	@RequestMapping(method = RequestMethod.POST)
	@ResponseBody
	@ApiOperation(value = "Create a new device group")
	public IDeviceGroup createDeviceNetwork(@RequestBody DeviceGroupCreateRequest request)
			throws SiteWhereException {
		IDeviceGroup result = SiteWhereServer.getInstance().getDeviceManagement().createDeviceGroup(request);
		return DeviceGroup.copy(result);
	}
}