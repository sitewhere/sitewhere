/*
 * Copyright (c) SiteWhere, LLC. All rights reserved. http://www.sitewhere.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package com.sitewhere.spi.microservice;

/**
 * Common interface for Spring Boot applications that wrap SiteWhere
 * microservices.
 *
 * @param <T>
 */
public interface IMicroserviceApplication<T extends IMicroservice<?>> {

    /**
     * Get wrapped microservice.
     * 
     * @return
     */
    public T getMicroservice();
}