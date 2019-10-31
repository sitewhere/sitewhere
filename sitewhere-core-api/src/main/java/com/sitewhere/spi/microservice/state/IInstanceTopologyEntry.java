/*
 * Copyright (c) SiteWhere, LLC. All rights reserved. http://www.sitewhere.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package com.sitewhere.spi.microservice.state;

import java.util.Map;

/**
 * Entry that contains all microservices for a given module identifier.
 */
public interface IInstanceTopologyEntry {

    /**
     * Get map of microservices by hostname.
     * 
     * @return
     */
    public Map<String, IInstanceMicroservice> getMicroservicesByHostname();
}