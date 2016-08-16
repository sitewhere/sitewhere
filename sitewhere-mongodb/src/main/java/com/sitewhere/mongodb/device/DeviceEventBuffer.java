/*
 * Copyright (c) SiteWhere, LLC. All rights reserved. http://www.sitewhere.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package com.sitewhere.mongodb.device;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.mongodb.BulkWriteOperation;
import com.mongodb.DBCollection;
import com.mongodb.DBObject;
import com.mongodb.MongoCommandException;
import com.mongodb.MongoTimeoutException;
import com.sitewhere.spi.SiteWhereException;

/**
 * Buffers {@link DBObject} for bulk inserts.
 * 
 * @author Derek
 */
public class DeviceEventBuffer implements IDeviceEventBuffer {

	/** Static logger instance */
	private static Logger LOGGER = LogManager.getLogger();

	/** Max number of puts that can be stored in the queue */
	private static final int MAX_QUEUE_SIZE = 10000;

	/** Max number of milliseconds cache before sending */
	private static final int MAX_TIME_BEFORE_WRITE = 250;

	/** Buffer of DBObjects to insert */
	private BlockingQueue<DBObject> buffer = new ArrayBlockingQueue<DBObject>(MAX_QUEUE_SIZE);

	/** Used to create the buffer sending thread */
	private ExecutorService executor;

	/** Events collection */
	private DBCollection events;

	/** Max inserts per chunk */
	private int maxChunkSize;

	public DeviceEventBuffer(DBCollection events, int maxChunkSize) {
		this.events = events;
		this.maxChunkSize = maxChunkSize;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.sitewhere.hbase.device.IDeviceEventBuffer#start()
	 */
	public void start() throws SiteWhereException {
		executor = Executors.newSingleThreadExecutor();
		executor.execute(new EventSender());
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.sitewhere.hbase.device.IDeviceEventBuffer#stop()
	 */
	public void stop() throws SiteWhereException {
		executor.shutdownNow();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.sitewhere.mongodb.device.IDeviceEventBuffer#add(com.mongodb.DBObject)
	 */
	public void add(DBObject record) {
		try {
			buffer.put(record);
		} catch (InterruptedException e) {
			LOGGER.error("Interrupted while trying to put data.", e);
		}
	}

	/**
	 * Thread that sends {@link DBObject} inserts to MongoDB in batches.
	 * 
	 * @author Derek
	 */
	private class EventSender implements Runnable {

		@Override
		public void run() {
			long lastPut = System.currentTimeMillis();

			while (true) {
				BulkWriteOperation op = events.initializeUnorderedBulkOperation();
				int count = 0;

				while (true) {
					try {
						DBObject record = buffer.poll(MAX_TIME_BEFORE_WRITE, TimeUnit.MILLISECONDS);
						if (record != null) {
							op.insert(record);
							count++;
						}
					} catch (InterruptedException e) {
						return;
					}

					if ((count >= maxChunkSize) || ((System.currentTimeMillis() - lastPut) > MAX_TIME_BEFORE_WRITE)) {
						if (count > 0) {
							try {
								LOGGER.debug("Executing bulk insert of " + count + " event records.");
								op.execute();
							} catch (MongoCommandException e) {
								LOGGER.error("Error during MongoDB insert.", e);
							} catch (MongoTimeoutException e) {
								LOGGER.error("Connection to MongoDB lost.", e);
							}
						}

						lastPut = System.currentTimeMillis();
						break;
					}
				}
			}
		}
	}
}