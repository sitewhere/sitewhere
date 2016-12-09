package com.sitewhere.spi.server;

import com.sitewhere.spi.SiteWhereException;
import com.sitewhere.spi.server.lifecycle.ILifecycleProgressMonitor;

/**
 * Helps with migration of previous server versions to provide compatibility for
 * users that drop the new WAR into an existing server instance.
 * 
 * @author Derek
 */
public interface IBackwardCompatibilityService {

    /**
     * Actions executed before server is initialized.
     * 
     * @param monitor
     * @throws SiteWhereException
     */
    public void beforeServerInitialize(ILifecycleProgressMonitor monitor) throws SiteWhereException;

    /**
     * Actions executed before server is started.
     * 
     * @param monitor
     * @throws SiteWhereException
     */
    public void beforeServerStart(ILifecycleProgressMonitor monitor) throws SiteWhereException;

    /**
     * Actions executed after server is started.
     * 
     * @param monitor
     * @throws SiteWhereException
     */
    public void afterServerStart(ILifecycleProgressMonitor monitor) throws SiteWhereException;
}