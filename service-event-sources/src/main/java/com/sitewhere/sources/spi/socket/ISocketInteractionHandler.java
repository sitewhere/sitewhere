/*
 * Copyright (c) SiteWhere, LLC. All rights reserved. http://www.sitewhere.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package com.sitewhere.sources.spi.socket;

import java.net.Socket;

import com.sitewhere.sources.spi.IInboundEventReceiver;
import com.sitewhere.spi.SiteWhereException;

/**
 * Interface for handling socket communication with a remote device.
 * 
 * @author Derek
 * 
 * @param <T>
 */
public interface ISocketInteractionHandler<T> {

    /**
     * Delegates processing of socket information. Commands parsed from the
     * socket should be passed to {@link IInboundEventReceiver}
     * onEventPayloadReceived() method.
     * 
     * @param socket
     * @param receiver
     * @throws SiteWhereException
     */
    public void process(Socket socket, IInboundEventReceiver<T> receiver) throws SiteWhereException;
}