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
import com.sitewhere.spi.device.event.IDeviceCommandInvocation;
import com.sitewhere.spi.device.provisioning.ICommandDeliveryProvider;

/**
 * Default implementation of {@link ICommandDeliveryProvider} interface.
 * 
 * @author Derek
 */
public class DefaultCommandDeliveryProvider implements ICommandDeliveryProvider {

	/** Static logger instance */
	private static Logger LOGGER = Logger.getLogger(DefaultCommandDeliveryProvider.class);

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.sitewhere.spi.device.provisioning.ICommandDeliveryProvider#deliver(com.sitewhere
	 * .spi.device.event.IDeviceCommandInvocation, byte[])
	 */
	@Override
	public void deliver(IDeviceCommandInvocation invocation, byte[] encoded) throws SiteWhereException {
		LOGGER.info("Default message delivery provider called.");
	}
}