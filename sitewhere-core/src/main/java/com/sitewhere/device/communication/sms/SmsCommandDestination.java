/*
 * Copyright (c) SiteWhere, LLC. All rights reserved. http://www.sitewhere.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package com.sitewhere.device.communication.sms;

import com.sitewhere.device.communication.CommandDestination;
import com.sitewhere.spi.device.communication.ICommandDeliveryProvider;
import com.sitewhere.spi.device.communication.ICommandDestination;

/**
 * Implementation of {@link ICommandDestination} that encodes and delivers
 * messages that are strings and {@link ICommandDeliveryProvider} requires
 * {@link SmsParameters}.
 * 
 * @author Derek
 */
public class SmsCommandDestination extends CommandDestination<String, SmsParameters> {
}