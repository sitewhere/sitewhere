package com.sitewhere.rest.model.monitoring;

import com.sitewhere.spi.monitoring.IProgressMessage;
import com.sitewhere.spi.server.lifecycle.ILifecycleProgressMonitor;

/**
 * Contains progress information provided by an
 * {@link ILifecycleProgressMonitor}.
 * 
 * @author Derek
 */
public class ProgressMessage implements IProgressMessage {

    /** Serial version UID */
    private static final long serialVersionUID = 293429181916222135L;

    /** Task name */
    private String taskName;

    /** Percentage complete (between 0 and 100) */
    private double progressPercentage;

    /** Operation message */
    private String message;

    public ProgressMessage() {
    }

    public ProgressMessage(String taskName, double progressPercentage, String message) {
	this.taskName = taskName;
	this.progressPercentage = progressPercentage;
	this.message = message;
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.sitewhere.spi.monitoring.IProgressMessage#getTaskName()
     */
    @Override
    public String getTaskName() {
	return taskName;
    }

    public void setTaskName(String taskName) {
	this.taskName = taskName;
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * com.sitewhere.spi.monitoring.IProgressMessage#getProgressPercentage()
     */
    @Override
    public double getProgressPercentage() {
	return progressPercentage;
    }

    public void setProgressPercentage(double progressPercentage) {
	this.progressPercentage = progressPercentage;
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.sitewhere.spi.monitoring.IProgressMessage#getMessage()
     */
    @Override
    public String getMessage() {
	return message;
    }

    public void setMessage(String message) {
	this.message = message;
    }
}