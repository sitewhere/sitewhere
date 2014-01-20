/*
 * SolrDeviceManagementFacade.java 
 * --------------------------------------------------------------------------------------
 * Copyright (c) Reveal Technologies, LLC. All rights reserved. http://www.reveal-tech.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package com.sitewhere.solr;

import java.io.IOException;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.apache.log4j.Logger;
import org.apache.solr.client.solrj.SolrServerException;
import org.apache.solr.client.solrj.impl.HttpSolrServer;
import org.apache.solr.client.solrj.response.UpdateResponse;
import org.apache.solr.common.SolrInputDocument;
import org.springframework.beans.factory.InitializingBean;

import com.sitewhere.rest.model.device.event.processor.DeviceEventProcessor;
import com.sitewhere.spi.SiteWhereException;
import com.sitewhere.spi.device.event.IDeviceAlert;
import com.sitewhere.spi.device.event.IDeviceLocation;
import com.sitewhere.spi.device.event.IDeviceMeasurements;
import com.sitewhere.spi.device.event.processor.IDeviceEventProcessor;

/**
 * {@link IDeviceEventProcessor} implementation that takes saved events and indexes them
 * in Apache Solr for advanced analytics processing.
 * 
 * @author Derek
 */
public class SolrDeviceEventProcessor extends DeviceEventProcessor implements InitializingBean {

	/** Static logger instance */
	private static Logger LOGGER = Logger.getLogger(SolrDeviceEventProcessor.class);

	/** Default URL for contacting Solr server */
	private static final String DEFAULT_SOLR_URL = "http://localhost:8983/solr";

	/** Number of documents to buffer before blocking calls */
	private static final int BUFFER_SIZE = 1000;

	/** URL used to interact with Solr server */
	private String solrServerUrl = DEFAULT_SOLR_URL;

	/** Solr server instance */
	private HttpSolrServer solr;

	/** Bounded queue that holds documents to be processed */
	private ArrayBlockingQueue<SolrInputDocument> queue = new ArrayBlockingQueue<SolrInputDocument>(
			BUFFER_SIZE);

	/** Used to execute Solr indexing in a separate thread */
	/** TODO: Use a better approach for scalability */
	private ExecutorService executor = Executors.newSingleThreadExecutor();

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.springframework.beans.factory.InitializingBean#afterPropertiesSet()
	 */
	@Override
	public void afterPropertiesSet() throws Exception {
		solr = new HttpSolrServer(getSolrServerUrl());
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.sitewhere.rest.model.device.event.processor.DeviceEventProcessor#start()
	 */
	@Override
	public void start() throws SiteWhereException {
		super.start();
		LOGGER.info("Device managment will send indexing information to Solr server at: "
				+ getSolrServerUrl());
		executor.execute(new SolrDocumentQueueProcessor());
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.sitewhere.rest.model.device.event.processor.DeviceEventProcessor#stop()
	 */
	@Override
	public void stop() throws SiteWhereException {
		super.stop();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.sitewhere.rest.model.device.event.processor.DeviceEventProcessor#afterMeasurements
	 * (com.sitewhere.spi.device.event.IDeviceMeasurements)
	 */
	@Override
	public void afterMeasurements(IDeviceMeasurements measurements) throws SiteWhereException {
		SolrInputDocument document = SiteWhereSolrFactory.createDocumentFromMeasurements(measurements);
		try {
			queue.put(document);
		} catch (InterruptedException e) {
			throw new SiteWhereException("Interrupted during indexing.", e);
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.sitewhere.rest.model.device.event.processor.DeviceEventProcessor#afterLocation
	 * (com.sitewhere.spi.device.event.IDeviceLocation)
	 */
	@Override
	public void afterLocation(IDeviceLocation location) throws SiteWhereException {
		SolrInputDocument document = SiteWhereSolrFactory.createDocumentFromLocation(location);
		try {
			queue.put(document);
		} catch (InterruptedException e) {
			throw new SiteWhereException("Interrupted during indexing.", e);
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.sitewhere.rest.model.device.event.processor.DeviceEventProcessor#afterAlert
	 * (com.sitewhere.spi.device.event.IDeviceAlert)
	 */
	@Override
	public void afterAlert(IDeviceAlert alert) throws SiteWhereException {
		SolrInputDocument document = SiteWhereSolrFactory.createDocumentFromAlert(alert);
		try {
			queue.put(document);
		} catch (InterruptedException e) {
			throw new SiteWhereException("Interrupted during indexing.", e);
		}
	}

	public String getSolrServerUrl() {
		return solrServerUrl;
	}

	public void setSolrServerUrl(String solrServerUrl) {
		this.solrServerUrl = solrServerUrl;
	}

	/**
	 * Class that processes documents in the queue asynchronously.
	 * 
	 * @author Derek
	 */
	private class SolrDocumentQueueProcessor implements Runnable {

		@Override
		public void run() {
			LOGGER.info("Started Solr indexing thread.");
			while (true) {
				try {
					SolrInputDocument document = queue.take();
					try {
						UpdateResponse response = solr.add(document);
						if (response.getStatus() != 0) {
							LOGGER.info("Bad response code indexing document: " + response.getStatus());
						}
					} catch (SolrServerException e) {
						LOGGER.error("Exception indexing SiteWhere document.", e);
					} catch (IOException e) {
						LOGGER.error("IOException indexing SiteWhere document.", e);
					} catch (Throwable e) {
						LOGGER.error("Unhandled exception indexing SiteWhere document.", e);
					}
				} catch (InterruptedException e) {
					LOGGER.error(e);
				}
			}
		}
	}
}