/*
 * Copyright (c) SiteWhere, LLC. All rights reserved. http://www.sitewhere.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package com.sitewhere.hbase.device;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import org.apache.hadoop.hbase.client.HTableInterface;
import org.apache.hadoop.hbase.client.Put;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.sitewhere.hbase.IHBaseContext;
import com.sitewhere.hbase.ISiteWhereHBase;
import com.sitewhere.hbase.common.HBaseUtils;
import com.sitewhere.spi.SiteWhereException;

/**
 * Buffers {@link Put} commands so that they are not auto flushed.
 * 
 * @author Derek
 */
public class DeviceEventBuffer implements IDeviceEventBuffer {

	/** Static logger instance */
	private static Logger LOGGER = LogManager.getLogger();

	/** Max number of puts that can be stored in the queue */
	private static final int MAX_QUEUE_SIZE = 10000;

	/** Max number of puts to cache before sending */
	private static final int MAX_PUTS_BEFORE_WRITE = 500;

	/** Max number of milliseconds cache before sending */
	private static final int MAX_TIME_BEFORE_WRITE = 250;

	/** HBase context */
	private IHBaseContext context;

	/** Buffer of Put commands */
	private BlockingQueue<Put> buffer = new ArrayBlockingQueue<Put>(MAX_QUEUE_SIZE);

	/** Used to create the buffer sending thread */
	private ExecutorService executor;

	/** Events table interface */
	private HTableInterface events;

	public DeviceEventBuffer(IHBaseContext context) {
		this.context = context;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.sitewhere.hbase.device.IDeviceEventBuffer#start()
	 */
	public void start() throws SiteWhereException {
		events = context.getClient().getTableInterface(context.getTenant(), ISiteWhereHBase.EVENTS_TABLE_NAME);
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
		if (events != null) {
			HBaseUtils.closeCleanly(events);
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.sitewhere.hbase.device.IDeviceEventBuffer#add(org.apache.hadoop.hbase
	 * .client .Put)
	 */
	public void add(Put put) {
		try {
			buffer.put(put);
		} catch (InterruptedException e) {
			LOGGER.error("Interrupted while trying to put data.", e);
		}
	}

	/**
	 * Thread that sends {@link Put} commands to HBase in batches.
	 * 
	 * @author Derek
	 */
	private class EventSender implements Runnable {

		/** List of puts that will be sent */
		private List<Put> puts = new ArrayList<Put>();

		@Override
		public void run() {
			long lastPut = System.currentTimeMillis();

			while (true) {
				try {
					Put put = buffer.poll(MAX_TIME_BEFORE_WRITE, TimeUnit.MILLISECONDS);
					if (put != null) {
						puts.add(put);
					}
				} catch (InterruptedException e) {
					return;
				}

				if ((puts.size() >= MAX_PUTS_BEFORE_WRITE)
						|| ((System.currentTimeMillis() - lastPut) > MAX_TIME_BEFORE_WRITE)) {
					if (puts.size() > 0) {
						try {
							events.put(puts);
							events.flushCommits();
							puts.clear();
						} catch (IOException e) {
							LOGGER.error("Unable to save event data.", e);
						}
					}

					lastPut = System.currentTimeMillis();
				}
			}
		}
	}
}