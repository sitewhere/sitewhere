/*
 * Copyright (c) SiteWhere, LLC. All rights reserved. http://www.sitewhere.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package com.sitewhere.communication.hazelcast;

import com.hazelcast.core.HazelcastInstance;
import com.sitewhere.spi.server.lifecycle.ILifecycleComponent;

/**
 * Wraps Hazelcast instance configuration.
 * 
 * @author Derek
 */
public interface IHazelcastConfiguration extends ILifecycleComponent {

    /**
     * Get Hazelcast instance for this node.
     * 
     * @return
     */
    public HazelcastInstance getHazelcastInstance();
}
