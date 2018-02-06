/*
 * Copyright (c) SiteWhere, LLC. All rights reserved. http://www.sitewhere.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package com.sitewhere.search.microservice;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.sitewhere.search.spi.microservice.IEventSearchMicroservice;

/**
 * Spring bean configuration for microservice.
 * 
 * @author Derek
 */
@Configuration
public class EventSearchMicroserviceConfiguration {

    @Bean
    public IEventSearchMicroservice eventSearchMicroservice() {
	return new EventSearchMicroservice();
    }
}