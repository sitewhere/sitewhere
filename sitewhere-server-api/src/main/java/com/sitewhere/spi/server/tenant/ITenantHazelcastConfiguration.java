/*
 * Copyright (c) SiteWhere, LLC. All rights reserved. http://www.sitewhere.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package com.sitewhere.spi.server.tenant;

import com.hazelcast.core.HazelcastInstance;
import com.sitewhere.spi.server.lifecycle.ITenantLifecycleComponent;

/**
 * Allows direct access to the Hazelcast instance used by tenant.
 * 
 * @author Derek
 */
public interface ITenantHazelcastConfiguration extends ITenantLifecycleComponent {

	/**
	 * Get a handle to the Hazelcast instance.
	 * 
	 * @return
	 */
	public HazelcastInstance getHazelcastInstance();
}