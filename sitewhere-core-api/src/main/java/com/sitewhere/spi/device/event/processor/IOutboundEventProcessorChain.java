/*
 * Copyright (c) SiteWhere, LLC. All rights reserved. http://www.sitewhere.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package com.sitewhere.spi.device.event.processor;

import java.util.List;

/**
 * Holds a list of {@link IOutboundEventProcessor} objects that are invoked in
 * order.
 * 
 * @author Derek
 */
public interface IOutboundEventProcessorChain extends IOutboundEventProcessor {

    /**
     * Sets the indicator for whether processing is enabled. NOTE: Events passed
     * to the chain while processing is disabled are thrown away.
     * 
     * @param enabled
     */
    public void setProcessingEnabled(boolean enabled);

    /**
     * Indicates whether processing is enabled.
     * 
     * @return
     */
    public boolean isProcessingEnabled();

    /**
     * Get the list of chained processors.
     * 
     * @return
     */
    public List<IOutboundEventProcessor> getProcessors();
}