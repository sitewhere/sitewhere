/*
 * Copyright (c) SiteWhere, LLC. All rights reserved. http://www.sitewhere.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package com.sitewhere.spi.scheduling;

/**
 * Indicates state of a scheduled job.
 * 
 * @author Derek
 */
public enum ScheduledJobState {

    /** Job not yet submitted to scheduler */
    Unsubmitted,

    /** Job submitted to scheduler */
    Active,

    /** Job complete */
    Complete;
}