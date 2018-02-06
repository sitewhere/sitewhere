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

public class EventData implements Comparable<EventData> {
    private final Message message;
    private final MessageId messageId;
    private long enqueueTime;

    public EventData(Message message, MessageId messageId) {
	this.message = message;
	this.messageId = messageId;
    }

    public EventData(Message message, MessageId messageId, long enqueuTime) {
	this.message = message;
	this.messageId = messageId;
	this.enqueueTime = enqueuTime;
    }

    public static EventData create(Message message, MessageId messageId) {
	return new EventData(message, messageId);
    }

    public static EventData create(Message message, MessageId messageId, long enqueueTime) {
	return new EventData(message, messageId, enqueueTime);
    }

    public Message getMessage() {
	return this.message;
    }

    public MessageId getMessageId() {
	return this.messageId;
    }

    public long getEnqueueTime() {
	return this.enqueueTime;
    }

    @Override
    public int compareTo(EventData ed) {
	return messageId.getSequenceNumber().compareTo(ed.getMessageId().getSequenceNumber());
    }
}
