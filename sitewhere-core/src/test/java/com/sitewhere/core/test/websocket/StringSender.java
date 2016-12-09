/*
 * Copyright (c) SiteWhere, LLC. All rights reserved. http://www.sitewhere.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package com.sitewhere.core.test.websocket;

import java.io.IOException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import javax.websocket.CloseReason;
import javax.websocket.Endpoint;
import javax.websocket.EndpointConfig;
import javax.websocket.Session;

/**
 * Endpoint that sends a String every second.
 * 
 * @author Derek
 */
public class StringSender extends Endpoint {

    /** Used for sending in background thread */
    private ExecutorService executor = Executors.newSingleThreadExecutor();

    @Override
    public void onClose(Session session, CloseReason closeReason) {
	executor.shutdownNow();
    }

    /*
     * (non-Javadoc)
     * 
     * @see javax.websocket.Endpoint#onOpen(javax.websocket.Session,
     * javax.websocket.EndpointConfig)
     */
    @Override
    public void onOpen(final Session session, EndpointConfig config) {
	executor.execute(new Runnable() {

	    @Override
	    public void run() {
		while (true) {
		    try {
			session.getBasicRemote().sendText("LOC,f5fac881-1b9b-4aed-8e65-4476574db5ab,33.7550,-84.3900");
			Thread.sleep(1000);
		    } catch (IOException e) {
			e.printStackTrace();
		    } catch (InterruptedException e) {
		    }
		}
	    }
	});
    }
}