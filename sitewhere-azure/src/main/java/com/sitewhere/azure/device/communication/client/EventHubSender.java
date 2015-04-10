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

import org.apache.qpid.amqp_1_0.client.LinkDetachedException;
import org.apache.qpid.amqp_1_0.client.Message;
import org.apache.qpid.amqp_1_0.client.Sender;
import org.apache.qpid.amqp_1_0.client.Session;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.TimeoutException;

public class EventHubSender {

    private static final Logger logger = LoggerFactory.getLogger(EventHubSender.class);

    private final Session session;
    private final String entityPath;
    private final String partitionId;
    private final String destinationAddress;

    private Sender sender;

    public EventHubSender(Session session, String entityPath, String partitionId) {
        this.session = session;
        this.entityPath = entityPath;
        this.partitionId = partitionId;
        this.destinationAddress = this.getDestinationAddress();
    }

    public void send(String data) throws EventHubException {
        try {
            if (this.sender == null) {
                this.ensureSenderCreated();
            }

            Message message = new Message(data);
            this.sender.send(message);

        } catch (LinkDetachedException e) {
            logger.error(e.getMessage());

            EventHubException eventHubException = new EventHubException("Sender has been closed");
            throw eventHubException;
        } catch (TimeoutException e) {
            logger.error(e.getMessage());

            EventHubException eventHubException = new EventHubException("Timed out while waiting to get credit to send");
            throw eventHubException;
        } catch (Exception e) {
            logger.error(e.getMessage());
        }
    }

    public void close() {
        try {
            this.sender.close();
        } catch (Sender.SenderClosingException e) {
            logger.error("Closing a sender encountered error: " + e.getMessage());
        }
    }

    private String getDestinationAddress() {
        if (this.partitionId == null || this.partitionId.equals("")) {
            return this.entityPath;
        } else {
            return String.format(Constants.DestinationAddressFormatString, this.entityPath, this.partitionId);
        }
    }

    private synchronized void ensureSenderCreated() throws Exception {
        if (this.sender == null) {
            this.sender = this.session.createSender(this.destinationAddress);
        }
    }
}
