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
import java.util.List;

import com.sitewhere.rest.model.device.event.DeviceEventBatch;
import com.sitewhere.rest.model.device.event.DeviceEventBatchResponse;
import com.sitewhere.rest.model.device.event.request.DeviceAlertCreateRequest;
import com.sitewhere.rest.model.device.event.request.DeviceLocationCreateRequest;
import com.sitewhere.rest.model.device.event.request.DeviceMeasurementsCreateRequest;
import com.sitewhere.rest.model.device.request.DeviceCreateRequest;
import com.sitewhere.rest.model.search.SearchResults;
import com.sitewhere.spi.SiteWhereException;
import com.sitewhere.spi.device.IDevice;
import com.sitewhere.spi.device.IDeviceAssignment;
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
	    MockDeviceAssignmentMarshalHelper helper = new MockDeviceAssignmentMarshalHelper();
	    helper.setIncludeAsset(false);
	    MockAssetModuleManager assets = new MockAssetModuleManager();

	    return helper.convert(ExampleData.TRACKER_TO_DEREK, assets);
	}
    }

    public static class ListDeviceAssignmentHistoryResponse {

	public Object generate() throws SiteWhereException {
	    MockDeviceAssignmentMarshalHelper helper = new MockDeviceAssignmentMarshalHelper();
	    helper.setIncludeAsset(false);
	    MockAssetModuleManager assets = new MockAssetModuleManager();

	    List<IDeviceAssignment> list = new ArrayList<IDeviceAssignment>();
	    list.add(helper.convert(ExampleData.TRACKER_TO_DEREK, assets));
	    list.add(helper.convert(ExampleData.TRACKER_TO_MARTIN, assets));
	    return new SearchResults<IDeviceAssignment>(list, 2);
	}
    }

    public static class AddMappingRequest {

	public Object generate() throws SiteWhereException {
	    return ExampleData.MAPPING_HEART_MONITOR;
	}
    }

    public static class AddMappingResponse {

	public Object generate() throws SiteWhereException {
	    Device_Tracker tracker = new Device_Tracker();
	    tracker.getDeviceElementMappings().add(ExampleData.MAPPING_HEART_MONITOR);
	    return tracker;
	}
    }

    public static class DeleteMappingResponse {

	public Object generate() throws SiteWhereException {
	    MockDeviceMarshalHelper helper = new MockDeviceMarshalHelper();
	    helper.setIncludeSpecification(false);
	    helper.setIncludeAsset(false);
	    helper.setIncludeAssignment(false);
	    helper.setIncludeSite(false);
	    helper.setIncludeNested(false);
	    return helper.convert(ExampleData.TRACKER, new MockAssetModuleManager());
	}
    }

    public static class ListDevicesForCriteriaResponse {

	public Object generate() throws SiteWhereException {
	    MockDeviceMarshalHelper helper = new MockDeviceMarshalHelper();
	    helper.setIncludeSpecification(false);
	    helper.setIncludeAsset(true);
	    helper.setIncludeAssignment(false);
	    helper.setIncludeSite(false);
	    helper.setIncludeNested(false);
	    MockAssetModuleManager assets = new MockAssetModuleManager();

	    List<IDevice> list = new ArrayList<IDevice>();
	    list.add(helper.convert(ExampleData.TRACKER, assets));
	    list.add(helper.convert(ExampleData.HEART_MONITOR, assets));
	    return new SearchResults<IDevice>(list, 2);
	}
    }

    public static class ListDevicesForSpecificationResponse {

	public Object generate() throws SiteWhereException {
	    MockDeviceMarshalHelper helper = new MockDeviceMarshalHelper();
	    helper.setIncludeSpecification(false);
	    helper.setIncludeAsset(true);
	    helper.setIncludeAssignment(false);
	    helper.setIncludeSite(false);
	    helper.setIncludeNested(false);
	    MockAssetModuleManager assets = new MockAssetModuleManager();

	    List<IDevice> list = new ArrayList<IDevice>();
	    list.add(helper.convert(ExampleData.TRACKER, assets));
	    list.add(helper.convert(ExampleData.TRACKER2, assets));
	    return new SearchResults<IDevice>(list, 2);
	}
    }

    public static class AddDeviceEventBatchRequest {

	public Object generate() throws SiteWhereException {
	    DeviceEventBatch batch = new DeviceEventBatch();
	    batch.setHardwareId(ExampleData.TRACKER.getHardwareId());

	    DeviceMeasurementsCreateRequest mx = new DeviceMeasurementsCreateRequest();
	    mx.setMeasurements(ExampleData.EVENT_MEASUREMENT1.getMeasurements());
	    mx.setEventDate(new Date());
	    mx.setUpdateState(true);
	    batch.getMeasurements().add(mx);

	    DeviceLocationCreateRequest loc = new DeviceLocationCreateRequest();
	    loc.setLatitude(ExampleData.EVENT_LOCATION1.getLatitude());
	    loc.setLongitude(ExampleData.EVENT_LOCATION1.getLongitude());
	    loc.setElevation(ExampleData.EVENT_LOCATION1.getElevation());
	    loc.setEventDate(new Date());
	    loc.setUpdateState(true);
	    batch.getLocations().add(loc);

	    DeviceAlertCreateRequest alert = new DeviceAlertCreateRequest();
	    alert.setSource(ExampleData.EVENT_ALERT1.getSource());
	    alert.setType(ExampleData.EVENT_ALERT1.getType());
	    alert.setLevel(ExampleData.EVENT_ALERT1.getLevel());
	    alert.setMessage(ExampleData.EVENT_ALERT1.getMessage());
	    alert.setEventDate(new Date());
	    alert.setUpdateState(true);
	    batch.getAlerts().add(alert);

	    return batch;
	}
    }

    public static class AddDeviceEventBatchResponse {

	public Object generate() throws SiteWhereException {
	    DeviceEventBatchResponse response = new DeviceEventBatchResponse();
	    response.getCreatedMeasurements().add(ExampleData.EVENT_MEASUREMENT1);
	    response.getCreatedLocations().add(ExampleData.EVENT_LOCATION1);
	    response.getCreatedAlerts().add(ExampleData.EVENT_ALERT1);
	    return response;
	}
    }
}