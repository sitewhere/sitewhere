/*
 * Copyright (c) SiteWhere, LLC. All rights reserved. http://www.sitewhere.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package com.sitewhere.commands.destination.sms;

import com.sitewhere.commands.destination.CommandDestination;
import com.sitewhere.commands.encoding.string.JsonStringCommandExecutionEncoder;
import com.sitewhere.commands.spi.ICommandDeliveryProvider;
import com.sitewhere.commands.spi.ICommandDestination;

/**
 * Implementation of {@link ICommandDestination} that encodes and delivers
 * messages that are strings and {@link ICommandDeliveryProvider} requires
 * {@link SmsParameters}.
 * 
 * @author Derek
 */
public class SmsCommandDestination extends CommandDestination<String, SmsParameters> {

    public SmsCommandDestination() {
	setCommandExecutionEncoder(new JsonStringCommandExecutionEncoder());
	setCommandDeliveryParameterExtractor(new SmsParameterExtractor());
    }
}