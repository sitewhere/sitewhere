/*
 * DefaultProvisioningEventProcessor.java 
 * --------------------------------------------------------------------------------------
 * Copyright (c) Reveal Technologies, LLC. All rights reserved. http://www.reveal-tech.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package com.sitewhere.device.provisioning;

import com.sitewhere.rest.model.device.event.processor.DeviceEventProcessor;
import com.sitewhere.server.SiteWhereServer;
import com.sitewhere.spi.SiteWhereException;
import com.sitewhere.spi.device.event.IDeviceCommandInvocation;

/**
 * Event processor that hands off {@link IDeviceCommandInvocation} events after they have
 * been saved so that provisioning can process them.
 * 
 * @author Derek
 */
public class DefaultProvisioningEventProcessor extends DeviceEventProcessor {

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.sitewhere.rest.model.device.event.processor.DeviceEventProcessor#
	 * afterCommandInvocation(com.sitewhere.spi.device.event.IDeviceCommandInvocation)
	 */
	@Override
	public void afterCommandInvocation(IDeviceCommandInvocation invocation) throws SiteWhereException {
		SiteWhereServer.getInstance().getDeviceProvisioning().deliver(invocation);
	}
}