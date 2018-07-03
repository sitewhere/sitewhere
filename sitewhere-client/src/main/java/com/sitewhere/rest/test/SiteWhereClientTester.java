/*
 * Copyright (c) SiteWhere, LLC. All rights reserved. http://www.sitewhere.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package com.sitewhere.rest.test;

import java.util.Date;
import java.util.concurrent.Callable;

import com.sitewhere.rest.client.SiteWhereClient;
import com.sitewhere.rest.model.device.event.request.DeviceAlertCreateRequest;
import com.sitewhere.rest.model.device.event.request.DeviceLocationCreateRequest;
import com.sitewhere.rest.model.device.event.request.DeviceMeasurementCreateRequest;
import com.sitewhere.rest.test.SiteWhereClientTester.TestResults;
import com.sitewhere.spi.ISiteWhereClient;
import com.sitewhere.spi.device.event.AlertLevel;

/**
 * Used to test performance of repeated calls to the SiteWhere REST services.
 * Randomly creates a given number of events for a given device assignment.
 * 
 * @author Derek
 */
public class SiteWhereClientTester implements Callable<TestResults> {

    /** Token for assignment to receive events */
    private String assignmentToken;

    /** Number of events to generate */
    private int eventCount;

    /** Indicates whether assignment state should be updated by event */
    private boolean updateState;

    public SiteWhereClientTester(String assignmentToken, int eventCount, boolean updateState) {
	this.assignmentToken = assignmentToken;
	this.eventCount = eventCount;
	this.updateState = updateState;
    }

    /*
     * (non-Javadoc)
     * 
     * @see java.util.concurrent.Callable#call()
     */
    @Override
    public TestResults call() throws Exception {
	ISiteWhereClient client = new SiteWhereClient("http://sw-swarm-master.cloudapp.net:8080/sitewhere/api/",
		"admin", "password");
	for (int i = 0; i < eventCount; i++) {
	    int random = (int) Math.floor(Math.random() * 3);
	    if (random == 0) {
		DeviceAlertCreateRequest request = new DeviceAlertCreateRequest();
		request.setEventDate(new Date());
		request.setType("test.error");
		request.setLevel(AlertLevel.Error);
		request.setMessage("This is a test alert message.");
		request.setUpdateState(updateState);
		client.createDeviceAlert(getAssignmentToken(), request);
	    } else if (random == 1) {
		DeviceLocationCreateRequest request = new DeviceLocationCreateRequest();
		request.setEventDate(new Date());
		request.setLatitude(33.7550);
		request.setLongitude(-84.3900);
		request.setElevation(1000.0);
		request.setUpdateState(updateState);
		client.createDeviceLocation(getAssignmentToken(), request);
	    } else if (random == 2) {
		DeviceMeasurementCreateRequest request = new DeviceMeasurementCreateRequest();
		request.setEventDate(new Date());
		request.setUpdateState(updateState);
		client.createDeviceMeasurements(getAssignmentToken(), request);
	    }
	}
	return new TestResults();
    }

    public String getAssignmentToken() {
	return assignmentToken;
    }

    public void setAssignmentToken(String assignmentToken) {
	this.assignmentToken = assignmentToken;
    }

    public int getEventCount() {
	return eventCount;
    }

    public void setEventCount(int eventCount) {
	this.eventCount = eventCount;
    }

    /**
     * Holds results from client test.
     * 
     * @author Derek
     */
    public static class TestResults {
    }
}