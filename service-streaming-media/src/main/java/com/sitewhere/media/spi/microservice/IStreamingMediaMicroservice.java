/*
 * Copyright (c) SiteWhere, LLC. All rights reserved. http://www.sitewhere.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package com.sitewhere.media.spi.microservice;

import com.sitewhere.media.configuration.StreamingMediaConfiguration;
import com.sitewhere.spi.microservice.MicroserviceIdentifier;
import com.sitewhere.spi.microservice.multitenant.IMultitenantMicroservice;

/**
 * Microservice that provides streaming media functionality.
 */
public interface IStreamingMediaMicroservice extends
	IMultitenantMicroservice<MicroserviceIdentifier, StreamingMediaConfiguration, IStreamingMediaTenantEngine> {
}