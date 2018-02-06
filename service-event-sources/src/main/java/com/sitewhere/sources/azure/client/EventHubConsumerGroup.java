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

import org.apache.qpid.amqp_1_0.client.Connection;
import org.apache.qpid.amqp_1_0.client.ConnectionException;
import org.apache.qpid.amqp_1_0.client.Session;

public class EventHubConsumerGroup {

    private final Connection connection;
    private final String entityPath;
    private final String consumerGroupName;

    private Session session;

    public EventHubConsumerGroup(Connection connection, String entityPath, String consumerGroupName) {
	this.connection = connection;
	this.entityPath = entityPath;
	this.consumerGroupName = consumerGroupName;
    }

    public EventHubReceiver createReceiver(String partitionId, String startingOffset, int defaultCredits)
	    throws EventHubException {
	this.ensureSessionCreated();

	if (startingOffset == null || startingOffset.equals("")) {
	    startingOffset = Constants.DefaultStartingOffset;
	}

	String filterStr = String.format(Constants.OffsetFilterFormatString, startingOffset);
	return new EventHubReceiver(this.session, this.entityPath, this.consumerGroupName, partitionId, filterStr,
		defaultCredits);
    }

    public EventHubReceiver createReceiver(String partitionId, long timeAfter, int defaultCredits)
	    throws EventHubException {
	this.ensureSessionCreated();

	String filterStr = String.format(Constants.EnqueueTimeFilterFormatString, timeAfter);
	return new EventHubReceiver(this.session, this.entityPath, this.consumerGroupName, partitionId, filterStr,
		defaultCredits);
    }

    public void close() {
	if (this.session != null) {
	    this.session.close();
	}
    }

    synchronized void ensureSessionCreated() throws EventHubException {

	try {
	    if (this.session == null) {
		this.session = this.connection.createSession();
	    }
	} catch (ConnectionException e) {
	    throw new EventHubException(e);
	}
    }
}
