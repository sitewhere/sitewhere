/*
 * $Id$
 * --------------------------------------------------------------------------------------
 * Copyright (c) Reveal Technologies, LLC. All rights reserved. http://www.reveal-tech.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */

package com.sitewhere.rest.model.device.event;

import java.util.Map;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.sitewhere.spi.device.event.DeviceEventType;
import com.sitewhere.spi.device.event.IDeviceMeasurements;

/**
 * Implementation of device measurements.
 * 
 * @author dadams
 */
@JsonIgnoreProperties
@JsonInclude(Include.NON_NULL)
public class DeviceMeasurements extends DeviceEvent implements IDeviceMeasurements {

	/** For Java serialization */
	private static final long serialVersionUID = -4369962596450151827L;

	/** Holder for measurements */
	private MeasurementsProvider measurementsMetadata = new MeasurementsProvider();

	public DeviceMeasurements() {
		super(DeviceEventType.Measurements);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.sitewhere.spi.device.IMeasurementsProvider#addOrReplaceMeasurement(java.lang
	 * .String, java.lang.Double)
	 */
	@Override
	public void addOrReplaceMeasurement(String name, Double value) {
		measurementsMetadata.addOrReplaceMeasurement(name, value);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.sitewhere.spi.device.IMeasurementsProvider#removeMeasurement(java.lang.String)
	 */
	@Override
	public Double removeMeasurement(String name) {
		return measurementsMetadata.removeMeasurement(name);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.sitewhere.spi.device.IMeasurementsProvider#getMeasurement(java.lang.String)
	 */
	@Override
	public Double getMeasurement(String name) {
		return measurementsMetadata.getMeasurement(name);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.sitewhere.spi.device.IMeasurementsProvider#getMeasurements()
	 */
	@Override
	public Map<String, Double> getMeasurements() {
		return measurementsMetadata.getMeasurements();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.sitewhere.spi.device.IMeasurementsProvider#clearMeasurements()
	 */
	@Override
	public void clearMeasurements() {
		measurementsMetadata.clearMeasurements();
	}

	/**
	 * Needed for JSON marshaling.
	 * 
	 * @param entries
	 */
	public void setMeasurements(Map<String, Double> entries) {
		this.measurementsMetadata = new MeasurementsProvider();
		measurementsMetadata.setMeasurements(entries);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.sitewhere.spi.device.IDeviceMeasurements#getMeasurementsSummary()
	 */
	public String getMeasurementsSummary() {
		String result = "";
		boolean isFirst = true;
		for (String key : measurementsMetadata.getMeasurements().keySet()) {
			if (!isFirst) {
				result += ", ";
			} else {
				isFirst = false;
			}
			result += key + ": " + measurementsMetadata.getMeasurement(key);
		}
		return result;
	}

	/**
	 * For Jackson marshalling.
	 * 
	 * @param value
	 */
	public void setMeasurementsSummary(String value) {
	}

	/**
	 * Create a copy of an SPI object. Used by web services for marshaling.
	 * 
	 * @param input
	 * @return
	 */
	public static DeviceMeasurements copy(IDeviceMeasurements input) {
		DeviceMeasurements result = new DeviceMeasurements();
		DeviceEvent.copy(input, result);
		for (String key : input.getMeasurements().keySet()) {
			result.addOrReplaceMeasurement(key, input.getMeasurement(key));
		}
		return result;
	}
}