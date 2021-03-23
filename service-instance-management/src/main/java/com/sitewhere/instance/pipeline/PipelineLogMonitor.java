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
package com.sitewhere.instance.pipeline;

import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import com.google.common.collect.EvictingQueue;
import com.sitewhere.instance.microservice.InstanceManagementTenantEngine;
import com.sitewhere.instance.spi.pipeline.IPipelineLogMonitor;
import com.sitewhere.microservice.instance.EventPipelineLog;
import com.sitewhere.microservice.lifecycle.TenantEngineLifecycleComponent;
import com.sitewhere.spi.SiteWhereException;
import com.sitewhere.spi.microservice.instance.IEventPipelineLog;
import com.sitewhere.spi.microservice.lifecycle.ILifecycleProgressMonitor;

import io.lettuce.core.Range;
import io.lettuce.core.Range.Boundary;
import io.lettuce.core.StreamMessage;
import io.lettuce.core.XReadArgs;
import io.lettuce.core.api.sync.RedisCommands;

/**
 * Monitor that listens on a Redis Stream for event pipeline log entries.
 */
public class PipelineLogMonitor extends TenantEngineLifecycleComponent implements IPipelineLogMonitor {

    /** Queue for pipeline log entries */
    private Queue<IEventPipelineLog> pipelineLogQueue;

    /** Executor service */
    private ExecutorService executor;

    /*
     * @see com.sitewhere.microservice.lifecycle.LifecycleComponent#initialize(com.
     * sitewhere.spi.microservice.lifecycle.ILifecycleProgressMonitor)
     */
    @Override
    public void initialize(ILifecycleProgressMonitor monitor) throws SiteWhereException {
	super.initialize(monitor);

	// Create queue with max length equal to history length.
	int history = ((InstanceManagementTenantEngine) getTenantEngine()).getActiveConfiguration()
		.getEventPipelineHistoryLength();
	this.pipelineLogQueue = EvictingQueue.create(history);
    }

    /*
     * @see
     * com.sitewhere.microservice.lifecycle.LifecycleComponent#start(com.sitewhere.
     * spi.microservice.lifecycle.ILifecycleProgressMonitor)
     */
    @Override
    public void start(ILifecycleProgressMonitor monitor) throws SiteWhereException {
	super.start(monitor);
	if (this.executor != null) {
	    this.executor.shutdownNow();
	}
	this.executor = Executors.newSingleThreadExecutor();
	this.executor.execute(new MonitorThread());
    }

    /*
     * @see
     * com.sitewhere.microservice.lifecycle.LifecycleComponent#stop(com.sitewhere.
     * spi.microservice.lifecycle.ILifecycleProgressMonitor)
     */
    @Override
    public void stop(ILifecycleProgressMonitor monitor) throws SiteWhereException {
	if (this.executor != null) {
	    this.executor.shutdownNow();
	}
	super.stop(monitor);
    }

    /*
     * @see
     * com.sitewhere.instance.spi.pipeline.IPipelineLogMonitor#getPipelineLogQueue()
     */
    @Override
    public Queue<IEventPipelineLog> getPipelineLogQueue() {
	return pipelineLogQueue;
    }

    /*
     * @see
     * com.sitewhere.instance.spi.pipeline.IPipelineLogMonitor#clearPipelineLog()
     */
    @Override
    public void clearPipelineLog() {
	String topic = getLogPipelineTopic(getTenantEngine());
	RedisCommands<String, byte[]> commands = getMicroservice().getRedisStreamConnection().sync();
	commands.xtrim(topic, 0);
	getPipelineLogQueue().clear();
    }

    /**
     * Thread that queries the pipeline log topic and pulls entries into a evicting
     * queue.
     */
    private class MonitorThread implements Runnable {

	/*
	 * @see java.lang.Runnable#run()
	 */
	@Override
	@SuppressWarnings("unchecked")
	public void run() {
	    String topic = getLogPipelineTopic(getTenantEngine());
	    RedisCommands<String, byte[]> commands = getMicroservice().getRedisStreamConnection().sync();
	    String latestId = null;
	    while (true) {
		try {
		    List<StreamMessage<String, byte[]>> messages;
		    if (latestId == null) {
			messages = commands.xrange(topic, Range.from(Boundary.unbounded(), Boundary.unbounded()));
		    } else {
			messages = commands.xread(XReadArgs.StreamOffset.from(topic, latestId));
		    }
		    if (messages.size() > 0) {
			for (StreamMessage<String, byte[]> message : messages) {
			    latestId = message.getId();
			    Map<String, byte[]> body = message.getBody();
			    EventPipelineLog log = EventPipelineLog.fromMap(body);
			    getPipelineLogQueue().add(log);
			    if (getLogger().isDebugEnabled()) {
				getLogger().debug(String.format("Received pipeline log message: %s %s %s",
					log.getSource(), log.getDeviceToken(), log.getMessage()));
			    }
			}
		    }
		    Thread.sleep(1000);
		} catch (InterruptedException e) {
		    getLogger().error("Pipeline monitoring thread interrupted. Shutting down.");
		    return;
		} catch (Throwable e) {
		    getLogger().error("Exception in pipeline log monitoring thread.", e);
		    return;
		}
	    }
	}
    }
}
