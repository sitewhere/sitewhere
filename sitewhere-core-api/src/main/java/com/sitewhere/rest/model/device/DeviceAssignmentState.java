/*
 * Copyright (c) SiteWhere, LLC. All rights reserved. http://www.sitewhere.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package com.sitewhere.rest.model.device;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.sitewhere.rest.model.datatype.JsonDateSerializer;
import com.sitewhere.rest.model.device.event.DeviceAlert;
import com.sitewhere.rest.model.device.event.DeviceLocation;
import com.sitewhere.rest.model.device.event.DeviceMeasurement;
import com.sitewhere.spi.SiteWhereException;
import com.sitewhere.spi.device.IDeviceAssignmentState;
import com.sitewhere.spi.device.event.IDeviceAlert;
import com.sitewhere.spi.device.event.IDeviceLocation;
import com.sitewhere.spi.device.event.IDeviceMeasurement;

/**
 * Model object for device assignment state.
 * 
 * @author Derek
 */
@JsonInclude(Include.NON_NULL)
public class DeviceAssignmentState implements IDeviceAssignmentState, Serializable {

    /** Serialization version identifier */
    private static final long serialVersionUID = -8536671667872805013L;

    /** Date of last interaction with assignment */
    private Date lastInteractionDate;

    /** Date presence was determined to be missing */
    private Date presenceMissingDate;

    /** Last location event */
    private DeviceLocation lastLocation;

    /** Last measurement event for each measurement id */
    private List<DeviceMeasurement> latestMeasurements = new ArrayList<DeviceMeasurement>();

    /** Last alert event for each alert type */
    private List<DeviceAlert> latestAlerts = new ArrayList<DeviceAlert>();

    /*
     * (non-Javadoc)
     * 
     * @see
     * com.sitewhere.spi.device.IDeviceAssignmentState#getLastInteractionDate()
     */
    @Override
    @JsonSerialize(using = JsonDateSerializer.class)
    public Date getLastInteractionDate() {
	return lastInteractionDate;
    }

    public void setLastInteractionDate(Date lastInteractionDate) {
	this.lastInteractionDate = lastInteractionDate;
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * com.sitewhere.spi.device.IDeviceAssignmentState#getPresenceMissingDate()
     */
    @JsonSerialize(using = JsonDateSerializer.class)
    public Date getPresenceMissingDate() {
	return presenceMissingDate;
    }

    public void setPresenceMissingDate(Date presenceMissingDate) {
	this.presenceMissingDate = presenceMissingDate;
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.sitewhere.spi.device.IDeviceAssignmentState#getLastLocation()
     */
    @Override
    public IDeviceLocation getLastLocation() {
	return lastLocation;
    }

    public void setLastLocation(DeviceLocation lastLocation) {
	this.lastLocation = lastLocation;
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * com.sitewhere.spi.device.IDeviceAssignmentState#getLatestMeasurements()
     */
    @Override
    @SuppressWarnings("unchecked")
    public List<IDeviceMeasurement> getLatestMeasurements() {
	return (List<IDeviceMeasurement>) (List<? extends IDeviceMeasurement>) latestMeasurements;
    }

    public void setLatestMeasurements(List<DeviceMeasurement> latestMeasurements) {
	this.latestMeasurements = latestMeasurements;
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.sitewhere.spi.device.IDeviceAssignmentState#getLatestAlerts()
     */
    @Override
    @SuppressWarnings("unchecked")
    public List<IDeviceAlert> getLatestAlerts() {
	return (List<IDeviceAlert>) (List<? extends IDeviceAlert>) latestAlerts;
    }

    public void setLatestAlerts(List<DeviceAlert> latestAlerts) {
	this.latestAlerts = latestAlerts;
    }

    public static DeviceAssignmentState copy(IDeviceAssignmentState source) throws SiteWhereException {
	DeviceAssignmentState target = new DeviceAssignmentState();
	target.setLastInteractionDate(source.getLastInteractionDate());
	target.setPresenceMissingDate(source.getPresenceMissingDate());
	if (source.getLastLocation() != null) {
	    target.setLastLocation(DeviceLocation.copy(source.getLastLocation()));
	}
	List<DeviceMeasurement> measurements = new ArrayList<DeviceMeasurement>();
	for (IDeviceMeasurement sm : source.getLatestMeasurements()) {
	    measurements.add(DeviceMeasurement.copy(sm));
	}
	target.setLatestMeasurements(measurements);
	List<DeviceAlert> alerts = new ArrayList<DeviceAlert>();
	for (IDeviceAlert sa : source.getLatestAlerts()) {
	    alerts.add(DeviceAlert.copy(sa));
	}
	target.setLatestAlerts(alerts);
	return target;
    }
}