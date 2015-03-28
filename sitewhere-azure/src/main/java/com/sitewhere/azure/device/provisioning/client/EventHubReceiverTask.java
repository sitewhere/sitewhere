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
package com.sitewhere.azure.device.provisioning.client;

import org.apache.log4j.Logger;

import java.util.List;
import java.util.Map;
import java.util.UUID;

public class EventHubReceiverTask {

	private static Logger logger = Logger.getLogger(EventHubReceiverTask.class);

	@SuppressWarnings("unused")
	private final UUID instanceId;
	private final EventHubReceiverTaskConfig eventHubConfig;
	private final int checkpointIntervalInSeconds;

	private IStateStore stateStore;
	private IPartitionCoordinator partitionCoordinator;
	private IPartitionManagerFactory pmFactory;
	private IEventHubReceiverFactory recvFactory;
	private long lastCheckpointTime;
	private int currentPartitionIndex = -1;

	public EventHubReceiverTask(EventHubReceiverTaskConfig config) {
		this(config, null, null, null);
	}

	@SuppressWarnings("serial")
	public EventHubReceiverTask(EventHubReceiverTaskConfig config, IStateStore store,
			IPartitionManagerFactory pmFactory, IEventHubReceiverFactory recvFactory) {
		this.eventHubConfig = config;
		this.instanceId = UUID.randomUUID();
		this.checkpointIntervalInSeconds = config.getCheckpointIntervalInSeconds();
		this.lastCheckpointTime = System.currentTimeMillis();
		stateStore = store;
		this.pmFactory = pmFactory;
		if (this.pmFactory == null) {
			this.pmFactory = new IPartitionManagerFactory() {
				@Override
				public IPartitionManager create(EventHubReceiverTaskConfig config, String partitionId,
						IStateStore stateStore, IEventHubReceiver receiver) {
					return new PartitionManager(config, partitionId, stateStore, receiver);
				}
			};
		}
		this.recvFactory = recvFactory;
		if (this.recvFactory == null) {
			this.recvFactory = new IEventHubReceiverFactory() {
				@Override
				public IEventHubReceiver create(EventHubReceiverTaskConfig config, String partitionId) {
					return new EventHubReceiverImpl(config, partitionId);
				}
			};
		}

	}

	/**
	 * This is a extracted method that is easy to test
	 *
	 * @throws Exception
	 */
	@SuppressWarnings("rawtypes")
	private void preparePartitions(Map config, int totalTasks, int taskIndex) throws Exception {
		if (stateStore == null) {
			String zkEndpointAddress = eventHubConfig.getZkConnectionString();
			stateStore = new ZookeeperStateStore(zkEndpointAddress);
		}

		stateStore.open();

		partitionCoordinator =
				new StaticPartitionCoordinator(eventHubConfig, taskIndex, totalTasks, stateStore, pmFactory,
						recvFactory);

		for (IPartitionManager partitionManager : partitionCoordinator.getMyPartitionManagers()) {
			partitionManager.open();
		}
	}

	@SuppressWarnings("rawtypes")
	public void open(Map context) {
		logger.info("begin: open()");
		int totalTasks;
		totalTasks = (Integer) context.get(Constants.TotalTaskKey);
		int taskIndex = (Integer) context.get(Constants.TaskIndexKey);
		try {
			preparePartitions(context, totalTasks, taskIndex);
		} catch (Exception e) {
			logger.error(e.getMessage());
			throw new RuntimeException(e);
		}

		logger.info("end open()");
	}

	public EventData receive() {
		EventData eventData = null;

		List<IPartitionManager> partitionManagers = partitionCoordinator.getMyPartitionManagers();
		for (int i = 0; i < partitionManagers.size(); i++) {
			currentPartitionIndex = (currentPartitionIndex + 1) % partitionManagers.size();
			IPartitionManager partitionManager = partitionManagers.get(currentPartitionIndex);

			if (partitionManager == null) {
				throw new RuntimeException("partitionManager doesn't exist.");
			}

			eventData = partitionManager.receive();

			if (eventData != null) {
				break;
			}
		}

		if (eventData != null) {
			MessageId messageId = eventData.getMessageId();
			logger.info(messageId.toString());
			ack(eventData.getMessageId());
		}

		checkpointIfNeeded();
		return eventData;

		// We don't need to sleep here because the IPartitionManager.receive() is
		// a blocked call so it's fine to call this function in a tight loop.
	}

	public void ack(Object msgId) {
		MessageId messageId = (MessageId) msgId;
		IPartitionManager partitionManager =
				partitionCoordinator.getPartitionManager(messageId.getPartitionId());
		String offset = messageId.getOffset();
		partitionManager.ack(offset);
	}

	public void fail(Object msgId) {
		MessageId messageId = (MessageId) msgId;
		IPartitionManager partitionManager =
				partitionCoordinator.getPartitionManager(messageId.getPartitionId());
		String offset = messageId.getOffset();
		partitionManager.fail(offset);
	}

	public void deactivate() {
		// let's checkpoint so that we can get the last checkpoint when restarting.
		checkpoint();
	}

	public void close() {
		for (IPartitionManager partitionManager : partitionCoordinator.getMyPartitionManagers()) {
			partitionManager.close();
		}
		stateStore.close();
		stateStore = null;
	}

	private void checkpointIfNeeded() {
		long nextCheckpointTime = lastCheckpointTime + checkpointIntervalInSeconds * 1000;
		if (nextCheckpointTime < System.currentTimeMillis()) {

			checkpoint();
			lastCheckpointTime = System.currentTimeMillis();
		}
	}

	private void checkpoint() {
		for (IPartitionManager partitionManager : partitionCoordinator.getMyPartitionManagers()) {
			partitionManager.checkpoint();
		}
	}
}
