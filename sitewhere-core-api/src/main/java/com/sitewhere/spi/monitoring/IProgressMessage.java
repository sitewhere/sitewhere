package com.sitewhere.spi.monitoring;

import java.io.Serializable;

/**
 * Message sent to indicate progress for a long-running task.
 * 
 * @author Derek
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