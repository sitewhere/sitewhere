/*
 * Copyright (c) SiteWhere, LLC. All rights reserved. http://www.sitewhere.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package com.sitewhere.batch.configuration.manager;

/**
 * Batch operation manager configuration.
 */
public class BatchOperationManagerConfiguration {

    /** Throttle delay in milliseconds */
    private long throttleDelayMs;

    public long getThrottleDelayMs() {
	return throttleDelayMs;
    }

    public void setThrottleDelayMs(long throttleDelayMs) {
	this.throttleDelayMs = throttleDelayMs;
    }
}
