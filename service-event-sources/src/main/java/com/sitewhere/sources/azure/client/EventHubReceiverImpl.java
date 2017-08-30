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
package com.sitewhere.sources.azure.client;

import org.apache.qpid.amqp_1_0.client.Message;
import org.apache.qpid.amqp_1_0.type.Section;
import org.apache.qpid.amqp_1_0.type.Symbol;
import org.apache.qpid.amqp_1_0.type.messaging.MessageAnnotations;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class EventHubReceiverImpl implements IEventHubReceiver {
    private static final Logger logger = LoggerFactory.getLogger(EventHubReceiverImpl.class);
    private static final Symbol OffsetKey = Symbol.valueOf("x-opt-offset");
    private static final Symbol SequenceNumberKey = Symbol.valueOf("x-opt-sequence-number");
    private static final Symbol EnqueueTimeKey = Symbol.valueOf("x-opt-enqueued-time");

    private final String connectionString;
    private final String entityName;
    private final String partitionId;
    private final int defaultCredits;

    private EventHubReceiver receiver;

    public EventHubReceiverImpl(EventHubReceiverTaskConfig config, String partitionId) {
	this.connectionString = config.getConnectionString();
	this.entityName = config.getEntityPath();
	this.defaultCredits = config.getReceiverCredits();
	this.partitionId = partitionId;
    }

    @Override
    public void open(IEventHubReceiverFilter filter) throws EventHubException {
	logger.info("creating eventhub receiver: partitionId=" + partitionId + ", offset=" + filter.getOffset()
		+ ", enqueueTime=" + filter.getEnqueueTime());
	long start = System.currentTimeMillis();
	EventHubClient eventHubClient = EventHubClient.create(connectionString, entityName);
	if (filter.getOffset() != null) {
	    receiver = eventHubClient.getDefaultConsumerGroup().createReceiver(partitionId, filter.getOffset(),
		    defaultCredits);
	} else if (filter.getEnqueueTime() != 0) {
	    receiver = eventHubClient.getDefaultConsumerGroup().createReceiver(partitionId, filter.getEnqueueTime(),
		    defaultCredits);
	} else {
	    logger.error("Invalid IEventHubReceiverFilter, use default offset as filter");
	    receiver = eventHubClient.getDefaultConsumerGroup().createReceiver(partitionId,
		    Constants.DefaultStartingOffset, defaultCredits);
	}
	long end = System.currentTimeMillis();
	logger.info("created eventhub receiver, time taken(ms): " + (end - start));
    }

    @Override
    public void close() {
	if (receiver != null) {
	    receiver.close();
	    logger.info("closed eventhub receiver: partitionId=" + partitionId);
	    receiver = null;
	}
    }

    @Override
    public boolean isOpen() {
	return (receiver != null);
    }

    @Override
    public EventData receive(long timeoutInMilliseconds) {
	// long start = System.currentTimeMillis();
	Message message = receiver.receive(timeoutInMilliseconds);
	// long end = System.currentTimeMillis();
	// long millis = (end - start);

	if (message == null) {
	    return null;
	}

	// logger.info(String.format("received a message. PartitionId: %s,
	// Offset: %s", partitionId, this.lastOffset));
	MessageId messageId = createMessageId(message);
	long enqueueTime = getEnqueueTime(message);

	return EventData.create(message, messageId, enqueueTime);
    }

    @SuppressWarnings("rawtypes")
    private MessageId createMessageId(Message message) {
	String offset = null;
	long sequenceNumber = 0;

	for (Section section : message.getPayload()) {
	    if (section instanceof MessageAnnotations) {
		MessageAnnotations annotations = (MessageAnnotations) section;
		HashMap annonationMap = (HashMap) annotations.getValue();

		if (annonationMap.containsKey(OffsetKey)) {
		    offset = (String) annonationMap.get(OffsetKey);
		}

		if (annonationMap.containsKey(SequenceNumberKey)) {
		    sequenceNumber = (Long) annonationMap.get(SequenceNumberKey);
		}
	    }
	}

	return MessageId.create(partitionId, offset, sequenceNumber);
    }

    @SuppressWarnings("rawtypes")
    private long getEnqueueTime(Message message) {
	long enqueueTime = 0L;
	for (Section section : message.getPayload()) {
	    if (section instanceof MessageAnnotations) {
		MessageAnnotations annotations = (MessageAnnotations) section;
		HashMap annonationMap = (HashMap) annotations.getValue();

		if (annonationMap.containsKey(EnqueueTimeKey)) {
		    enqueueTime = ((Date) annonationMap.get(EnqueueTimeKey)).getTime();
		}
	    }
	}
	return enqueueTime;

    }

    @Override
    @SuppressWarnings("rawtypes")
    public Map getMetricsData() {
	Map ret = new HashMap();
	return ret;
    }
}
