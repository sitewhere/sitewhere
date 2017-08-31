/*
 * Copyright (c) SiteWhere, LLC. All rights reserved. http://www.sitewhere.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package com.sitewhere.sources.websocket;

import java.io.BufferedReader;
import java.io.InputStreamReader;

import org.glassfish.tyrus.server.Server;
import org.junit.Test;

public class WebSocketServerTest {

    /**
     * Starts a WebSocket server that has two endpoints, one that generates
     * String payloads and another that generates binary payloads.
     */
    @Test
    public void startWebSocketServer() {
	Server server = new Server("localhost", 6543, "/sitewhere", null, WebSocketConfiguration.class);

	try {
	    server.start();
	    BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
	    System.out.print("Please press a key to stop the server.");
	    reader.readLine();
	} catch (Exception e) {
	    e.printStackTrace();
	} finally {
	    server.stop();
	}
    }
}