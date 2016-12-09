package com.sitewhere.server.lifecycle;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.sitewhere.spi.SiteWhereException;
import com.sitewhere.spi.monitoring.IProgressMessage;
import com.sitewhere.spi.server.lifecycle.ILifecycleProgressMonitor;

/**
 * Default implementation of {@link ILifecycleProgressMonitor}.
 * 
 * @author Derek
 */
public class LifecycleProgressMonitor implements ILifecycleProgressMonitor {

    /** Static logger instance */
    private static Logger LOGGER = LogManager.getLogger();

    /*
     * (non-Javadoc)
     * 
     * @see com.sitewhere.spi.monitoring.IProgressReporter#reportProgress(com.
     * sitewhere.spi.monitoring.IProgressMessage)
     */
    @Override
    public void reportProgress(IProgressMessage message) throws SiteWhereException {
	LOGGER.info("[" + message.getTaskName() + "]: (" + message.getCurrentOperation() + "/"
		+ message.getTotalOperations() + ") " + message.getMessage());
    }
}