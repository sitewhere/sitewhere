/*
 * Copyright (c) SiteWhere, LLC. All rights reserved. http://www.sitewhere.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package com.sitewhere.outbound.siddhi;

import java.util.Arrays;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.wso2.siddhi.core.event.Event;
import org.wso2.siddhi.core.stream.output.StreamCallback;

/**
 * Implementation of {@link StreamCallback} that writes events to the logger.
 * 
 * @author Derek
 */
public class StreamDebugger extends StreamCallback {

    /** Static logger instance */
    private static Logger LOGGER = LogManager.getLogger();

    /*
     * (non-Javadoc)
     * 
     * @see
     * org.wso2.siddhi.core.stream.output.StreamCallback#receive(org.wso2.siddhi
     * .core. event.Event[])
     */
    @Override
    public void receive(Event[] events) {
	LOGGER.info(Arrays.deepToString(events));
    }
}