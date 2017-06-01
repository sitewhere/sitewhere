package com.sitewhere.spi.monitoring;

import com.sitewhere.spi.error.ErrorLevel;

/**
 * Extends {@link IProgressMessage} to add error information.
 * 
 * @author Derek
 */
public interface IProgressErrorMessage extends IProgressMessage {

    /**
     * Get error level associated with message.
     * 
     * @return
     */
    public ErrorLevel getLevel();
}