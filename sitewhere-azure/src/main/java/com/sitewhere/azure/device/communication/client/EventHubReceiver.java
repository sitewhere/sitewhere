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

import org.apache.qpid.amqp_1_0.client.*;
import org.apache.qpid.amqp_1_0.type.Symbol;
import org.apache.qpid.amqp_1_0.type.UnsignedInteger;
import org.apache.qpid.amqp_1_0.type.messaging.Filter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Collections;
import java.util.Map;

public final class EventHubReceiver {

    private static final Logger logger = LoggerFactory.getLogger(EventHubReceiver.class);
    private static final String linkName = "eventhubs-receiver-link";

    private final Session session;
    private final String entityPath;
    private final String consumerGroupName;
    private final String partitionId;
    private final String consumerAddress;
    private final Map<Symbol, Filter> filters;
    private final int defaultCredits;

    private Receiver receiver;
    private boolean isClosed;

    public EventHubReceiver(Session session, String entityPath, String consumerGroupName, String partitionId,
	    String filterStr, int defaultCredits) throws EventHubException {

	this.session = session;
	this.entityPath = entityPath;
	this.consumerGroupName = consumerGroupName;
	this.partitionId = partitionId;
	this.consumerAddress = this.getConsumerAddress();
	this.filters = Collections.singletonMap(Symbol.valueOf(Constants.SelectorFilterName),
		(Filter) new SelectorFilter(filterStr));
	logger.info("receiver filter string: " + filterStr);
	this.defaultCredits = defaultCredits;

	this.ensureReceiverCreated();
    }

    // receive without timeout means wait until a message is delivered.
    public Message receive() {
	return this.receive(-1L);
    }

    public Message receive(long waitTimeInMilliseconds) {

	this.checkIfClosed();

	Message message = this.receiver.receive(waitTimeInMilliseconds);

	if (message != null) {
	    // Let's acknowledge a message although EH service doesn't need it
	    // to avoid AMQP flow issue.
	    receiver.acknowledge(message);

	    return message;
	} else {
	    this.checkError();
	}

	return null;
    }

    public void close() {
	if (!isClosed) {
	    receiver.close();
	    isClosed = true;
	}
    }

    private String getConsumerAddress() {
	return String.format(Constants.ConsumerAddressFormatString, entityPath, consumerGroupName, partitionId);
    }

    private void ensureReceiverCreated() throws EventHubException {
	try {
	    logger.info("defaultCredits: " + defaultCredits);
	    receiver = session.createReceiver(consumerAddress, AcknowledgeMode.ALO, linkName, false, filters, null);
	    receiver.setCredit(UnsignedInteger.valueOf(defaultCredits), true);
	} catch (ConnectionErrorException e) {
	    // caller (EventHubSpout) will log the error
	    throw new EventHubException(e);
	}
    }

    private void checkError() {
	org.apache.qpid.amqp_1_0.type.transport.Error error = this.receiver.getError();
	if (error != null) {
	    String errorMessage = error.toString();
	    logger.error(errorMessage);
	    this.close();

	    throw new RuntimeException(errorMessage);
	} else {
	    // adding a sleep here to avoid any potential tight-loop issue.
	    try {
		Thread.sleep(10);
	    } catch (InterruptedException e) {
		logger.error(e.toString());
	    }
	}
    }

    private void checkIfClosed() {
	if (this.isClosed) {
	    throw new RuntimeException("receiver was closed.");
	}
    }
}
