/*
 * PlaceholderEventProcessor.java 
 * --------------------------------------------------------------------------------------
 * Copyright (c) Reveal Technologies, LLC. All rights reserved. http://www.reveal-tech.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package com.sitewhere.server.device.event.processor;

import org.apache.log4j.Logger;

import com.sitewhere.spi.SiteWhereException;
import com.sitewhere.spi.device.IDeviceAssignment;
import com.sitewhere.spi.device.event.IDeviceAlert;
import com.sitewhere.spi.device.event.IDeviceLocation;
import com.sitewhere.spi.device.event.IDeviceMeasurements;
import com.sitewhere.spi.device.event.processor.IDeviceEventProcessor;
import com.sitewhere.spi.device.event.request.IDeviceAlertCreateRequest;
import com.sitewhere.spi.device.event.request.IDeviceLocationCreateRequest;
import com.sitewhere.spi.device.event.request.IDeviceMeasurementsCreateRequest;

/**
 * Event processor that logs when methods are called.
 * 
 * @author Derek
 */
public class PlaceholderEventProcessor implements IDeviceEventProcessor {

	/** Logger instance */
	private static Logger LOGGER = Logger.getLogger(PlaceholderEventProcessor.class);

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.sitewhere.spi.device.event.processor.IDeviceEventProcessor#beforeMeasurements
	 * (com.sitewhere.spi.device.IDeviceAssignment,
	 * com.sitewhere.spi.device.event.request.IDeviceMeasurementsCreateRequest)
	 */
	@Override
	public void beforeMeasurements(IDeviceAssignment assignment, IDeviceMeasurementsCreateRequest request)
			throws SiteWhereException {
		LOGGER.info("Calling 'beforeMeasurements' in event processor.");
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.sitewhere.spi.device.event.processor.IDeviceEventProcessor#afterMeasurements
	 * (com.sitewhere.spi.device.event.IDeviceMeasurements)
	 */
	@Override
	public void afterMeasurements(IDeviceMeasurements measurements) throws SiteWhereException {
		LOGGER.info("Calling 'afterMeasurements' in event processor.");
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.sitewhere.spi.device.event.processor.IDeviceEventProcessor#beforeLocation(com
	 * .sitewhere.spi.device.IDeviceAssignment,
	 * com.sitewhere.spi.device.event.request.IDeviceLocationCreateRequest)
	 */
	@Override
	public void beforeLocation(IDeviceAssignment assignment, IDeviceLocationCreateRequest request)
			throws SiteWhereException {
		LOGGER.info("Calling 'beforeLocation' in event processor.");
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.sitewhere.spi.device.event.processor.IDeviceEventProcessor#afterLocation(com
	 * .sitewhere.spi.device.event.IDeviceLocation)
	 */
	@Override
	public void afterLocation(IDeviceLocation location) throws SiteWhereException {
		LOGGER.info("Calling 'afterLocation' in event processor.");
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.sitewhere.spi.device.event.processor.IDeviceEventProcessor#beforeAlert(com.
	 * sitewhere.spi.device.IDeviceAssignment,
	 * com.sitewhere.spi.device.event.request.IDeviceAlertCreateRequest)
	 */
	@Override
	public void beforeAlert(IDeviceAssignment assignment, IDeviceAlertCreateRequest request)
			throws SiteWhereException {
		LOGGER.info("Calling 'beforeAlert' in event processor.");
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.sitewhere.spi.device.event.processor.IDeviceEventProcessor#afterAlert(com.sitewhere
	 * .spi.device.event.IDeviceAlert)
	 */
	@Override
	public void afterAlert(IDeviceAlert alert) throws SiteWhereException {
		LOGGER.info("Calling 'afterAlert' in event processor.");
	}
}