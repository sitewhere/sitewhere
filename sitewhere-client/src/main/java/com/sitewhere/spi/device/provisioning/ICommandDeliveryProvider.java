/*
 * ICommandDeliveryProvider.java 
 * --------------------------------------------------------------------------------------
 * Copyright (c) Reveal Technologies, LLC. All rights reserved. http://www.reveal-tech.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package com.sitewhere.spi.device.provisioning;

import com.sitewhere.spi.SiteWhereException;
import com.sitewhere.spi.device.event.IDeviceCommandInvocation;

/**
 * Handles delivery of encoded command information on an underlying transport.
 * 
 * @author Derek
 */
public interface ICommandDeliveryProvider {

	/**
	 * Deliver the given encoded invocation. The original invocation is included since it
	 * may contain metadata important to the delivery mechanism.
	 * 
	 * @param invocation
	 * @param encoded
	 * @throws SiteWhereException
	 */
	public void deliver(IDeviceCommandInvocation invocation, byte[] encoded) throws SiteWhereException;
}