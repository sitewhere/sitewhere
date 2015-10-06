/*
 * Copyright (c) SiteWhere, LLC. All rights reserved. http://www.sitewhere.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package com.sitewhere.web.rest.documentation;

import com.sitewhere.rest.model.device.request.DeviceCreateRequest;
import com.sitewhere.spi.SiteWhereException;
import com.sitewhere.web.rest.documentation.ExampleData.Device_Tracker;

/**
 * Example of REST requests for interacting with devices.
 * 
 * @author Derek
 */
public class Devices {

	public static class CreateDeviceRequest {

		public Object generate() throws SiteWhereException {
			DeviceCreateRequest request = new DeviceCreateRequest();
			request.setSiteToken(ExampleData.SITE_CONSTRUCTION.getToken());
			request.setSpecificationToken(ExampleData.SPEC_MEITRACK.getToken());
			request.setHardwareId(ExampleData.TRACKER.getHardwareId());
			request.setComments(ExampleData.TRACKER.getComments());
			return request;
		}
	}

	public static class CreateDeviceResponse {

		public Object generate() throws SiteWhereException {
			return ExampleData.TRACKER;
		}
	}

	public static class GetDeviceByHardwareIdResponse {

		public Object generate() throws SiteWhereException {
			MockDeviceMarshalHelper helper = new MockDeviceMarshalHelper();
			helper.setIncludeSpecification(true);
			helper.setIncludeAsset(true);
			helper.setIncludeAssignment(true);
			helper.setIncludeSite(true);
			helper.setIncludeNested(true);
			return helper.convert(ExampleData.TRACKER, new MockAssetModuleManager());
		}
	}

	public static class UpdateDeviceRequest {

		public Object generate() throws SiteWhereException {
			DeviceCreateRequest request = new DeviceCreateRequest();
			request.setComments(ExampleData.TRACKER.getComments() + " Updated.");
			return request;
		}
	}

	public static class UpdateDeviceResponse {

		public Object generate() throws SiteWhereException {
			Device_Tracker tracker = new Device_Tracker();
			tracker.setComments(ExampleData.TRACKER.getComments() + " Updated.");
			return tracker;
		}
	}

	public static class GetCurrentDeviceAssignmentResponse {

		public Object generate() throws SiteWhereException {
			return ExampleData.TRACKER_TO_DEREK;
		}
	}
}