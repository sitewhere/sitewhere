/*
 * Copyright (c) SiteWhere, LLC. All rights reserved. http://www.sitewhere.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package com.sitewhere.web.rest.documentation;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import com.sitewhere.core.SiteWherePersistence;
import com.sitewhere.rest.model.device.DeviceAssignment;
import com.sitewhere.rest.model.device.request.DeviceAssignmentCreateRequest;
import com.sitewhere.spi.SiteWhereException;
import com.sitewhere.spi.device.DeviceAssignmentType;

/**
 * Example of REST request for creating an assignment.
 * 
 * @author Derek
 */
@SuppressWarnings("serial")
public class Assignments {

	public static class CreateUnassociatedRequest extends DeviceAssignmentCreateRequest {

		public CreateUnassociatedRequest() throws SiteWhereException {
			setDeviceHardwareId(ExampleData.TRACKER1.getHardwareId());
			setAssignmentType(DeviceAssignmentType.Unassociated);
			Map<String, String> metadata = new HashMap<String, String>();
			metadata.put("validUntil", "2016-10-10");
			metadata.put("renewable", "true");
			setMetadata(metadata);
			SiteWherePersistence.deviceAssignmentCreateLogic(this, ExampleData.TRACKER1,
					UUID.randomUUID().toString());
		}
	}

	public static class CreateAssociatedRequest extends DeviceAssignmentCreateRequest {

		public CreateAssociatedRequest() throws SiteWhereException {
			setDeviceHardwareId(ExampleData.TRACKER1.getHardwareId());
			setAssignmentType(DeviceAssignmentType.Associated);
			setAssetModuleId("fs-persons");
			setAssetId("bob");
			Map<String, String> metadata = new HashMap<String, String>();
			metadata.put("validUntil", "2016-10-10");
			metadata.put("renewable", "true");
			setMetadata(metadata);
			SiteWherePersistence.deviceAssignmentCreateLogic(this, ExampleData.TRACKER1,
					UUID.randomUUID().toString());
		}
	}

	public static class CreateAssociatedResponse extends DeviceAssignment {

		public CreateAssociatedResponse() {
			setDeviceHardwareId(ExampleData.TRACKER1.getHardwareId());
			setAssignmentType(DeviceAssignmentType.Associated);
			setAssetModuleId("fs-persons");
			setAssetId("bob");
			Map<String, String> metadata = new HashMap<String, String>();
			metadata.put("validUntil", "2016-10-10");
			metadata.put("renewable", "true");
			setMetadata(metadata);
		}
	}
}