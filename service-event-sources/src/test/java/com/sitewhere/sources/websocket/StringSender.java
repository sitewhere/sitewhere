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

import java.io.IOException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import javax.websocket.CloseReason;
import javax.websocket.Endpoint;
import javax.websocket.EndpointConfig;
import javax.websocket.Session;

/**
 * Endpoint that sends a String every second.
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