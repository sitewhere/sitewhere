package com.sitewhere.rest.model.monitoring;

import com.sitewhere.spi.error.ErrorLevel;
import com.sitewhere.spi.monitoring.IProgressErrorMessage;

/**
 * Progress message that includes error information.
 * 
 * @author Derek
 */
public class ProgressErrorMessage extends ProgressMessage implements IProgressErrorMessage {

    /** Serial version UID */
    private static final long serialVersionUID = -7676418806779136207L;

    /** Error level for message */
    private ErrorLevel level;

    public ProgressErrorMessage() {
    }

    public ProgressErrorMessage(String taskName, double progressPercentage, String message, ErrorLevel level) {
	super(taskName, progressPercentage, message);
	this.level = level;
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.sitewhere.spi.monitoring.IProgressErrorMessage#getLevel()
     */
    @Override
    public ErrorLevel getLevel() {
	return level;
    }

    public void setLevel(ErrorLevel level) {
	this.level = level;
    }
}