/*
 * Copyright (c) SiteWhere, LLC. All rights reserved. http://www.sitewhere.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package com.sitewhere.siddhi;

import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;
import org.wso2.siddhi.core.SiddhiManager;
import org.wso2.siddhi.core.stream.input.InputHandler;

import com.sitewhere.device.event.processor.OutboundEventProcessor;
import com.sitewhere.spi.SiteWhereException;
import com.sitewhere.spi.device.event.IDeviceAlert;
import com.sitewhere.spi.device.event.processor.IOutboundEventProcessor;

/**
 * Implementation of {@link IOutboundEventProcessor} that injects events into Siddhi for
 * complex event processing.
 * 
 * @author Derek
 */
public class SiddhiEventProcessor extends OutboundEventProcessor implements IOutboundEventProcessor {

	/** Static logger instance */
	private static Logger LOGGER = Logger.getLogger(SiddhiEventProcessor.class);

	/** Defines the alert event stream */
	private static String DEFINE_ALERT_STREAM = "define stream AlertStream ("
			+ "id string, site string, assignment string, assetmodule string, asset string, eventdate long,"
			+ "source string, level string, type string, message string);";

	/** Siddhi manager */
	private SiddhiManager manager;

	/** Sends events to alert stream */
	private InputHandler alertInputHandler;

	/** List of queries that are registered with Siddhi */
	private List<SiddhiQuery> queries = new ArrayList<SiddhiQuery>();

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.sitewhere.spi.server.lifecycle.ILifecycleComponent#start()
	 */
	@Override
	public void start() throws SiteWhereException {
		manager = new SiddhiManager();
		createStreams();
		registerQueries();
	}

	/**
	 * Create the streams for standard SiteWhere event types.
	 * 
	 * @throws SiteWhereException
	 */
	protected void createStreams() throws SiteWhereException {
		this.alertInputHandler = getManager().defineStream(DEFINE_ALERT_STREAM);
	}

	/**
	 * Register queries and associated callbacks with {@link SiddhiManager}.
	 * 
	 * @throws SiteWhereException
	 */
	protected void registerQueries() throws SiteWhereException {
		for (SiddhiQuery query : queries) {
			getManager().addQuery(query.getSelector());
			for (String streamName : query.getCallbacks().keySet()) {
				getManager().addCallback(streamName, query.getCallbacks().get(streamName));
			}
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.sitewhere.device.event.processor.OutboundEventProcessor#onAlert(com.sitewhere
	 * .spi.device.event.IDeviceAlert)
	 */
	@Override
	public void onAlert(IDeviceAlert alert) throws SiteWhereException {
		try {
			getAlertInputHandler().send(
					new Object[] {
							alert.getId(),
							alert.getSiteToken(),
							alert.getDeviceAssignmentToken(),
							alert.getAssetModuleId(),
							alert.getAssetId(),
							alert.getEventDate().getTime(),
							alert.getSource().name(),
							alert.getLevel().name(),
							alert.getType(),
							alert.getMessage() });
		} catch (InterruptedException e) {
			throw new SiteWhereException("Unable to process alert in Siddhi.", e);
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.sitewhere.spi.server.lifecycle.ILifecycleComponent#stop()
	 */
	@Override
	public void stop() throws SiteWhereException {
		if (manager != null) {
			manager.shutdown();
			manager = null;
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.sitewhere.spi.server.lifecycle.ILifecycleComponent#getLogger()
	 */
	@Override
	public Logger getLogger() {
		return LOGGER;
	}

	public SiddhiManager getManager() {
		return manager;
	}

	public void setManager(SiddhiManager manager) {
		this.manager = manager;
	}

	public InputHandler getAlertInputHandler() {
		return alertInputHandler;
	}

	public void setAlertInputHandler(InputHandler alertInputHandler) {
		this.alertInputHandler = alertInputHandler;
	}

	public List<SiddhiQuery> getQueries() {
		return queries;
	}

	public void setQueries(List<SiddhiQuery> queries) {
		this.queries = queries;
	}
}