package com.sitewhere.spi.device.communication;

import com.sitewhere.spi.SiteWhereException;
import com.sitewhere.spi.server.lifecycle.ITenantLifecycleComponent;

/**
 * Provides support for filtering events that are duplicates of events already
 * in the system. The deduplication logic is implemented in subclasses.
 * 
 * @author Derek
 */
public interface IDeviceEventDeduplicator extends ITenantLifecycleComponent {

    /**
     * Detects whether the given device event is a duplicate of another event in
     * the system.
     * 
     * @param request
     * @return
     * @throws SiteWhereException
     */
    public boolean isDuplicate(IDecodedDeviceRequest<?> request) throws SiteWhereException;
}