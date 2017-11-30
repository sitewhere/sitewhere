/*
 * Copyright (c) SiteWhere, LLC. All rights reserved. http://www.sitewhere.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package com.sitewhere.spi.microservice.state;

/**
 * Enumerates instance topology update types.
 * 
 * @author Derek
 */
public enum InstanceTopologyUpdateType {

    /** Microservice started */
    MicroserviceStarted,

    /** Microservice stopped */
    MicroserviceStopped,

    /** Microservice unresponsive */
    MicroserviceUnresponsive;
}