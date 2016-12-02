package com.sitewhere.rest.model.monitoring;

import com.sitewhere.spi.monitoring.IProgressMessage;
import com.sitewhere.spi.server.lifecycle.ILifecycleProgressMonitor;

/**
 * Contains progress information provided by an
 * {@link ILifecycleProgressMonitor}.
 * 
 * @author Derek
 *
 */
public class ProgressMessage implements IProgressMessage {

    /** Task name */
    private String taskName;

    /** Total operation count */
    private int totalOperations;

    /** Current operation index */
    private int currentOperation;

    /** Operation message */
    private String message;

    public ProgressMessage() {
    }

    public ProgressMessage(String taskName, int totalOperations, int currentOperation, String message) {
	this.taskName = taskName;
	this.totalOperations = totalOperations;
	this.currentOperation = currentOperation;
	this.message = message;
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.sitewhere.spi.monitoring.IProgressMessage#getTaskName()
     */
    public String getTaskName() {
	return taskName;
    }

    public void setTaskName(String taskName) {
	this.taskName = taskName;
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.sitewhere.spi.monitoring.IProgressMessage#getTotalOperations()
     */
    public int getTotalOperations() {
	return totalOperations;
    }

    public void setTotalOperations(int totalOperations) {
	this.totalOperations = totalOperations;
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.sitewhere.spi.monitoring.IProgressMessage#getCurrentOperation()
     */
    public int getCurrentOperation() {
	return currentOperation;
    }

    public void setCurrentOperation(int currentOperation) {
	this.currentOperation = currentOperation;
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.sitewhere.spi.monitoring.IProgressMessage#getMessage()
     */
    public String getMessage() {
	return message;
    }

    public void setMessage(String message) {
	this.message = message;
    }
}