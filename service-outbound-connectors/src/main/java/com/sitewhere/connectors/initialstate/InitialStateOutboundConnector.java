/**
 * Copyright © 2014-2021 The SiteWhere Authors
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.sitewhere.connectors.initialstate;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import com.sitewhere.connectors.SerialOutboundConnector;
import com.sitewhere.connectors.spi.IOutboundConnector;
import com.sitewhere.microservice.api.device.DeviceAssignmentMarshalHelper;
import com.sitewhere.rest.model.device.DeviceAssignment;
import com.sitewhere.rest.model.device.marshaling.MarshaledDeviceAssignment;
import com.sitewhere.spi.SiteWhereException;
import com.sitewhere.spi.device.IDeviceAssignment;
import com.sitewhere.spi.device.event.IDeviceAlert;
import com.sitewhere.spi.device.event.IDeviceEventContext;
import com.sitewhere.spi.device.event.IDeviceLocation;
import com.sitewhere.spi.device.event.IDeviceMeasurement;
import com.sitewhere.spi.microservice.lifecycle.ILifecycleProgressMonitor;

/**
 * Implmentation of {@link IOutboundConnector} that sends events to the cloud
 * provider at InitialState.com.
 */
@SuppressWarnings("unused")
public class InitialStateOutboundConnector extends SerialOutboundConnector {

    /** Base URI for REST calls */
    private static final String API_BASE = "https://groker.initialstate.com/api/";

    /** Header name for access key */
    private static final String HEADER_ACCESS_KEY = "X-IS-AccessKey";

    /** Header name for bucket key */
    private static final String HEADER_BUCKET_KEY = "X-IS-BucketKey";

    /** Use Spring RestTemplate to send requests */
    // private RestTemplate client;

    /** Account-specific key for using streaming APIs */
    private String streamingAccessKey;

    /** Cache of assignment ids to detailed assignment information */
    private Map<UUID, DeviceAssignment> assignmentsById = new HashMap<UUID, DeviceAssignment>();

    /*
     * @see
     * com.sitewhere.connectors.FilteredOutboundConnector#start(com.sitewhere.spi.
     * microservice.lifecycle.ILifecycleProgressMonitor)
     */
    @Override
    public void start(ILifecycleProgressMonitor monitor) throws SiteWhereException {
	// Required for filters.
	super.start(monitor);

	// this.client = new RestTemplate();
    }

    /*
     * @see
     * com.sitewhere.connectors.SerialOutboundConnector#onMeasurement(com.sitewhere.
     * spi.device.event.IDeviceEventContext,
     * com.sitewhere.spi.device.event.IDeviceMeasurement)
     */
    @Override
    public void onMeasurement(IDeviceEventContext context, IDeviceMeasurement mx) throws SiteWhereException {
	List<EventCreateRequest> events = new ArrayList<EventCreateRequest>();
	EventCreateRequest event = new EventCreateRequest();
	event.setKey(mx.getName());
	event.setValue(String.valueOf(mx.getValue()));
	event.setEpoch(((double) System.currentTimeMillis()) / ((double) 1000));
	events.add(event);
	DeviceAssignment assignment = assureBucket(mx.getDeviceAssignmentId());
	createEvents(assignment.getToken(), events);
    }

    /*
     * @see
     * com.sitewhere.connectors.SerialOutboundConnector#onLocation(com.sitewhere.spi
     * .device.event.IDeviceEventContext,
     * com.sitewhere.spi.device.event.IDeviceLocation)
     */
    @Override
    public void onLocation(IDeviceEventContext context, IDeviceLocation location) throws SiteWhereException {
	List<EventCreateRequest> events = new ArrayList<EventCreateRequest>();

	EventCreateRequest event = new EventCreateRequest();
	event.setKey("location");
	event.setValue("" + location.getLatitude() + "," + location.getLongitude());
	event.setEpoch(((double) System.currentTimeMillis()) / ((double) 1000));
	events.add(event);

	DeviceAssignment assignment = assureBucket(location.getDeviceAssignmentId());
	createEvents(assignment.getToken(), events);
    }

    /*
     * @see
     * com.sitewhere.connectors.SerialOutboundConnector#onAlert(com.sitewhere.spi.
     * device.event.IDeviceEventContext,
     * com.sitewhere.spi.device.event.IDeviceAlert)
     */
    @Override
    public void onAlert(IDeviceEventContext context, IDeviceAlert alert) throws SiteWhereException {
	List<EventCreateRequest> events = new ArrayList<EventCreateRequest>();

	EventCreateRequest event = new EventCreateRequest();
	event.setKey(alert.getType());
	event.setValue(alert.getMessage());
	event.setEpoch(((double) System.currentTimeMillis()) / ((double) 1000));
	events.add(event);

	DeviceAssignment assignment = assureBucket(alert.getDeviceAssignmentId());
	createEvents(assignment.getToken(), events);
    }

    /**
     * Assure that a bucket is created for the given assignment.
     * 
     * @param assignmentToken
     * @return
     * @throws SiteWhereException
     */
    protected DeviceAssignment assureBucket(UUID assignmentId) throws SiteWhereException {
	DeviceAssignment cached = assignmentsById.get(assignmentId);
	if (cached != null) {
	    return cached;
	}
	IDeviceAssignment assignment = getDeviceManagement().getDeviceAssignment(assignmentId);
	if (assignment == null) {
	    throw new SiteWhereException("Assignment not found.");
	}

	DeviceAssignmentMarshalHelper helper = new DeviceAssignmentMarshalHelper(getDeviceManagement());
	helper.setIncludeAsset(true);
	helper.setIncludeDevice(true);
	MarshaledDeviceAssignment converted = helper.convert(assignment, null);

	createBucket(converted.getToken(),
		converted.getAsset().getName() + " (" + converted.getDevice().getToken() + ")");
	assignmentsById.put(assignmentId, converted);
	return converted;
    }

    /**
     * Create a new InitialState bucket. Returns true if created, false if it
     * already existed, and throws and exception on error.
     * 
     * @param bucketKey
     * @param bucketName
     * @return
     * @throws SiteWhereException
     */
    protected boolean createBucket(String bucketKey, String bucketName) throws SiteWhereException {
	// try {
	// BucketCreateRequest request = new BucketCreateRequest();
	// request.setBucketKey(bucketKey);
	// request.setBucketName(bucketName);
	//
	// HttpHeaders headers = new HttpHeaders();
	// headers.add(HEADER_ACCESS_KEY, getStreamingAccessKey());
	// HttpEntity<BucketCreateRequest> entity = new
	// HttpEntity<BucketCreateRequest>(request, headers);
	// String url = API_BASE + "buckets";
	// Map<String, String> vars = new HashMap<String, String>();
	// ResponseEntity<String> response = getClient().exchange(url, HttpMethod.POST,
	// entity, String.class, vars);
	// if (response.getStatusCode() == HttpStatus.CREATED) {
	// return true;
	// }
	// if (response.getStatusCode() == HttpStatus.NO_CONTENT) {
	// return false;
	// }
	// throw new SiteWhereException("Unable to create bucket. Status code was: " +
	// response.getStatusCode());
	// } catch (ResourceAccessException e) {
	// if (e.getCause() instanceof SiteWhereSystemException) {
	// throw (SiteWhereSystemException) e.getCause();
	// }
	// throw new RuntimeException(e);
	// }
	return false;
    }

    /**
     * Send a batch of events to the given bucket.
     * 
     * @param bucketKey
     * @param events
     * @return
     * @throws SiteWhereException
     */
    protected boolean createEvents(String bucketKey, List<EventCreateRequest> events) throws SiteWhereException {
	// try {
	// HttpHeaders headers = new HttpHeaders();
	// headers.add(HEADER_ACCESS_KEY, getStreamingAccessKey());
	// headers.add(HEADER_BUCKET_KEY, bucketKey);
	// HttpEntity<List<EventCreateRequest>> entity = new
	// HttpEntity<List<EventCreateRequest>>(events, headers);
	// String url = API_BASE + "events";
	// Map<String, String> vars = new HashMap<String, String>();
	// ResponseEntity<String> response = getClient().exchange(url, HttpMethod.POST,
	// entity, String.class, vars);
	// if (response.getStatusCode() == HttpStatus.NO_CONTENT) {
	// return true;
	// }
	// throw new SiteWhereException("Unable to create event. Status code was: " +
	// response.getStatusCode());
	// } catch (ResourceAccessException e) {
	// throw new SiteWhereException(e);
	// }
	return false;
    }
    //
    // public RestTemplate getClient() {
    // return client;
    // }
    //
    // public void setClient(RestTemplate client) {
    // this.client = client;
    // }

    public String getStreamingAccessKey() {
	return streamingAccessKey;
    }

    public void setStreamingAccessKey(String streamingAccessKey) {
	this.streamingAccessKey = streamingAccessKey;
    }
}