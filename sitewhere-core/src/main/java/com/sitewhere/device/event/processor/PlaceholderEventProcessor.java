/*
 * PlaceholderEventProcessor.java 
 * --------------------------------------------------------------------------------------
 * Copyright (c) Reveal Technologies, LLC. All rights reserved. http://www.reveal-tech.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package com.sitewhere.device.event.processor;

import org.apache.log4j.Logger;

import com.sitewhere.spi.SiteWhereException;
import com.sitewhere.spi.device.event.IDeviceAlert;
import com.sitewhere.spi.device.event.IDeviceCommandInvocation;
import com.sitewhere.spi.device.event.IDeviceCommandResponse;
import com.sitewhere.spi.device.event.IDeviceLocation;
import com.sitewhere.spi.device.event.IDeviceMeasurements;
import com.sitewhere.spi.device.event.processor.IOutboundEventProcessor;

/**
 * Event processor that logs when methods are called.
 * 
 * @author Derek
 */
public class PlaceholderEventProcessor implements IOutboundEventProcessor {

	/** Logger instance */
	private static Logger LOGGER = Logger.getLogger(PlaceholderEventProcessor.class);

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.sitewhere.spi.device.event.processor.IDeviceEventProcessor#start()
	 */
	@Override
	public void start() throws SiteWhereException {
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.sitewhere.spi.device.event.processor.IDeviceEventProcessor#stop()
	 */
	@Override
	public void stop() throws SiteWhereException {
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.sitewhere.spi.device.event.processor.IOutboundEventProcessor#onMeasurements
	 * (com.sitewhere.spi.device.event.IDeviceMeasurements)
	 */
	@Override
	public void onMeasurements(IDeviceMeasurements measurements) throws SiteWhereException {
		LOGGER.info("Calling 'afterMeasurements' in event processor.");
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.sitewhere.spi.device.event.processor.IOutboundEventProcessor#onLocation(com
	 * .sitewhere.spi.device.event.IDeviceLocation)
	 */
	@Override
	public void onLocation(IDeviceLocation location) throws SiteWhereException {
		LOGGER.info("Calling 'afterLocation' in event processor.");
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.sitewhere.spi.device.event.processor.IOutboundEventProcessor#onAlert(com.sitewhere
	 * .spi.device.event.IDeviceAlert)
	 */
	@Override
	public void onAlert(IDeviceAlert alert) throws SiteWhereException {
		LOGGER.info("Calling 'afterAlert' in event processor.");
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.sitewhere.spi.device.event.processor.IOutboundEventProcessor#onCommandInvocation
	 * (com.sitewhere.spi.device.event.IDeviceCommandInvocation)
	 */
	@Override
	public void onCommandInvocation(IDeviceCommandInvocation invocation) throws SiteWhereException {
		LOGGER.info("Calling 'afterCommandInvocation' in event processor.");
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.sitewhere.spi.device.event.processor.IOutboundEventProcessor#onCommandResponse
	 * (com.sitewhere.spi.device.event.IDeviceCommandResponse)
	 */
	@Override
	public void onCommandResponse(IDeviceCommandResponse response) throws SiteWhereException {
		LOGGER.info("Calling 'afterCommandResponse' in event processor.");
	}
}