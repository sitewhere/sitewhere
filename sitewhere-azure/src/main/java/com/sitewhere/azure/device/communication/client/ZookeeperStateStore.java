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

import org.apache.curator.RetryPolicy;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.retry.RetryNTimes;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ZookeeperStateStore implements IStateStore {
    private static final long serialVersionUID = 1L;
    private static final Logger logger = LoggerFactory.getLogger(ZookeeperStateStore.class);

    private final String zookeeperConnectionString;
    private final CuratorFramework curatorFramework;

    public ZookeeperStateStore(String zookeeperConnectionString) {
        this(zookeeperConnectionString, 3, 100);
    }

    public ZookeeperStateStore(String connectionString, int retries, int retryInterval) {
        if (connectionString == null) {
            zookeeperConnectionString = "localhost:2181";
        } else {
            zookeeperConnectionString = connectionString;
        }

        RetryPolicy retryPolicy = new RetryNTimes(retries, retryInterval);
        curatorFramework = CuratorFrameworkFactory.newClient(zookeeperConnectionString, retryPolicy);
    }

    @Override
    public void open() {
        curatorFramework.start();
    }

    @Override
    public void close() {
        curatorFramework.close();
    }

    @Override
    public void saveData(String statePath, String data) {
        data = data == null ? "" : data;
        byte[] bytes = data.getBytes();

        try {
            if (curatorFramework.checkExists().forPath(statePath) == null) {
                curatorFramework.create().creatingParentsIfNeeded().forPath(statePath, bytes);
            } else {
                curatorFramework.setData().forPath(statePath, bytes);
            }

            logger.info(String.format("data was saved. path: %s, data: %s.", statePath, data));
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public String readData(String statePath) {
        try {
            if (curatorFramework.checkExists().forPath(statePath) == null) {
                // do we want to throw an exception if path doesn't exist??
                return null;
            } else {
                byte[] bytes = curatorFramework.getData().forPath(statePath);
                String data = new String(bytes);

                logger.info(String.format("data was retrieved. path: %s, data: %s.", statePath, data));

                return data;
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
