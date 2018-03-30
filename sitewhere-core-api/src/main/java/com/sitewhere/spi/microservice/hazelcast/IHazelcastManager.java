/*
 * Copyright (c) SiteWhere, LLC. All rights reserved. http://www.sitewhere.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package com.sitewhere.spi.microservice.hazelcast;

import com.sitewhere.spi.server.lifecycle.ILifecycleComponent;

/**
 * Manager for a Hazelcast instance.
 * 
 * @author Derek
 */
public interface IHazelcastManager extends ILifecycleComponent, IHazelcastProvider {

    /** Hazelcast communication port */
    public static final int HZ_PORT = 5701;

    /** Group name */
    public static final String GROUP_NAME = "sitewhere";

    /** Group password */
    public static final String GROUP_PASSWORD = "sitewhere";
}