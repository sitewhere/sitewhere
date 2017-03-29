package com.sitewhere.server.lifecycle;

import com.sitewhere.spi.server.lifecycle.ILifecycleProgressContext;
import com.sitewhere.spi.server.lifecycle.ILifecycleProgressMonitor;

/**
 * Provides context for a nesting level in an {@link ILifecycleProgressMonitor}.
 * 
 * @author Derek
 */
public class LifecycleProgressContext implements ILifecycleProgressContext {

    /** Get task name */
    private String taskName;

    /** Count of total operations for context */
    private int operationCount;

    /** Current operations index */
    private int currentOperationIndex;

    /** Current operation message */
    private String currentOperationMessage;

    public LifecycleProgressContext(int operationCount, String taskName) {
	this.operationCount = operationCount;
	this.taskName = taskName;
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * com.sitewhere.spi.server.lifecycle.ILifecycleProgressContext#getTaskName(
     * )
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
     * @see com.sitewhere.spi.server.lifecycle.ILifecycleProgressContext#
     * getOperationCount()
     */
    @Override
    public int getOperationCount() {
	return operationCount;
    }

    public void setOperationCount(int operationCount) {
	this.operationCount = operationCount;
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.sitewhere.spi.server.lifecycle.ILifecycleProgressContext#
     * getCurrentOperationIndex()
     */
    @Override
    public int getCurrentOperationIndex() {
	return currentOperationIndex;
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.sitewhere.spi.server.lifecycle.ILifecycleProgressContext#
     * setCurrentOperationIndex(int)
     */
    public void setCurrentOperationIndex(int currentOperationIndex) {
	this.currentOperationIndex = currentOperationIndex;
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.sitewhere.spi.server.lifecycle.ILifecycleProgressContext#
     * getCurrentOperationMessage()
     */
    @Override
    public String getCurrentOperationMessage() {
	return currentOperationMessage;
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.sitewhere.spi.server.lifecycle.ILifecycleProgressContext#
     * setCurrentOperationMessage(java.lang.String)
     */
    public void setCurrentOperationMessage(String currentOperationMessage) {
	this.currentOperationMessage = currentOperationMessage;
    }
}