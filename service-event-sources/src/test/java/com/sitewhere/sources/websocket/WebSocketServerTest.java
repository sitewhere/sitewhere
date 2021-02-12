/**
 * Copyright © 2014-2021 The SiteWhere Authors
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
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