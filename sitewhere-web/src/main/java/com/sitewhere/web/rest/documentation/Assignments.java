/*
 * Copyright (c) SiteWhere, LLC. All rights reserved. http://www.sitewhere.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package com.sitewhere.web.rest.documentation;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import com.sitewhere.core.SiteWherePersistence;
import com.sitewhere.rest.model.common.MetadataProvider;
import com.sitewhere.rest.model.device.request.DeviceAssignmentCreateRequest;
import com.sitewhere.rest.model.search.SearchResults;
import com.sitewhere.spi.SiteWhereException;
import com.sitewhere.spi.device.DeviceAssignmentType;
import com.sitewhere.spi.device.event.IDeviceEvent;

/**
 * Example of REST request for creating an assignment.
 * 
 * @author Derek
 */
@SuppressWarnings("serial")
public class Assignments {

	public static class CreateUnassociatedRequest extends DeviceAssignmentCreateRequest {

		public CreateUnassociatedRequest() throws SiteWhereException {
			setDeviceHardwareId(ExampleData.TRACKER.getHardwareId());
			setAssignmentType(DeviceAssignmentType.Unassociated);
			Map<String, String> metadata = new HashMap<String, String>();
			metadata.put("validUntil", "2016-10-10");
			metadata.put("renewable", "true");
			setMetadata(metadata);
			SiteWherePersistence.deviceAssignmentCreateLogic(this, ExampleData.TRACKER,
					UUID.randomUUID().toString());
		}
	}

	public static class CreateAssociatedRequest extends DeviceAssignmentCreateRequest {

		public CreateAssociatedRequest() throws SiteWhereException {
			setDeviceHardwareId(ExampleData.TRACKER.getHardwareId());
			setAssignmentType(DeviceAssignmentType.Associated);
			setAssetModuleId(ExampleData.AM_PERSONS.getId());
			setAssetId(ExampleData.ASSET_DEREK.getId());
			Map<String, String> metadata = new HashMap<String, String>();
			metadata.put("validUntil", "2016-10-10");
			metadata.put("renewable", "true");
			setMetadata(metadata);
			SiteWherePersistence.deviceAssignmentCreateLogic(this, ExampleData.TRACKER,
					UUID.randomUUID().toString());
		}
	}

	public static class CreateAssociatedResponse {

		public Object generate() throws SiteWhereException {
			MockDeviceAssignmentMarshalHelper helper = new MockDeviceAssignmentMarshalHelper();
			helper.setIncludeAsset(true);
			helper.setIncludeDevice(true);
			helper.setIncludeSite(true);
			return helper.convert(ExampleData.TRACKER_TO_DEREK, new MockAssetModuleManager());
		}
	}

	public static class UpdateAssignmentMetadataRequest {

		public Object generate() throws SiteWhereException {
			MetadataProvider provider = new MetadataProvider();
			provider.addOrReplaceMetadata("key1", "value1");
			provider.addOrReplaceMetadata("key2", "value2");
			return provider;
		}
	}

	public static class ListAssignmentEventsResponse {

		public Object generate() throws SiteWhereException {
			List<IDeviceEvent> events = new ArrayList<IDeviceEvent>();
			events.add(ExampleData.EVENT_LOCATION1);
			events.add(ExampleData.EVENT_MEASUREMENT1);
			events.add(ExampleData.EVENT_ALERT1);
			return new SearchResults<IDeviceEvent>(events, 3);
		}
	}
}