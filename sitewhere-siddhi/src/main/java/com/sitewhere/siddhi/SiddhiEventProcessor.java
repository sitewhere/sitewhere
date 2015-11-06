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
import org.wso2.siddhi.core.stream.output.StreamCallback;

import com.sitewhere.device.event.processor.FilteredOutboundEventProcessor;
import com.sitewhere.spi.SiteWhereException;
import com.sitewhere.spi.device.event.IDeviceAlert;
import com.sitewhere.spi.device.event.IDeviceLocation;
import com.sitewhere.spi.device.event.IDeviceMeasurements;
import com.sitewhere.spi.device.event.processor.IOutboundEventProcessor;

/**
 * Implementation of {@link IOutboundEventProcessor} that injects events into Siddhi for
 * complex event processing.
 * 
 * @author Derek
 */
public class SiddhiEventProcessor extends FilteredOutboundEventProcessor {

	/** Static logger instance */
	private static Logger LOGGER = Logger.getLogger(SiddhiEventProcessor.class);

	/** Defines the measurement event stream */
	private static String DEFINE_MEASUREMENT_STREAM = "define stream MeasurementStream ("
			+ "id string, site string, assignment string, assetmodule string, asset string, eventdate long,"
			+ "mxname string, mxvalue float);";

	/** Defines the location event stream */
	private static String DEFINE_LOCATION_STREAM = "define stream LocationStream ("
			+ "id string, site string, assignment string, assetmodule string, asset string, eventdate long,"
			+ "latitude float, longitude float, elevation float);";

	/** Defines the alert event stream */
	private static String DEFINE_ALERT_STREAM = "define stream AlertStream ("
			+ "id string, site string, assignment string, assetmodule string, asset string, eventdate long,"
			+ "source string, level string, type string, message string);";

	/** Siddhi manager */
	private SiddhiManager manager;

	/** Sends events to measurement stream */
	private InputHandler mxInputHandler;

	/** Sends events to location stream */
	private InputHandler locationInputHandler;

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
		// Required for filters.
		super.start();

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
		this.locationInputHandler = getManager().defineStream(DEFINE_LOCATION_STREAM);
		this.mxInputHandler = getManager().defineStream(DEFINE_MEASUREMENT_STREAM);
	}

	/**
	 * Register queries and associated callbacks with {@link SiddhiManager}.
	 * 
	 * @throws SiteWhereException
	 */
	protected void registerQueries() throws SiteWhereException {
		for (SiddhiQuery query : queries) {
			getManager().addQuery(query.getSelector());
			for (StreamCallback callback : query.getCallbacks()) {
				getManager().addCallback(callback.getStreamId(), callback);
			}
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.sitewhere.device.event.processor.FilteredOutboundEventProcessor#
	 * onMeasurementsNotFiltered(com.sitewhere.spi.device.event.IDeviceMeasurements)
	 */
	@Override
	public void onMeasurementsNotFiltered(IDeviceMeasurements measurements) throws SiteWhereException {
		for (String mxname : measurements.getMeasurements().keySet()) {
			Double mxvalue = measurements.getMeasurement(mxname);
			try {
				// Send a separate stream event per individual measurement.
				getMxInputHandler().send(
						new Object[] {
								measurements.getId(),
								measurements.getSiteToken(),
								measurements.getDeviceAssignmentToken(),
								measurements.getAssetModuleId(),
								measurements.getAssetId(),
								measurements.getEventDate().getTime(),
								mxname,
								mxvalue.floatValue() });
			} catch (InterruptedException e) {
				throw new SiteWhereException("Unable to process measurement in Siddhi.", e);
			}
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.sitewhere.device.event.processor.FilteredOutboundEventProcessor#
	 * onLocationNotFiltered(com.sitewhere.spi.device.event.IDeviceLocation)
	 */
	@Override
	public void onLocationNotFiltered(IDeviceLocation location) throws SiteWhereException {
		try {
			getLocationInputHandler().send(
					new Object[] {
							location.getId(),
							location.getSiteToken(),
							location.getDeviceAssignmentToken(),
							location.getAssetModuleId(),
							location.getAssetId(),
							location.getEventDate().getTime(),
							location.getLatitude().floatValue(),
							location.getLongitude().floatValue(),
							location.getElevation().floatValue() });
		} catch (InterruptedException e) {
			throw new SiteWhereException("Unable to process alert in Siddhi.", e);
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.sitewhere.device.event.processor.FilteredOutboundEventProcessor#onAlertNotFiltered
	 * (com.sitewhere.spi.device.event.IDeviceAlert)
	 */
	@Override
	public void onAlertNotFiltered(IDeviceAlert alert) throws SiteWhereException {
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
		super.stop();
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

	public InputHandler getMxInputHandler() {
		return mxInputHandler;
	}

	public void setMxInputHandler(InputHandler mxInputHandler) {
		this.mxInputHandler = mxInputHandler;
	}

	public InputHandler getLocationInputHandler() {
		return locationInputHandler;
	}

	public void setLocationInputHandler(InputHandler locationInputHandler) {
		this.locationInputHandler = locationInputHandler;
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