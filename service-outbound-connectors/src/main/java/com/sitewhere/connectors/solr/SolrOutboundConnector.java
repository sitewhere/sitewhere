/**
 * Copyright © 2014-2021 The SiteWhere Authors
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.sitewhere.connectors.solr;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import org.apache.solr.client.solrj.SolrServerException;
import org.apache.solr.client.solrj.response.SolrPingResponse;
import org.apache.solr.client.solrj.response.UpdateResponse;
import org.apache.solr.common.SolrInputDocument;

import com.sitewhere.connectors.SerialOutboundConnector;
import com.sitewhere.connectors.spi.IOutboundConnector;
import com.sitewhere.solr.SiteWhereSolrFactory;
import com.sitewhere.solr.SolrConfiguration;
import com.sitewhere.solr.SolrConnection;
import com.sitewhere.spi.SiteWhereException;
import com.sitewhere.spi.device.event.IDeviceAlert;
import com.sitewhere.spi.device.event.IDeviceEventContext;
import com.sitewhere.spi.device.event.IDeviceLocation;
import com.sitewhere.spi.device.event.IDeviceMeasurement;
import com.sitewhere.spi.microservice.lifecycle.ILifecycleProgressMonitor;

/**
 * {@link IOutboundConnector} implementation that takes saved events and indexes
 * them in Apache Solr for advanced analytics processing.
 */
public class SolrOutboundConnector extends SerialOutboundConnector {

    /** Number of documents to buffer before blocking calls */
    private static final int BUFFER_SIZE = 1000;

    /** Interval at which batches are written */
    private static final int BATCH_INTERVAL = 2 * 1000;

    /** Maximum count of documents to send in a batch */
    private static final int MAX_BATCH_SIZE = 200;

    /** Interval by which documents should be committed */
    private static final int COMMIT_INTERVAL = 60 * 1000;

    /** Injected Solr configuration */
    private SolrConfiguration solrConfiguration;

    /** Connection to Solr instance */
    private SolrConnection solrConnection;

    /** Bounded queue that holds documents to be processed */
    private BlockingQueue<SolrInputDocument> queue = new ArrayBlockingQueue<SolrInputDocument>(BUFFER_SIZE);

    /** Used to execute Solr indexing in a separate thread */
    /** TODO: Use a better approach for scalability */
    private ExecutorService executor = Executors.newSingleThreadExecutor();

    /*
     * @see
     * com.sitewhere.connectors.FilteredOutboundConnector#start(com.sitewhere.spi.
     * server.lifecycle.ILifecycleProgressMonitor)
     */
    @Override
    public void start(ILifecycleProgressMonitor monitor) throws SiteWhereException {
	// Required for filters.
	super.start(monitor);

	if (getSolrConfiguration() == null) {
	    throw new SiteWhereException("No Solr configuration provided.");
	}
	try {
	    // Create and start Solr connection.
	    this.solrConnection = new SolrConnection(getSolrConfiguration());
	    getSolrConnection().start(monitor);

	    getLogger().info("Attempting to ping Solr server to verify availability...");
	    SolrPingResponse response = getSolrConnection().getSolrClient().ping();
	    int pingTime = response.getQTime();
	    getLogger().info("Solr server location verified. Ping responded in " + pingTime + " ms.");
	} catch (SolrServerException e) {
	    throw new SiteWhereException("Ping failed. Verify that Solr server is available.", e);
	} catch (IOException e) {
	    throw new SiteWhereException("Exception in ping. Verify that Solr server is available.", e);
	}
	getLogger().info(
		"Solr event processor indexing events to server at: " + getSolrConfiguration().getSolrServerUrl());
	executor.execute(new SolrDocumentQueueProcessor());
    }

    /*
     * @see
     * com.sitewhere.connectors.SerialOutboundConnector#onMeasurement(com.sitewhere.
     * spi.device.event.IDeviceEventContext,
     * com.sitewhere.spi.device.event.IDeviceMeasurement)
     */
    @Override
    public void onMeasurement(IDeviceEventContext context, IDeviceMeasurement mx) throws SiteWhereException {
	SolrInputDocument document = SiteWhereSolrFactory.createDocumentFromMeasurement(mx);
	try {
	    queue.put(document);
	} catch (InterruptedException e) {
	    throw new SiteWhereException("Interrupted during indexing.", e);
	}
    }

    /*
     * @see
     * com.sitewhere.connectors.SerialOutboundConnector#onLocation(com.sitewhere.spi
     * .device.event.IDeviceEventContext,
     * com.sitewhere.spi.device.event.IDeviceLocation)
     */
    @Override
    public void onLocation(IDeviceEventContext context, IDeviceLocation location) throws SiteWhereException {
	SolrInputDocument document = SiteWhereSolrFactory.createDocumentFromLocation(location);
	try {
	    queue.put(document);
	} catch (InterruptedException e) {
	    throw new SiteWhereException("Interrupted during indexing.", e);
	}
    }

    /*
     * @see
     * com.sitewhere.connectors.SerialOutboundConnector#onAlert(com.sitewhere.spi.
     * device.event.IDeviceEventContext,
     * com.sitewhere.spi.device.event.IDeviceAlert)
     */
    @Override
    public void onAlert(IDeviceEventContext context, IDeviceAlert alert) throws SiteWhereException {
	SolrInputDocument document = SiteWhereSolrFactory.createDocumentFromAlert(alert);
	try {
	    queue.put(document);
	} catch (InterruptedException e) {
	    throw new SiteWhereException("Interrupted during indexing.", e);
	}
    }

    /**
     * Class that processes documents in the queue asynchronously.
     */
    private class SolrDocumentQueueProcessor implements Runnable {

	@Override
	public void run() {
	    getLogger().info("Started Solr indexing thread.");
	    boolean interrupted = false;

	    while (!interrupted) {
		long start = System.currentTimeMillis();
		List<SolrInputDocument> batch = new ArrayList<SolrInputDocument>();

		while (((System.currentTimeMillis() - start) < BATCH_INTERVAL) && (batch.size() <= MAX_BATCH_SIZE)) {
		    try {
			SolrInputDocument document = queue.poll(BATCH_INTERVAL, TimeUnit.MILLISECONDS);
			if (document != null) {
			    batch.add(document);
			}
		    } catch (InterruptedException e) {
			getLogger().error("Solr indexing thread interrupted.", e);
			interrupted = true;
			break;
		    }
		}
		if (!batch.isEmpty()) {
		    try {
			UpdateResponse response = getSolrConnection().getSolrClient().add(batch, COMMIT_INTERVAL);
			if (response.getStatus() != 0) {
			    getLogger().warn("Bad response code indexing documents: " + response.getStatus());
			}
			getLogger().debug("Indexed " + batch.size() + " documents in Solr.");
		    } catch (SolrServerException e) {
			getLogger().error("Exception indexing SiteWhere document.", e);
		    } catch (IOException e) {
			getLogger().error("IOException indexing SiteWhere document.", e);
		    } catch (Throwable e) {
			getLogger().error("Unhandled exception indexing SiteWhere document.", e);
		    }
		}
	    }
	}
    }

    public SolrConfiguration getSolrConfiguration() {
	return solrConfiguration;
    }

    public void setSolrConfiguration(SolrConfiguration solrConfiguration) {
	this.solrConfiguration = solrConfiguration;
    }

    public SolrConnection getSolrConnection() {
	return solrConnection;
    }

    public void setSolrConnection(SolrConnection solrConnection) {
	this.solrConnection = solrConnection;
    }
}