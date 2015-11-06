/*
 * Copyright (c) SiteWhere, LLC. All rights reserved. http://www.sitewhere.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package com.sitewhere.cloud.providers.initialstate;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.client.RestTemplate;

import com.sitewhere.SiteWhere;
import com.sitewhere.device.event.processor.FilteredOutboundEventProcessor;
import com.sitewhere.device.marshaling.DeviceAssignmentMarshalHelper;
import com.sitewhere.rest.model.device.DeviceAssignment;
import com.sitewhere.spi.SiteWhereException;
import com.sitewhere.spi.SiteWhereSystemException;
import com.sitewhere.spi.device.IDeviceAssignment;
import com.sitewhere.spi.device.event.IDeviceAlert;
import com.sitewhere.spi.device.event.IDeviceLocation;
import com.sitewhere.spi.device.event.IDeviceMeasurements;
import com.sitewhere.spi.device.event.processor.IOutboundEventProcessor;

/**
 * Implmentation of {@link IOutboundEventProcessor} that sends events to the cloud
 * provider at InitialState.com.
 * 
 * @author Derek
 */
public class InitialStateEventProcessor extends FilteredOutboundEventProcessor {

	/** Static logger instance */
	private static Logger LOGGER = Logger.getLogger(InitialStateEventProcessor.class);

	/** Base URI for REST calls */
	private static final String API_BASE = "https://groker.initialstate.com/api/";

	/** Header name for access key */
	private static final String HEADER_ACCESS_KEY = "X-IS-AccessKey";

	/** Header name for bucket key */
	private static final String HEADER_BUCKET_KEY = "X-IS-BucketKey";

	/** Use Spring RestTemplate to send requests */
	private RestTemplate client;

	/** Account-specific key for using streaming APIs */
	private String streamingAccessKey;

	/** Cache of assignment tokens to detailed assignment information */
	private Map<String, DeviceAssignment> assignmentsByToken = new HashMap<String, DeviceAssignment>();

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.sitewhere.spi.server.lifecycle.ILifecycleComponent#start()
	 */
	@Override
	public void start() throws SiteWhereException {
		// Required for filters.
		super.start();

		this.client = new RestTemplate();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.sitewhere.device.event.processor.FilteredOutboundEventProcessor#
	 * onMeasurementsNotFiltered(com.sitewhere.spi.device.event.IDeviceMeasurements)
	 */
	@Override
	public void onMeasurementsNotFiltered(IDeviceMeasurements measurements) throws SiteWhereException {
		Map<String, Double> mx = measurements.getMeasurements();
		List<EventCreateRequest> events = new ArrayList<EventCreateRequest>();
		for (String name : mx.keySet()) {
			EventCreateRequest event = new EventCreateRequest();
			event.setKey(name);
			event.setValue(String.valueOf(mx.get(name)));
			event.setEpoch(((double) System.currentTimeMillis()) / ((double) 1000));
			events.add(event);
		}
		DeviceAssignment assignment = assureBucket(measurements.getDeviceAssignmentToken());
		createEvents(assignment.getToken(), events);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.sitewhere.spi.device.event.processor.IFilteredOutboundEventProcessor#
	 * onLocationNotFiltered(com.sitewhere.spi.device.event.IDeviceLocation)
	 */
	@Override
	public void onLocationNotFiltered(IDeviceLocation location) throws SiteWhereException {
		List<EventCreateRequest> events = new ArrayList<EventCreateRequest>();

		EventCreateRequest event = new EventCreateRequest();
		event.setKey("location");
		event.setValue("" + location.getLatitude() + "," + location.getLongitude());
		event.setEpoch(((double) System.currentTimeMillis()) / ((double) 1000));
		events.add(event);

		DeviceAssignment assignment = assureBucket(location.getDeviceAssignmentToken());
		createEvents(assignment.getToken(), events);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.sitewhere.spi.device.event.processor.IFilteredOutboundEventProcessor#
	 * onAlertNotFiltered(com.sitewhere.spi.device.event.IDeviceAlert)
	 */
	@Override
	public void onAlertNotFiltered(IDeviceAlert alert) throws SiteWhereException {
		List<EventCreateRequest> events = new ArrayList<EventCreateRequest>();

		EventCreateRequest event = new EventCreateRequest();
		event.setKey(alert.getType());
		event.setValue(alert.getMessage());
		event.setEpoch(((double) System.currentTimeMillis()) / ((double) 1000));
		events.add(event);

		DeviceAssignment assignment = assureBucket(alert.getDeviceAssignmentToken());
		createEvents(assignment.getToken(), events);
	}

	/**
	 * Assure that a bucket is created for the given assignment.
	 * 
	 * @param assignmentToken
	 * @return
	 * @throws SiteWhereException
	 */
	protected DeviceAssignment assureBucket(String assignmentToken) throws SiteWhereException {
		DeviceAssignment cached = assignmentsByToken.get(assignmentToken);
		if (cached != null) {
			return cached;
		}
		IDeviceAssignment assignment =
				SiteWhere.getServer().getDeviceManagement(getTenant()).getDeviceAssignmentByToken(
						assignmentToken);
		if (assignment == null) {
			throw new SiteWhereException("Assignment not found.");
		}

		DeviceAssignmentMarshalHelper helper = new DeviceAssignmentMarshalHelper(getTenant());
		helper.setIncludeAsset(false);
		helper.setIncludeDevice(true);
		helper.setIncludeSite(false);
		DeviceAssignment converted =
				helper.convert(assignment, SiteWhere.getServer().getAssetModuleManager(getTenant()));

		createBucket(converted.getToken(), converted.getAssetName() + " ("
				+ converted.getDevice().getAssetName() + ")");
		assignmentsByToken.put(assignmentToken, converted);
		return converted;
	}

	/**
	 * Create a new InitialState bucket. Returns true if created, false if it already
	 * existed, and throws and exception on error.
	 * 
	 * @param bucketKey
	 * @param bucketName
	 * @return
	 * @throws SiteWhereException
	 */
	protected boolean createBucket(String bucketKey, String bucketName) throws SiteWhereException {
		try {
			BucketCreateRequest request = new BucketCreateRequest();
			request.setBucketKey(bucketKey);
			request.setBucketName(bucketName);

			HttpHeaders headers = new HttpHeaders();
			headers.add(HEADER_ACCESS_KEY, getStreamingAccessKey());
			HttpEntity<BucketCreateRequest> entity = new HttpEntity<BucketCreateRequest>(request, headers);
			String url = API_BASE + "buckets";
			Map<String, String> vars = new HashMap<String, String>();
			ResponseEntity<String> response =
					getClient().exchange(url, HttpMethod.POST, entity, String.class, vars);
			if (response.getStatusCode() == HttpStatus.CREATED) {
				return true;
			}
			if (response.getStatusCode() == HttpStatus.NO_CONTENT) {
				return false;
			}
			throw new SiteWhereException("Unable to create bucket. Status code was: "
					+ response.getStatusCode());
		} catch (ResourceAccessException e) {
			if (e.getCause() instanceof SiteWhereSystemException) {
				throw (SiteWhereSystemException) e.getCause();
			}
			throw new RuntimeException(e);
		}
	}

	/**
	 * Send a batch of events to the given bucket.
	 * 
	 * @param bucketKey
	 * @param events
	 * @return
	 * @throws SiteWhereException
	 */
	protected boolean createEvents(String bucketKey, List<EventCreateRequest> events)
			throws SiteWhereException {
		try {
			HttpHeaders headers = new HttpHeaders();
			headers.add(HEADER_ACCESS_KEY, getStreamingAccessKey());
			headers.add(HEADER_BUCKET_KEY, bucketKey);
			HttpEntity<List<EventCreateRequest>> entity =
					new HttpEntity<List<EventCreateRequest>>(events, headers);
			String url = API_BASE + "events";
			Map<String, String> vars = new HashMap<String, String>();
			ResponseEntity<String> response =
					getClient().exchange(url, HttpMethod.POST, entity, String.class, vars);
			if (response.getStatusCode() == HttpStatus.NO_CONTENT) {
				return true;
			}
			throw new SiteWhereException("Unable to create event. Status code was: "
					+ response.getStatusCode());
		} catch (ResourceAccessException e) {
			throw new SiteWhereException(e);
		}
	}

	@Override
	public Logger getLogger() {
		return LOGGER;
	}

	public RestTemplate getClient() {
		return client;
	}

	public void setClient(RestTemplate client) {
		this.client = client;
	}

	public String getStreamingAccessKey() {
		return streamingAccessKey;
	}

	public void setStreamingAccessKey(String streamingAccessKey) {
		this.streamingAccessKey = streamingAccessKey;
	}
}