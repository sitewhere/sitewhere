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

import org.apache.log4j.Logger;
import org.apache.solr.client.solrj.SolrServerException;
import org.apache.solr.client.solrj.impl.HttpSolrServer;
import org.apache.solr.client.solrj.response.UpdateResponse;
import org.apache.solr.common.SolrInputDocument;
import org.springframework.beans.factory.InitializingBean;

import com.sitewhere.rest.model.device.DeviceEventBatchResponse;
import com.sitewhere.spi.SiteWhereException;
import com.sitewhere.spi.device.DeviceManagementAdapter;
import com.sitewhere.spi.device.IDeviceAlert;
import com.sitewhere.spi.device.IDeviceAssignment;
import com.sitewhere.spi.device.IDeviceEventBatch;
import com.sitewhere.spi.device.IDeviceEventBatchResponse;
import com.sitewhere.spi.device.IDeviceLocation;
import com.sitewhere.spi.device.IDeviceMeasurements;
import com.sitewhere.spi.device.request.IDeviceAlertCreateRequest;
import com.sitewhere.spi.device.request.IDeviceLocationCreateRequest;
import com.sitewhere.spi.device.request.IDeviceMeasurementsCreateRequest;

/**
 * Wraps a device management implementation so that Apache Solr can index newly created
 * records.
 * 
 * @author Derek
 */
public class SolrDeviceManagementDecorator extends DeviceManagementAdapter implements InitializingBean {

	/** Static logger instance */
	private static Logger LOGGER = Logger.getLogger(SolrDeviceManagementDecorator.class);

	/** Default URL for contacting Solr server */
	private static final String DEFAULT_SOLR_URL = "http://localhost:8983/solr";

	/** URL used to interact with Solr server */
	private String solrServerUrl = DEFAULT_SOLR_URL;

	/** Solr server instance */
	private HttpSolrServer solr;

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
	 * @see com.sitewhere.spi.device.DeviceManagementAdapter#start()
	 */
	@Override
	public void start() throws SiteWhereException {
		super.start();
		LOGGER.info("Device managment will send indexing information to Solr server at: "
				+ getSolrServerUrl());
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.sitewhere.spi.device.DeviceManagementAdapter#addDeviceEventBatch(java.lang.
	 * String, com.sitewhere.spi.device.IDeviceEventBatch)
	 */
	@Override
	public IDeviceEventBatchResponse addDeviceEventBatch(String assignmentToken, IDeviceEventBatch batch)
			throws SiteWhereException {
		DeviceEventBatchResponse response = new DeviceEventBatchResponse();
		IDeviceAssignment assignment = getDeviceAssignmentByToken(assignmentToken);
		for (IDeviceMeasurementsCreateRequest measurements : batch.getMeasurements()) {
			response.getCreatedMeasurements().add(addDeviceMeasurements(assignment, measurements));
		}
		for (IDeviceLocationCreateRequest location : batch.getLocations()) {
			response.getCreatedLocations().add(addDeviceLocation(assignment, location));
		}
		for (IDeviceAlertCreateRequest alert : batch.getAlerts()) {
			response.getCreatedAlerts().add(addDeviceAlert(assignment, alert));
		}
		return response;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.sitewhere.spi.device.IDeviceManagement#addDeviceMeasurements(com.sitewhere.
	 * spi.device.IDeviceAssignment,
	 * com.sitewhere.spi.device.request.IDeviceMeasurementsCreateRequest)
	 */
	@Override
	public IDeviceMeasurements addDeviceMeasurements(IDeviceAssignment assignment,
			IDeviceMeasurementsCreateRequest measurements) throws SiteWhereException {
		IDeviceMeasurements result = super.addDeviceMeasurements(assignment, measurements);
		SolrInputDocument document = SiteWhereSolrFactory.createDocumentFromMeasurements(result);
		indexDocument(document);
		return result;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.sitewhere.spi.device.DeviceManagementAdapter#addDeviceLocation(com.sitewhere
	 * .spi.device.IDeviceAssignment,
	 * com.sitewhere.spi.device.request.IDeviceLocationCreateRequest)
	 */
	@Override
	public IDeviceLocation addDeviceLocation(IDeviceAssignment assignment,
			IDeviceLocationCreateRequest location) throws SiteWhereException {
		IDeviceLocation result = super.addDeviceLocation(assignment, location);
		SolrInputDocument document = SiteWhereSolrFactory.createDocumentFromLocation(result);
		indexDocument(document);
		return result;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.sitewhere.spi.device.DeviceManagementAdapter#addDeviceAlert(com.sitewhere.spi
	 * .device.IDeviceAssignment,
	 * com.sitewhere.spi.device.request.IDeviceAlertCreateRequest)
	 */
	@Override
	public IDeviceAlert addDeviceAlert(IDeviceAssignment assignment, IDeviceAlertCreateRequest alert)
			throws SiteWhereException {
		IDeviceAlert result = super.addDeviceAlert(assignment, alert);
		SolrInputDocument document = SiteWhereSolrFactory.createDocumentFromAlert(result);
		indexDocument(document);
		return result;
	}

	/**
	 * Index a document in Solr.
	 * 
	 * @param document
	 */
	public void indexDocument(SolrInputDocument document) {
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
	}

	public String getSolrServerUrl() {
		return solrServerUrl;
	}

	public void setSolrServerUrl(String solrServerUrl) {
		this.solrServerUrl = solrServerUrl;
	}
}