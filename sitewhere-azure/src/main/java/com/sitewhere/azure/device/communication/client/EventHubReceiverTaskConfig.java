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


import java.io.Serializable;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;

public class EventHubReceiverTaskConfig implements Serializable {

    private static final long serialVersionUID = 1L;
    private final String userName;
    private final String password;
    private final String namespace;
    private final String entityPath;
    private final int partitionCount;

    public String getZkConnectionString() {
        return zkConnectionString;
    }

    private final String zkConnectionString;
    private final int checkpointIntervalInSeconds;
    private final int receiverCredits;
    private final int maxPendingMsgsPerPartition;
    private final long enqueueTimeFilter; //timestamp in millisecond

    private String connectionString;
    private String targetFqnAddress;
    //private IEventDataScheme scheme;

    public EventHubReceiverTaskConfig(String username, String password, String namespace,
                                      String entityPath, int partitionCount, String storeConnectionString) {
        this(username, password, namespace, entityPath, partitionCount,
                storeConnectionString, 300, 1024, 1024, 0);
    }

    //Keep this constructor for backward compatibility
    public EventHubReceiverTaskConfig(String username, String password, String namespace,
                                      String entityPath, int partitionCount, String storeConnectionString,
                                      int checkpointIntervalInSeconds, int receiverCredits) {
        this(username, password, namespace, entityPath, partitionCount,
                storeConnectionString, checkpointIntervalInSeconds, receiverCredits, 1024, 0);
    }

    public EventHubReceiverTaskConfig(String username, String password, String namespace,
                                      String entityPath, int partitionCount, String storeConnectionString,
                                      int checkpointIntervalInSeconds, int receiverCredits, int maxPendingMsgsPerPartition, long enqueueTimeFilter) {
        this.userName = username;
        this.password = password;
        this.connectionString = buildConnectionString(username, password, namespace);
        this.namespace = namespace;
        this.entityPath = entityPath;
        this.partitionCount = partitionCount;
        this.zkConnectionString = storeConnectionString;
        this.checkpointIntervalInSeconds = checkpointIntervalInSeconds;
        this.receiverCredits = receiverCredits;
        this.maxPendingMsgsPerPartition = maxPendingMsgsPerPartition;
        this.enqueueTimeFilter = enqueueTimeFilter;
        //this.scheme = new EventDataScheme();
    }

    public String getConnectionString() {
        return connectionString;
    }

    public String getNamespace() {
        return namespace;
    }

    public String getEntityPath() {
        return entityPath;
    }

    public int getCheckpointIntervalInSeconds() {
        return checkpointIntervalInSeconds;
    }

    public int getPartitionCount() {
        return partitionCount;
    }

    public int getReceiverCredits() {
        return receiverCredits;
    }

    public int getMaxPendingMsgsPerPartition() {
        return maxPendingMsgsPerPartition;
    }

    public long getEnqueueTimeFilter() {
        return enqueueTimeFilter;
    }

    //  public IEventDataScheme getEventDataScheme() {
//    return scheme;
//  }

//  public void setEventDataScheme(IEventDataScheme scheme) {
//    this.scheme = scheme;
//  }

    public List<String> getPartitionList() {
        List<String> partitionList = new ArrayList<String>();

        for (int i = 0; i < this.partitionCount; i++) {
            partitionList.add(Integer.toString(i));
        }

        return partitionList;
    }

    public void setTargetAddress(String targetFqnAddress) {
        this.targetFqnAddress = targetFqnAddress;
        this.connectionString = buildConnectionString(
                this.userName, this.password, this.namespace, this.targetFqnAddress);
    }

    public static String buildConnectionString(String username, String password, String namespace) {
        //String targetFqnAddress = "servicebus.windows.net";
        String targetFqnAddress = "servicebus.chinacloudapi.cn";
        return buildConnectionString(username, password, namespace, targetFqnAddress);
    }

    public static String buildConnectionString(String username, String password,
                                               String namespace, String targetFqnAddress) {
        return "amqps://" + username + ":" + encodeString(password)
                + "@" + namespace + "." + targetFqnAddress;
    }

    private static String encodeString(String input) {
        try {
            return URLEncoder.encode(input, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            //We don't need to throw this exception because the exception won't
            //happen because of user input. Our unit tests will catch this error.
            return "";
        }
    }

}
