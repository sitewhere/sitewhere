/*
 * DefaultInboundEventProcessorChain.java 
 * --------------------------------------------------------------------------------------
 * Copyright (c) Reveal Technologies, LLC. All rights reserved. http://www.reveal-tech.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package com.sitewhere.device.event.processor;

import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;

import com.sitewhere.spi.SiteWhereException;
import com.sitewhere.spi.device.event.processor.IInboundEventProcessor;
import com.sitewhere.spi.device.event.processor.IInboundEventProcessorChain;
import com.sitewhere.spi.device.event.request.IDeviceRegistrationRequest;

/**
 * Default implementation of {@link IInboundEventProcessorChain} interface.
 * 
 * @author Derek
 */
public class DefaultInboundEventProcessorChain implements IInboundEventProcessorChain {

	/** Static logger instance */
	private static Logger LOGGER = Logger.getLogger(DefaultInboundEventProcessorChain.class);

	/** List of processors */
	private List<IInboundEventProcessor> processors = new ArrayList<IInboundEventProcessor>();

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.sitewhere.spi.ISiteWhereLifecycle#start()
	 */
	@Override
	public void start() throws SiteWhereException {
		LOGGER.info("Inbound event processor chain starting...");
		for (IInboundEventProcessor processor : getProcessors()) {
			processor.start();
		}
		LOGGER.info("Inbound event processor chain started.");
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.sitewhere.spi.ISiteWhereLifecycle#stop()
	 */
	@Override
	public void stop() throws SiteWhereException {
		LOGGER.info("Inbound event processor chain stopping...");
		for (IInboundEventProcessor processor : getProcessors()) {
			processor.stop();
		}
		LOGGER.info("Inbound event processor chain stopped.");
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.sitewhere.spi.device.event.processor.IInboundEventProcessor#onRegistrationRequest
	 * (com.sitewhere.spi.device.event.request.IDeviceRegistrationCreateRequest)
	 */
	@Override
	public void onRegistrationRequest(IDeviceRegistrationRequest request) throws SiteWhereException {
		for (IInboundEventProcessor processor : getProcessors()) {
			try {
				processor.onRegistrationRequest(request);
			} catch (SiteWhereException e) {
				LOGGER.error(e);
			}
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.sitewhere.spi.device.event.processor.IInboundEventProcessorChain#getProcessors
	 * ()
	 */
	public List<IInboundEventProcessor> getProcessors() {
		return processors;
	}

	public void setProcessors(List<IInboundEventProcessor> processors) {
		this.processors = processors;
	}
}