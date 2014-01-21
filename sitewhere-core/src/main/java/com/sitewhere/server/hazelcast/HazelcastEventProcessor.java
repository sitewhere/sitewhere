/*
 * HazelcastEventProcessor.java 
 * --------------------------------------------------------------------------------------
 * Copyright (c) Reveal Technologies, LLC. All rights reserved. http://www.reveal-tech.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package com.sitewhere.server.hazelcast;

import org.apache.log4j.Logger;

import com.hazelcast.core.ITopic;
import com.sitewhere.rest.model.device.event.DeviceAlert;
import com.sitewhere.rest.model.device.event.DeviceLocation;
import com.sitewhere.rest.model.device.event.DeviceMeasurements;
import com.sitewhere.rest.model.device.event.processor.DeviceEventProcessor;
import com.sitewhere.spi.SiteWhereException;
import com.sitewhere.spi.device.event.IDeviceAlert;
import com.sitewhere.spi.device.event.IDeviceLocation;
import com.sitewhere.spi.device.event.IDeviceMeasurements;
import com.sitewhere.spi.server.hazelcast.ISiteWhereHazelcast;

/**
 * Sends processed device events out on Hazelcast topics for further processing.
 * 
 * @author Derek
 */
public class HazelcastEventProcessor extends DeviceEventProcessor {

	/** Static logger instance */
	private static Logger LOGGER = Logger.getLogger(HazelcastEventProcessor.class);

	/** Common Hazelcast configuration */
	private SiteWhereHazelcastConfiguration configuration;

	/** Topic for device measurements */
	private ITopic<DeviceMeasurements> measurementsTopic;

	/** Topic for device locations */
	private ITopic<DeviceLocation> locationsTopic;

	/** Topic for device alerts */
	private ITopic<DeviceAlert> alertsTopic;

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.sitewhere.rest.model.device.event.processor.DeviceEventProcessor#start()
	 */
	@Override
	public void start() throws SiteWhereException {
		LOGGER.info("Hazelcast device event processor starting ...");
		if (getConfiguration() == null) {
			throw new SiteWhereException("No Hazelcast configuration provided.");
		}
		this.measurementsTopic =
				getConfiguration().getHazelcastInstance().getTopic(
						ISiteWhereHazelcast.TOPIC_MEASUREMENTS_ADDED);
		this.locationsTopic =
				getConfiguration().getHazelcastInstance().getTopic(ISiteWhereHazelcast.TOPIC_LOCATION_ADDED);
		this.alertsTopic =
				getConfiguration().getHazelcastInstance().getTopic(ISiteWhereHazelcast.TOPIC_ALERT_ADDED);
		LOGGER.info("Hazelcast device event processor started.");
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.sitewhere.rest.model.device.event.processor.DeviceEventProcessor#afterMeasurements
	 * (com.sitewhere.spi.device.event.IDeviceMeasurements)
	 */
	@Override
	public void afterMeasurements(IDeviceMeasurements measurements) throws SiteWhereException {
		DeviceMeasurements marshaled = DeviceMeasurements.copy(measurements);
		measurementsTopic.publish(marshaled);
		LOGGER.debug("Published measurements for: " + measurements.getId());
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.sitewhere.rest.model.device.event.processor.DeviceEventProcessor#afterLocation
	 * (com.sitewhere.spi.device.event.IDeviceLocation)
	 */
	@Override
	public void afterLocation(IDeviceLocation location) throws SiteWhereException {
		DeviceLocation marshaled = DeviceLocation.copy(location);
		locationsTopic.publish(marshaled);
		LOGGER.debug("Published location for: " + location.getId());
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.sitewhere.rest.model.device.event.processor.DeviceEventProcessor#afterAlert
	 * (com.sitewhere.spi.device.event.IDeviceAlert)
	 */
	@Override
	public void afterAlert(IDeviceAlert alert) throws SiteWhereException {
		DeviceAlert marshaled = DeviceAlert.copy(alert);
		alertsTopic.publish(marshaled);
		LOGGER.debug("Published alert for: " + alert.getId());
	}

	public SiteWhereHazelcastConfiguration getConfiguration() {
		return configuration;
	}

	public void setConfiguration(SiteWhereHazelcastConfiguration configuration) {
		this.configuration = configuration;
	}
}