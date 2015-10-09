/*
 * Copyright (c) SiteWhere, LLC. All rights reserved. http://www.sitewhere.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package com.sitewhere.web.rest.documentation;

import com.sitewhere.rest.model.device.request.DeviceSpecificationCreateRequest;
import com.sitewhere.spi.SiteWhereException;

/**
 * Examples of REST payloads for various device specification methods.
 * 
 * @author Derek
 */
public class Specifications {

	public static class CreateSpecificationRequest {

		public Object generate() throws SiteWhereException {
			DeviceSpecificationCreateRequest request = new DeviceSpecificationCreateRequest();
			request.setToken(ExampleData.SPEC_MEITRACK.getToken());
			request.setName(ExampleData.SPEC_MEITRACK.getName());
			request.setContainerPolicy(ExampleData.SPEC_MEITRACK.getContainerPolicy());
			request.setAssetModuleId(ExampleData.SPEC_MEITRACK.getAssetModuleId());
			request.setAssetId(ExampleData.SPEC_MEITRACK.getAssetId());
			return request;
		}
	}

	public static class CreateSpecificationResponse {

		public Object generate() throws SiteWhereException {
			return ExampleData.SPEC_MEITRACK;
		}
	}
}