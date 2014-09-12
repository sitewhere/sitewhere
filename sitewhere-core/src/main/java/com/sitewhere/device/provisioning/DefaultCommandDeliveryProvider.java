/*
 * DefaultCommandDeliveryProvider.java 
 * --------------------------------------------------------------------------------------
 * Copyright (c) Reveal Technologies, LLC. All rights reserved. http://www.reveal-tech.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package com.sitewhere.device.provisioning;

import org.apache.log4j.Logger;

import com.sitewhere.spi.SiteWhereException;
import com.sitewhere.spi.device.IDeviceAssignment;
import com.sitewhere.spi.device.IDeviceNestingContext;
import com.sitewhere.spi.device.command.IDeviceCommandExecution;
import com.sitewhere.spi.device.provisioning.ICommandDeliveryProvider;

/**
 * Default implementation of {@link ICommandDeliveryProvider} interface that transmits
 * binary data stored in a byte array.
 * 
 * @author Derek
 */
public class DefaultCommandDeliveryProvider implements ICommandDeliveryProvider<byte[]> {

	/** Static logger instance */
	private static Logger LOGGER = Logger.getLogger(DefaultCommandDeliveryProvider.class);

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.sitewhere.spi.device.provisioning.ICommandDeliveryProvider#deliver(com.sitewhere
	 * .spi.device.IDeviceNestingContext, com.sitewhere.spi.device.IDeviceAssignment,
	 * com.sitewhere.spi.device.command.IDeviceCommandExecution, java.lang.Object)
	 */
	@Override
	public void deliver(IDeviceNestingContext nested, IDeviceAssignment assignment,
			IDeviceCommandExecution execution, byte[] encoded) throws SiteWhereException {
		LOGGER.info("Invoked default command delivery provider with command invocation.");
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.sitewhere.spi.device.provisioning.ICommandDeliveryProvider#deliverSystemCommand
	 * (com.sitewhere.spi.device.IDeviceNestingContext,
	 * com.sitewhere.spi.device.IDeviceAssignment, byte[])
	 */
	@Override
	public void deliverSystemCommand(IDeviceNestingContext nested, IDeviceAssignment assignment,
			byte[] encoded) throws SiteWhereException {
		LOGGER.info("Invoked default command delivery provider with system command.");
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.sitewhere.spi.ISiteWhereLifecycle#start()
	 */
	@Override
	public void start() throws SiteWhereException {
		LOGGER.info("Started dummy delivery provider.");
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.sitewhere.spi.ISiteWhereLifecycle#stop()
	 */
	@Override
	public void stop() throws SiteWhereException {
		LOGGER.info("Stopped dummy delivery provider.");
	}
}