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

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.TreeSet;

public class PartitionManager extends SimplePartitionManager {
    private static final Logger logger = LoggerFactory.getLogger(PartitionManager.class);
    private final int ehReceiveTimeoutMs = 5000;
    //private final int ehReceiveTimeoutMs = -1;

    //all sent events are stored in pending
    private final Map<String, EventData> pending;
    //all failed events are put in toResend, which is sorted by event's offset
    private final TreeSet<EventData> toResend;

    public PartitionManager(
            EventHubReceiverTaskConfig config,
            String partitionId,
            IStateStore stateStore,
            IEventHubReceiver receiver) {

        super(config, partitionId, stateStore, receiver);

        this.pending = new LinkedHashMap<String, EventData>();
        this.toResend = new TreeSet<EventData>();
    }

    @Override
    public EventData receive() {
        if (pending.size() >= config.getMaxPendingMsgsPerPartition()) {
            return null;
        }

        EventData eventData;
        if (toResend.isEmpty()) {
            eventData = receiver.receive(ehReceiveTimeoutMs);
        } else {
            eventData = toResend.pollFirst();
        }

        if (eventData != null) {
            lastOffset = eventData.getMessageId().getOffset();
            pending.put(lastOffset, eventData);
        }

        return eventData;
    }

    @Override
    public void ack(String offset) {
        pending.remove(offset);
    }

    @Override
    public void fail(String offset) {
        logger.warn("fail on " + offset);
        EventData eventData = pending.remove(offset);
        toResend.add(eventData);
    }

    @Override
    protected String getCompletedOffset() {
        String offset = null;

        if (pending.size() > 0) {
            //find the smallest offset in pending list
            offset = pending.keySet().iterator().next();
        }
        if (toResend.size() > 0) {
            //find the smallest offset in toResend list
            String offset2 = toResend.first().getMessageId().getOffset();
            if (offset == null || offset2.compareTo(offset) < 0) {
                offset = offset2;
            }
        }
        if (offset == null) {
            offset = lastOffset;
        }
        return offset;
    }
}
