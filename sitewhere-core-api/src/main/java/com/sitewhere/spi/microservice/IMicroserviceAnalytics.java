/*
 * Copyright (c) SiteWhere, LLC. All rights reserved. http://www.sitewhere.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package com.sitewhere.spi.microservice;

import com.sitewhere.spi.SiteWhereException;

/**
 * Sends runtime information to Google Analytics.
 */
public interface IMicroserviceAnalytics {

    /**
     * Send event indicating microservice started.
     * 
     * @param microservice
     * @throws SiteWhereException
     */
    public void sendMicroserviceStarted(IMicroservice<?> microservice) throws SiteWhereException;

    /**
     * Send event indicating time since microservice started.
     * 
     * @param microservice
     * @throws SiteWhereException
     */
    public void sendMicroserviceUptime(IMicroservice<?> microservice) throws SiteWhereException;

    /**
     * Send event indicating microservice stopped.
     * 
     * @param microservice
     * @throws SiteWhereException
     */
    public void sendMicroserviceStopped(IMicroservice<?> microservice) throws SiteWhereException;
}