/*
 * DeviceMeasurementsCreateRequest.java 
 * --------------------------------------------------------------------------------------
 * Copyright (c) Reveal Technologies, LLC. All rights reserved. http://www.reveal-tech.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package com.sitewhere.rest.model.device.event.request;

import java.util.Map;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.sitewhere.rest.model.device.event.DeviceMeasurements;
import com.sitewhere.rest.model.device.event.MeasurementsProvider;
import com.sitewhere.spi.device.event.request.IDeviceMeasurementsCreateRequest;

/**
 * Model object used to create a new {@link DeviceMeasurements} via REST APIs.
 * 
 * @author Derek
 */
@JsonIgnoreProperties
@JsonInclude(Include.NON_NULL)
public class DeviceMeasurementsCreateRequest extends DeviceEventCreateRequest implements
		IDeviceMeasurementsCreateRequest {

	/** Measurements metadata */
	private MeasurementsProvider measurements = new MeasurementsProvider();

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.sitewhere.spi.device.IMeasurementsProvider#addOrReplaceMeasurement(java.lang
	 * .String, java.lang.Double)
	 */
	@Override
	public void addOrReplaceMeasurement(String name, Double value) {
		measurements.addOrReplaceMeasurement(name, value);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.sitewhere.spi.device.IMeasurementsProvider#removeMeasurement(java.lang.String)
	 */
	@Override
	public Double removeMeasurement(String name) {
		return measurements.removeMeasurement(name);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.sitewhere.spi.device.IMeasurementsProvider#getMeasurement(java.lang.String)
	 */
	@Override
	public Double getMeasurement(String name) {
		return measurements.getMeasurement(name);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.sitewhere.spi.device.IMeasurementsProvider#getMeasurements()
	 */
	@Override
	public Map<String, Double> getMeasurements() {
		return measurements.getMeasurements();
	}

	/**
	 * Needed for JSON marshaling.
	 * 
	 * @param entries
	 */
	public void setMeasurements(Map<String, Double> measurements) {
		this.measurements = new MeasurementsProvider();
		this.measurements.setMeasurements(measurements);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.sitewhere.spi.device.IMeasurementsProvider#clearMeasurements()
	 */
	@Override
	public void clearMeasurements() {
		measurements.clearMeasurements();
	}
}