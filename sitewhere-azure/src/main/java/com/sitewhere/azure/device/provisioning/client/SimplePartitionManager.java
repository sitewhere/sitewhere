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

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;

/**
 * A simple partition manager that does not re-send failed messages
 */
public class SimplePartitionManager implements IPartitionManager {
    private static final Logger logger = LoggerFactory.getLogger(SimplePartitionManager.class);
    private static final String statePathPrefix = "/eventhub-state";

    protected final IEventHubReceiver receiver;
    protected String lastOffset = "-1";
    protected String committedOffset = "-1";

    protected final EventHubReceiverTaskConfig config;
    private final String partitionId;
    private final IStateStore stateStore;
    private final String statePath;

    public SimplePartitionManager(
            EventHubReceiverTaskConfig spoutConfig,
            String partitionId,
            IStateStore stateStore,
            IEventHubReceiver receiver) {
        this.receiver = receiver;
        this.config = spoutConfig;
        this.partitionId = partitionId;
        this.statePath = this.getPartitionStatePath();
        this.stateStore = stateStore;
    }

    @Override
    public void open() throws Exception {

        //read from state store, if not found, use startingOffset
        String offset = stateStore.readData(statePath);
        logger.info("read offset from state store: " + offset);
        if (offset == null) {
            offset = Constants.DefaultStartingOffset;
        }

        EventHubReceiverFilter filter = new EventHubReceiverFilter();
        if (offset.equals(Constants.DefaultStartingOffset)
                && config.getEnqueueTimeFilter() != 0) {
            filter.setEnqueueTime(config.getEnqueueTimeFilter());
        } else {
            filter.setOffset(offset);
        }

        receiver.open(filter);
    }

    @Override
    public void close() {
        this.receiver.close();
        this.checkpoint();
    }

    @Override
    public void checkpoint() {
        String completedOffset = getCompletedOffset();
        if (!committedOffset.equals(completedOffset)) {
            logger.info("saving state " + completedOffset);
            stateStore.saveData(statePath, completedOffset);
            committedOffset = completedOffset;
        }
    }

    protected String getCompletedOffset() {
        return lastOffset;
    }

    @Override
    public EventData receive() {
        EventData eventData = receiver.receive(5000);
        if (eventData != null) {
            lastOffset = eventData.getMessageId().getOffset();
        }
        return eventData;
    }

    @Override
    public void ack(String offset) {
        //do nothing
    }

    @Override
    public void fail(String offset) {
        logger.warn("fail on " + offset);
        //do nothing
    }

    private String getPartitionStatePath() {

        // Partition state path =
        // "/{prefix}/{topologyName}/{namespace}/{entityPath}/partitions/{partitionId}/state";
        String namespace = config.getNamespace();
        String entityPath = config.getEntityPath();
        //String topologyName = config.getTopologyName();

        String partitionStatePath = statePathPrefix + "/" + namespace + "/" + entityPath + "/partitions/" + this.partitionId;

        logger.info("partition state path: " + partitionStatePath);

        return partitionStatePath;
    }

    @Override
    public Map getMetricsData() {
        return receiver.getMetricsData();
    }
}
