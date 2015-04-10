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

public class EventHubSendClient {

    public static void main(String[] args) throws Exception {

        if (args == null || args.length < 7) {
            throw new IllegalArgumentException(
                    "arguments are missing. [username] [password] [namespace] [entityPath] [partitionId] [messageSize] [messageCount] are required.");
        }

        String username = args[0];
        String password = args[1];
        String namespace = args[2];
        String entityPath = args[3];
        String partitionId = args[4];
        int messageSize = Integer.parseInt(args[5]);
        int messageCount = Integer.parseInt(args[6]);
        assert (messageSize > 0);
        assert (messageCount > 0);

        if (partitionId.equals("-1")) {
            // -1 means we want to send data to partitions in round-robin fashion.
            partitionId = null;
        }

        try {
            String connectionString = EventHubReceiverTaskConfig.buildConnectionString(username, password, namespace);
            EventHubClient client = EventHubClient.create(connectionString, entityPath);
            EventHubSender sender = client.createPartitionSender(partitionId);

            StringBuilder sb = new StringBuilder(messageSize);
            for (int i = 1; i < messageCount + 1; ++i) {
                while (sb.length() < messageSize) {
                    sb.append(" current message: " + i);
                }
                sb.setLength(messageSize);
                sender.send(sb.toString());
                sb.setLength(0);
                if (i % 1000 == 0) {
                    System.out.println("Number of messages sent: " + i);
                }
            }
            System.out.println("Total Number of messages sent: " + messageCount);
        } catch (Exception e) {
            System.out.println("Exception: " + e.getMessage());
        }

        System.out.println("done");
    }
}
