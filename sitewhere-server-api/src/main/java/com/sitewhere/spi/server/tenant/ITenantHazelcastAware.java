/*
 * Copyright (c) SiteWhere, LLC. All rights reserved. http://www.sitewhere.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package com.sitewhere.spi.server.tenant;

/**
 * Marker interface for components interested in the tenant Hazelcast configuration.
 * 
 * @author Derek
 */
public interface ITenantHazelcastAware {

	/**
	 * Set the tenant Hazelcast configuration.
	 * 
	 * @param configuration
	 */
	public void setHazelcastConfiguration(ITenantHazelcastConfiguration configuration);
}