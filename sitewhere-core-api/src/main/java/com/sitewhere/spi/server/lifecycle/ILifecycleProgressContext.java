package com.sitewhere.spi.server.lifecycle;

/**
 * Provides context for a monitoring a lifecycle operation.
 * 
 * @author Derek
 */
public interface ILifecycleProgressContext {

    /**
     * Get task name for context.
     * 
     * @return
     */
    public String getTaskName();

    /**
     * Get number of operations expected within context.
     * 
     * @return
     */
    public int getOperationCount();

    /**
     * Get index of current operation.
     * 
     * @return
     */
    public int getCurrentOperationIndex();

    /**
     * Set index of current operation.
     * 
     * @param index
     */
    public void setCurrentOperationIndex(int index);

    /**
     * Get message associated with current operation.
     * 
     * @return
     */
    public String getCurrentOperationMessage();

    /**
     * Set message associated with current operation.
     * 
     * @param message
     */
    public void setCurrentOperationMessage(String message);
}