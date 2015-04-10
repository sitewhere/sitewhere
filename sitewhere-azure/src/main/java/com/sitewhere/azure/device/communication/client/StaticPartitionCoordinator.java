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
package com.sitewhere.azure.device.communication.client;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class StaticPartitionCoordinator implements IPartitionCoordinator {

    private static final Logger logger = LoggerFactory.getLogger(StaticPartitionCoordinator.class);

    protected final EventHubReceiverTaskConfig config;
    protected final int taskIndex;
    protected final int totalTasks;
    protected final List<IPartitionManager> partitionManagers;
    protected final Map<String, IPartitionManager> partitionManagerMap;
    protected final IStateStore stateStore;

    public StaticPartitionCoordinator(
            EventHubReceiverTaskConfig eventHubReceiverConfig,
            int taskIndex,
            int totalTasks,
            IStateStore stateStore,
            IPartitionManagerFactory pmFactory,
            IEventHubReceiverFactory recvFactory) {

        this.config = eventHubReceiverConfig;
        this.taskIndex = taskIndex;
        this.totalTasks = totalTasks;
        this.stateStore = stateStore;
        List<String> partitionIds = calculateParititionIdsToOwn();
        partitionManagerMap = new HashMap<String, IPartitionManager>();
        partitionManagers = new ArrayList<IPartitionManager>();

        for (String partitionId : partitionIds) {
            IEventHubReceiver receiver = recvFactory.create(config, partitionId);
            IPartitionManager partitionManager = pmFactory.create(
                    config, partitionId, stateStore, receiver);
            partitionManagerMap.put(partitionId, partitionManager);
            partitionManagers.add(partitionManager);
        }
    }

    @Override
    public List<IPartitionManager> getMyPartitionManagers() {
        return partitionManagers;
    }

    @Override
    public IPartitionManager getPartitionManager(String partitionId) {
        return partitionManagerMap.get(partitionId);
    }

    protected List<String> calculateParititionIdsToOwn() {
        List<String> taskPartitions = new ArrayList<String>();
        for (int i = this.taskIndex; i < config.getPartitionCount(); i += this.totalTasks) {
            taskPartitions.add(Integer.toString(i));
            logger.info(String.format("taskIndex %d owns partitionId %d.", this.taskIndex, i));
        }

        return taskPartitions;
    }
}
