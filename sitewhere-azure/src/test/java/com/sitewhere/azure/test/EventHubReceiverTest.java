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
package com.sitewhere.azure.test;

import com.sitewhere.azure.device.provisioning.client.Constants;
import com.sitewhere.azure.device.provisioning.client.EventData;
import com.sitewhere.azure.device.provisioning.client.EventHubReceiverTask;
import com.sitewhere.azure.device.provisioning.client.EventHubReceiverTaskConfig;
import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class EventHubReceiverTest {

    private String username = "YourUsername";
    private String password = "YourPassword";
    private String namespace = "YourNamespace";
    private String entityPath = "YourEntityPath";
    private int partitionCount = 0; // Amount of your EventHub partitions
    private String zkStateStore = "127.0.0.1:2181"; // Your state store, default to local ZK server.
    private String targetFqn = "servicebus.windows.net";
    //private String targetFqn = "servicebus.chinacloudapi.cn"; // for China

    private EventHubReceiverTaskConfig config;

    @Before
    public void setup() throws Exception {

        config = new EventHubReceiverTaskConfig(username, password, namespace, entityPath,
                partitionCount, zkStateStore);
        config.setTargetAddress(targetFqn);
    }

    @Test
    public void singleThreadReceiveTest(){

        EventHubReceiverTask task = new EventHubReceiverTask(config);

        Map<String, Integer> context = new HashMap<>();
        context.put(Constants.TotalTaskKey, 1);
        context.put(Constants.TaskIndexKey, 1);
        task.open(context);

        EventData data = task.receive();
        System.out.println("Message received: Partition->"+ data.getMessageId().getPartitionId() +
            " Sequence No.->"+data.getMessageId().getSequenceNumber() + " Offset->" + data.getMessageId().getOffset());

        task.close();
    }

}
