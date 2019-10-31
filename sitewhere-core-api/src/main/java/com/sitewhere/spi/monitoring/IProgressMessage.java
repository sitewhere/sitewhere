/*
 * Copyright (c) SiteWhere, LLC. All rights reserved. http://www.sitewhere.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package com.sitewhere.spi.monitoring;

import java.io.Serializable;

/**
 * Message sent to indicate progress for a long-running task.
 */
public interface IProgressMessage extends Serializable {

    /**
     * Get name of overall task being monitored.
     * 
     * @return
     */
    public String getTaskName();

    /**
     * Get progress value as a number between 0 and 100.
     * 
     * @return
     */
    public double getProgressPercentage();

    /**
     * Get message shown for current operation.
     * 
     * @return
     */
    public String getMessage();

    /**
     * Get timestamp for message.
     * 
     * @return
     */
    public Long getTimeStamp();
}