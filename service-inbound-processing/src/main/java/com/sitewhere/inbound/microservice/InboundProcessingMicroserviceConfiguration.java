/*
 * Copyright (c) SiteWhere, LLC. All rights reserved. http://www.sitewhere.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package com.sitewhere.inbound.microservice;

import com.sitewhere.inbound.spi.microservice.IInboundProcessingMicroservice;
import com.sitewhere.microservice.instance.InstanceSettings;
import com.sitewhere.spi.microservice.instance.IInstanceSettings;

/**
 * Spring bean configuration for microservice.
 */
public class InboundProcessingMicroserviceConfiguration {

    public IInboundProcessingMicroservice inboundProcessingMicroservice() {
	return new InboundProcessingMicroservice();
    }

    public IInstanceSettings instanceSettings() {
	return new InstanceSettings();
    }
}
