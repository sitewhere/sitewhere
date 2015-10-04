/*
 * Copyright (c) SiteWhere, LLC. All rights reserved. http://www.sitewhere.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package com.sitewhere.web.rest.documentation;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import com.sitewhere.core.SiteWherePersistence;
import com.sitewhere.device.charting.ChartBuilder;
import com.sitewhere.rest.model.common.MetadataProvider;
import com.sitewhere.rest.model.device.event.request.DeviceAlertCreateRequest;
import com.sitewhere.rest.model.device.event.request.DeviceLocationCreateRequest;
import com.sitewhere.rest.model.device.event.request.DeviceMeasurementsCreateRequest;
import com.sitewhere.rest.model.device.request.DeviceAssignmentCreateRequest;
import com.sitewhere.rest.model.device.request.DeviceStreamCreateRequest;
import com.sitewhere.rest.model.search.SearchResults;
import com.sitewhere.spi.SiteWhereException;
import com.sitewhere.spi.device.DeviceAssignmentType;
import com.sitewhere.spi.device.event.IDeviceAlert;
import com.sitewhere.spi.device.event.IDeviceEvent;
import com.sitewhere.spi.device.event.IDeviceLocation;
import com.sitewhere.spi.device.event.IDeviceMeasurements;
import com.sitewhere.spi.device.streaming.IDeviceStream;
import com.sitewhere.spi.search.ISearchResults;

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

	public static class ListAssignmentMeasurementsResponse {

		public Object generate() throws SiteWhereException {
			List<IDeviceMeasurements> events = new ArrayList<IDeviceMeasurements>();
			events.add(ExampleData.EVENT_MEASUREMENT1);
			events.add(ExampleData.EVENT_MEASUREMENT2);
			return new SearchResults<IDeviceMeasurements>(events, 2);
		}
	}

	public static class ListAssignmentMeasurementsChartSeriesResponse {

		@SuppressWarnings("unchecked")
		public Object generate() throws SiteWhereException {
			ISearchResults<IDeviceMeasurements> mx =
					(ISearchResults<IDeviceMeasurements>) (new ListAssignmentMeasurementsResponse()).generate();
			ChartBuilder builder = new ChartBuilder();
			return builder.process(mx.getResults(), new String[0]);
		}
	}

	public static class CreateAssignmentMeasurementsRequest {

		public Object generate() throws SiteWhereException {
			DeviceMeasurementsCreateRequest request = new DeviceMeasurementsCreateRequest();
			request.setMeasurements(ExampleData.EVENT_MEASUREMENT1.getMeasurements());
			request.setEventDate(new Date());
			request.setUpdateState(true);
			return request;
		}
	}

	public static class CreateAssignmentMeasurementsResponse {

		public Object generate() throws SiteWhereException {
			return ExampleData.EVENT_MEASUREMENT1;
		}
	}

	public static class ListAssignmenLocationsResponse {

		public Object generate() throws SiteWhereException {
			List<IDeviceLocation> events = new ArrayList<IDeviceLocation>();
			events.add(ExampleData.EVENT_LOCATION1);
			events.add(ExampleData.EVENT_LOCATION2);
			return new SearchResults<IDeviceLocation>(events, 2);
		}
	}

	public static class CreateAssignmentLocationRequest {

		public Object generate() throws SiteWhereException {
			DeviceLocationCreateRequest request = new DeviceLocationCreateRequest();
			request.setLatitude(ExampleData.EVENT_LOCATION1.getLatitude());
			request.setLongitude(ExampleData.EVENT_LOCATION1.getLongitude());
			request.setElevation(ExampleData.EVENT_LOCATION1.getElevation());
			request.setEventDate(new Date());
			request.setUpdateState(true);
			return request;
		}
	}

	public static class CreateAssignmentLocationResponse {

		public Object generate() throws SiteWhereException {
			return ExampleData.EVENT_LOCATION1;
		}
	}

	public static class ListAssignmenAlertsResponse {

		public Object generate() throws SiteWhereException {
			List<IDeviceAlert> events = new ArrayList<IDeviceAlert>();
			events.add(ExampleData.EVENT_ALERT1);
			events.add(ExampleData.EVENT_ALERT2);
			return new SearchResults<IDeviceAlert>(events, 2);
		}
	}

	public static class CreateAssignmentAlertRequest {

		public Object generate() throws SiteWhereException {
			DeviceAlertCreateRequest request = new DeviceAlertCreateRequest();
			request.setSource(ExampleData.EVENT_ALERT1.getSource());
			request.setType(ExampleData.EVENT_ALERT1.getType());
			request.setLevel(ExampleData.EVENT_ALERT1.getLevel());
			request.setMessage(ExampleData.EVENT_ALERT1.getMessage());
			request.setEventDate(new Date());
			request.setUpdateState(true);
			return request;
		}
	}

	public static class CreateAssignmentAlertResponse {

		public Object generate() throws SiteWhereException {
			return ExampleData.EVENT_ALERT1;
		}
	}

	public static class CreateDeviceStreamRequest {

		public Object generate() throws SiteWhereException {
			DeviceStreamCreateRequest request = new DeviceStreamCreateRequest();
			request.setStreamId(ExampleData.STREAM1.getStreamId());
			request.setContentType(ExampleData.STREAM1.getContentType());
			return request;
		}
	}

	public static class CreateDeviceStreamResponse {

		public Object generate() throws SiteWhereException {
			return ExampleData.STREAM1;
		}
	}

	public static class ListDeviceStreamsResponse {

		public Object generate() throws SiteWhereException {
			List<IDeviceStream> streams = new ArrayList<IDeviceStream>();
			streams.add(ExampleData.STREAM1);
			streams.add(ExampleData.STREAM2);
			return new SearchResults<IDeviceStream>(streams, 2);
		}
	}

	public static class GetDeviceStreamResponse {

		public Object generate() throws SiteWhereException {
			return ExampleData.STREAM1;
		}
	}
}