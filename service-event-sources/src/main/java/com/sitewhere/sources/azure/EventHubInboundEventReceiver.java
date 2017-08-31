/*******************************************************************************
 Copyright (c) Microsoft Open Technologies (Shanghai) Company Limited.  All rights reserved.

 The MIT License (MIT)

 Permission is hereby granted, free of charge, to any person obtaining a copy
 of this software and associated documentation files (the "Software"), to deal
 in the Software without restriction, including without limitation the rights
 to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 copies of the Software, and to permit persons to whom the Software is
 furnished to do so, subject to the following conditions:

 The above copyright notice and this permission notice shall be included in
 all copies or substantial portions of the Software.

 THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 THE SOFTWARE.

 *******************************************************************************/
package com.sitewhere.sources.azure;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.atomic.AtomicInteger;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.qpid.amqp_1_0.client.Message;

import com.sitewhere.device.communication.EventProcessingLogic;
import com.sitewhere.sources.InboundEventReceiver;
import com.sitewhere.sources.azure.client.Constants;
import com.sitewhere.sources.azure.client.EventData;
import com.sitewhere.sources.azure.client.EventHubReceiverTask;
import com.sitewhere.sources.azure.client.EventHubReceiverTaskConfig;
import com.sitewhere.spi.SiteWhereException;
import com.sitewhere.spi.server.lifecycle.ILifecycleProgressMonitor;

public class EventHubInboundEventReceiver extends InboundEventReceiver<byte[]> {

    private static final Logger logger = LogManager.getLogger();
    private static String username = "";
    private static String password = "";
    private static String namespace = "";
    private static String entityPath = "";
    private static int partitionCount = 0;
    private static String zkStateStore = "";
    private static String targetFqn = "";
    private ArrayList<EventHubReceiverTask> taskPool = new ArrayList<EventHubReceiverTask>();

    private static EventHubReceiverTaskConfig config;

    private ExecutorService executor = Executors.newCachedThreadPool(new ReceiverThreadFactory());

    /**
     * Used for naming consumer threads
     */
    private class ReceiverThreadFactory implements ThreadFactory {

	/**
	 * Counts threads
	 */
	private AtomicInteger counter = new AtomicInteger();

	public Thread newThread(Runnable r) {
	    return new Thread(r,
		    "SiteWhere EventHub(" + namespace + " - " + entityPath + ") Receiver " + counter.incrementAndGet());
	}
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * com.sitewhere.server.lifecycle.LifecycleComponent#start(com.sitewhere.spi
     * .server.lifecycle.ILifecycleProgressMonitor)
     */
    @Override
    public void start(ILifecycleProgressMonitor monitor) throws SiteWhereException {
	config = new EventHubReceiverTaskConfig(username, password, namespace, entityPath, partitionCount,
		zkStateStore);
	config.setTargetAddress(targetFqn);

	for (int i = 0; i < partitionCount; i++) {
	    executor.execute(new EventProcessor(partitionCount, i));
	}
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
	for (EventHubReceiverTask task : taskPool) {
	    task.deactivate();
	}
	executor.shutdownNow();
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.sitewhere.device.communication.InboundEventReceiver#getLogger()
     */
    @Override
    public Logger getLogger() {
	return logger;
    }

    private class EventProcessor implements Runnable {

	int taskIndex;
	int totalTasks;

	public EventProcessor(int totalTasks, int taskIndex) {
	    this.totalTasks = totalTasks;
	    this.taskIndex = taskIndex;
	}

	@Override
	public void run() {
	    EventHubReceiverTask task = new EventHubReceiverTask(config);
	    taskPool.add(task);

	    Map<String, Integer> context = new HashMap<String, Integer>();
	    context.put(Constants.TotalTaskKey, this.totalTasks);
	    context.put(Constants.TaskIndexKey, this.taskIndex);
	    boolean SWITCH = true;
	    int EH_RETRY_SLEEP_SECONDS = 10;

	    while (SWITCH) {

		try {
		    task.open(context);
		} catch (Exception e) {
		    logger.warn(
			    "Task " + taskIndex + " failed to open, retry in " + EH_RETRY_SLEEP_SECONDS + "seconds.",
			    e);
		    try {
			task.close();
			Thread.sleep(EH_RETRY_SLEEP_SECONDS * 1000);
		    } catch (Exception ignored) {
		    }
		    continue;
		}

		while (SWITCH) {
		    try {
			EventData data = task.receive();
			if (data == null) {
			    continue;
			}
			Message p = data.getMessage();

			if (p == null || p.getApplicationProperties() == null
				|| p.getApplicationProperties().getValue() == null
				|| p.getApplicationProperties().getValue().get(Constants.AmqpPayloadKey) == null) {
			    logger.warn("Skipped message without a valid payload received.");
			    continue;
			}
			byte[] payload = p.getApplicationProperties().getValue().get(Constants.AmqpPayloadKey)
				.toString().getBytes();
			// Map eventContext = new HashMap();
			// eventContext.put("enqueueTime",
			// data.getEnqueueTime());
			EventProcessingLogic.processRawPayload(EventHubInboundEventReceiver.this, payload, null);

		    } catch (Exception e) {
			e.printStackTrace();
			logger.warn(
				"Task " + taskIndex + " exception, restart in " + EH_RETRY_SLEEP_SECONDS + " seconds",
				e);
			try {
			    task.close();
			} catch (Exception ignored) {
			}
			break;
		    } catch (Throwable t) {
			logger.error("Error in task " + taskIndex + ":" + t.getMessage());
			try {
			    task.close();
			} catch (Throwable t1) {
			    logger.error(t1.getMessage());
			}
			SWITCH = false;
		    }
		}

		try {
		    Thread.sleep(EH_RETRY_SLEEP_SECONDS * 1000);
		} catch (InterruptedException ie) {
		    break;
		}
	    }
	}
    }

    public static void setUsername(String username) {
	EventHubInboundEventReceiver.username = username;
    }

    public static void setPassword(String password) {
	EventHubInboundEventReceiver.password = password;
    }

    public static void setNamespace(String namespace) {
	EventHubInboundEventReceiver.namespace = namespace;
    }

    public static void setEntityPath(String entityPath) {
	EventHubInboundEventReceiver.entityPath = entityPath;
    }

    public static void setPartitionCount(int partitionCount) {
	EventHubInboundEventReceiver.partitionCount = partitionCount;
    }

    public static void setZkStateStore(String zkStateStore) {
	EventHubInboundEventReceiver.zkStateStore = zkStateStore;
    }

    public static void setTargetFqn(String targetFqn) {
	EventHubInboundEventReceiver.targetFqn = targetFqn;
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.sitewhere.spi.device.communication.IInboundEventReceiver#
     * getDisplayName()
     */
    @Override
    public String getDisplayName() {
	return targetFqn + ":" + namespace + "/" + entityPath;
    }
}
