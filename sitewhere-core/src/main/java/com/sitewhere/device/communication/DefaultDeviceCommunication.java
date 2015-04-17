/*
 * Copyright (c) SiteWhere, LLC. All rights reserved. http://www.sitewhere.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package com.sitewhere.device.communication;

import org.apache.log4j.Logger;

import com.sitewhere.server.batch.BatchOperationManager;
import com.sitewhere.spi.SiteWhereException;
import com.sitewhere.spi.device.communication.IDeviceCommunication;

/**
 * Default implementation of the {@link IDeviceCommunication} interface.
 * 
 * @author Derek
 */
public class DefaultDeviceCommunication extends DeviceCommunication {

	/** Static logger instance */
	private static Logger LOGGER = Logger.getLogger(DefaultDeviceCommunication.class);

	public DefaultDeviceCommunication() {
		setRegistrationManager(new RegistrationManager());
		setBatchOperationManager(new BatchOperationManager());
		setDeviceStreamManager(new DeviceStreamManager());
		setInboundProcessingStrategy(new BlockingQueueInboundProcessingStrategy());
		setCommandProcessingStrategy(new DefaultCommandProcessingStrategy());
		setOutboundProcessingStrategy(new BlockingQueueOutboundProcessingStrategy());
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.sitewhere.device.communication.DeviceCommunication#start()
	 */
	@Override
	public void start() throws SiteWhereException {
		super.start();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.sitewhere.server.lifecycle.LifecycleComponent#getComponentName()
	 */
	@Override
	public String getComponentName() {
		return "CE Device Communication Subsystem";
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
	 * @see com.sitewhere.device.communication.DeviceCommunication#stop()
	 */
	@Override
	public void stop() throws SiteWhereException {
		LOGGER.info("Stopping CE device communication.");
		super.stop();
		LOGGER.info("Completed CE device communication shutdown.");
	}
}