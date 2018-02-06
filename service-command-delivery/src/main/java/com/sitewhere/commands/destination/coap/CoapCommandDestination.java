/*
 * Copyright (c) SiteWhere, LLC. All rights reserved. http://www.sitewhere.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package com.sitewhere.commands.destination.coap;

import com.sitewhere.commands.destination.CommandDestination;

/**
 * Command destination that makes a CoAP client request to send command data.
 * 
 * @author Derek
 */
public class CoapCommandDestination extends CommandDestination<byte[], CoapParameters> {

    public CoapCommandDestination() {
	setCommandDeliveryParameterExtractor(new MetadataCoapParameterExtractor());
    }
}