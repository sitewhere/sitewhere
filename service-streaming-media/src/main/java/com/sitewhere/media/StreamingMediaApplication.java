/*
 * Copyright (c) SiteWhere, LLC. All rights reserved. http://www.sitewhere.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package com.sitewhere.media;

import com.sitewhere.media.spi.microservice.IStreamingMediaMicroservice;
import com.sitewhere.microservice.MicroserviceApplication;

/**
 * Spring Boot application for streaming media microservice.
 */
public class StreamingMediaApplication extends MicroserviceApplication<IStreamingMediaMicroservice> {

    private IStreamingMediaMicroservice microservice;

    /*
     * (non-Javadoc)
     * 
     * @see
     * com.sitewhere.microservice.spi.IMicroserviceApplication#getMicroservice()
     */
    @Override
    public IStreamingMediaMicroservice getMicroservice() {
	return microservice;
    }
}