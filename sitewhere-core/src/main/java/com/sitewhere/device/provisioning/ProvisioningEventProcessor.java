/*
 * Copyright (c) SiteWhere, LLC. All rights reserved. http://www.sitewhere.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package com.sitewhere.device.provisioning;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.apache.log4j.Logger;

import com.sitewhere.SiteWhere;
import com.sitewhere.device.event.processor.OutboundEventProcessor;
import com.sitewhere.spi.SiteWhereException;
import com.sitewhere.spi.device.event.IDeviceCommandInvocation;

/**
 * Event processor that hands off {@link IDeviceCommandInvocation} events after they have
 * been saved so that provisioning can process them.
 * 
 * @author Derek
 */
public class ProvisioningEventProcessor extends OutboundEventProcessor {

	/** Static logger instance */
	private static Logger LOGGER = Logger.getLogger(ProvisioningEventProcessor.class);

	/** Number of invocations to buffer before blocking calls */
	private static final int BUFFER_SIZE = 100;

	/** Bounded queue that holds documents to be processed */
	private BlockingQueue<IDeviceCommandInvocation> queue = new ArrayBlockingQueue<IDeviceCommandInvocation>(
			BUFFER_SIZE);

	/** Used to execute Solr indexing in a separate thread */
	private ExecutorService executor = Executors.newSingleThreadExecutor();

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.sitewhere.rest.model.device.event.processor.OutboundEventProcessor#start()
	 */
	@Override
	public void start() throws SiteWhereException {
		executor.execute(new ProvisioningProcessor());
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.sitewhere.spi.server.lifecycle.ILifecycleComponent#getLogger()
	 */
	@Override
	public Logger getLogger() {
		return LOGGER;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.sitewhere.rest.model.device.event.processor.OutboundEventProcessor#stop()
	 */
	@Override
	public void stop() throws SiteWhereException {
		executor.shutdown();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.sitewhere.rest.model.device.event.processor.OutboundEventProcessor#
	 * onCommandInvocation(com.sitewhere.spi.device.event.IDeviceCommandInvocation)
	 */
	@Override
	public void onCommandInvocation(IDeviceCommandInvocation invocation) throws SiteWhereException {
		try {
			queue.put(invocation);
		} catch (InterruptedException e) {
			throw new SiteWhereException("Interrupted while processing command invocation.", e);
		}
	}

	/**
	 * Processes provisioning operations asynchronously.
	 */
	private class ProvisioningProcessor implements Runnable {

		@Override
		public void run() {
			while (true) {
				try {
					IDeviceCommandInvocation invocation = queue.take();
					try {
						LOGGER.debug("Provisioning processor thread picked up invocation.");
						SiteWhere.getServer().getDeviceProvisioning().deliverCommand(invocation);
					} catch (SiteWhereException e) {
						LOGGER.error("Exception thrown in provisioning operation.", e);
					} catch (Throwable e) {
						LOGGER.error("Unhandled exception in provisioning operation.", e);
					}
				} catch (InterruptedException e) {
					LOGGER.error(e);
				}
			}
		}
	}
}