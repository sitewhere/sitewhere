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
package com.sitewhere.sources;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import com.sitewhere.spi.SiteWhereException;
import com.sitewhere.spi.microservice.lifecycle.ILifecycleProgressMonitor;

/**
 * Abstract base class for event receivers that poll an external source at a
 * given interval.
 *
 * @param <T>
 */
public abstract class PollingInboundEventReceiver<T> extends InboundEventReceiver<T> {

    /** Default polling interval in milliseconds */
    private static final int DEFAULT_POLL_INTERVAL_MS = 10000;

    /** Polling interval in milliseconds */
    private int pollIntervalMs = DEFAULT_POLL_INTERVAL_MS;

    /** Handles poller threading */
    private ExecutorService executor;

    /**
     * Implemented in subclass to do work when polling occurs.
     * 
     * @throws SiteWhereException
     */
    public abstract void doPoll() throws SiteWhereException;

    /*
     * (non-Javadoc)
     * 
     * @see
     * com.sitewhere.server.lifecycle.LifecycleComponent#start(com.sitewhere.spi
     * .server.lifecycle.ILifecycleProgressMonitor)
     */
    @Override
    public void start(ILifecycleProgressMonitor monitor) throws SiteWhereException {
	this.executor = Executors.newSingleThreadExecutor();
	executor.submit(new Poller());
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * com.sitewhere.server.lifecycle.LifecycleComponent#stop(com.sitewhere.spi.
     * server.lifecycle.ILifecycleProgressMonitor)
     */
    @Override
    public void stop(ILifecycleProgressMonitor monitor) throws SiteWhereException {
	if (executor != null) {
	    executor.shutdownNow();
	}
    }

    /**
     * Class that excutes polling code at a given interval.
     */
    public class Poller implements Runnable {

	@Override
	public void run() {
	    while (true) {
		try {
		    doPoll();
		} catch (SiteWhereException e) {
		    getLogger().error("Error executing polling logic.", e);
		} catch (Exception e) {
		    getLogger().error("Unhandled exception in polling operation.", e);
		}
		try {
		    Thread.sleep(getPollIntervalMs());
		} catch (InterruptedException e) {
		    getLogger().warn("Poller thread interrupted.");
		    return;
		}
	    }
	}
    }

    public int getPollIntervalMs() {
	return pollIntervalMs;
    }

    public void setPollIntervalMs(int pollIntervalMs) {
	this.pollIntervalMs = pollIntervalMs;
    }
}