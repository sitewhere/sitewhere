/*
 * Copyright (c) SiteWhere, LLC. All rights reserved. http://www.sitewhere.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package com.sitewhere.outbound.siddhi;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URI;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.wso2.carbon.databridge.agent.thrift.DataPublisher;
import org.wso2.carbon.databridge.agent.thrift.exception.AgentException;
import org.wso2.carbon.databridge.commons.Attribute;
import org.wso2.carbon.databridge.commons.AttributeType;
import org.wso2.carbon.databridge.commons.Event;
import org.wso2.carbon.databridge.commons.StreamDefinition;
import org.wso2.carbon.databridge.commons.exception.AuthenticationException;
import org.wso2.carbon.databridge.commons.exception.MalformedStreamDefinitionException;
import org.wso2.carbon.databridge.commons.exception.TransportException;

import com.sitewhere.SiteWhere;
import com.sitewhere.device.event.processor.FilteredOutboundEventProcessor;
import com.sitewhere.spi.SiteWhereException;
import com.sitewhere.spi.device.event.IDeviceAlert;
import com.sitewhere.spi.device.event.IDeviceLocation;
import com.sitewhere.spi.device.event.IDeviceMeasurements;
import com.sitewhere.spi.server.lifecycle.ILifecycleProgressMonitor;

/**
 * Sends SiteWhere events to an external WSO2 CEP instance.
 * 
 * @author Derek
 */
public class Wso2CepEventProcessor extends FilteredOutboundEventProcessor {

    /** Static logger instance */
    private static Logger LOGGER = LogManager.getLogger();

    /** Measurements stream id */
    private static final String MEAUREMENTS_STREAM_ID = "com.sitewhere.Measurements";

    /** Locations stream id */
    private static final String LOCATIONS_STREAM_ID = "com.sitewhere.Locations";

    /** Alerts stream id */
    private static final String ALERTS_STREAM_ID = "com.sitewhere.Alerts";

    /** Protocol for external Siddhi server */
    private String siddhiProtocol = "tcp";

    /** Hostname for external Siddhi server */
    private String siddhiHost = "localhost";

    /** Port number for external Siddhi server */
    private int siddhiPort = 7611;

    /** Username for external Siddhi server */
    private String siddhiUsername = "admin";

    /** Password for external Siddhi server */
    private String siddhiPassword = "admin";

    /** WSO2 data publisher */
    private DataPublisher publisher;

    /** Stream for measurements */
    private String streamMeasurements;

    /** Stream for locations */
    private String streamLocations;

    /** Stream for alerts */
    private String streamAlerts;

    /*
     * (non-Javadoc)
     * 
     * @see
     * com.sitewhere.device.event.processor.FilteredOutboundEventProcessor#start
     * (com.sitewhere.spi.server.lifecycle.ILifecycleProgressMonitor)
     */
    @Override
    public void start(ILifecycleProgressMonitor monitor) throws SiteWhereException {
	// Required for filters.
	super.start(monitor);

	try {
	    URI rootUri = SiteWhere.getServer().getConfigurationResolver().getFilesystemConfigurationRoot();
	    File rootFolder = new File(rootUri.getPath());
	    if (!rootFolder.exists()) {
		throw new SiteWhereException("Configuration root does not exist.");
	    }

	    File security = new File(rootFolder, "security");
	    if (!security.exists()) {
		throw new SiteWhereException("Security folder does not exist: " + security.getAbsolutePath());
	    }

	    File jks = new File(security, "client-truststore.jks");
	    if (!jks.exists()) {
		throw new SiteWhereException("Trust store not found: " + jks.getAbsolutePath());
	    }

	    System.setProperty("javax.net.ssl.trustStore", jks.getAbsolutePath());
	    System.setProperty("javax.net.ssl.trustStorePassword", "wso2carbon");

	    String url = getSiddhiProtocol() + "://" + getSiddhiHost() + ":" + getSiddhiPort();
	    LOGGER.info("Connecting to WSO2 CEP instance at: " + url);
	    this.publisher = new DataPublisher(url, getSiddhiUsername(), getSiddhiPassword());
	} catch (MalformedURLException e) {
	    throw new SiteWhereException("Invalid URL for external Siddhi server.", e);
	} catch (AgentException e) {
	    throw new SiteWhereException("Agent problem for external Siddhi server.", e);
	} catch (AuthenticationException e) {
	    throw new SiteWhereException("Authentication problem for external Siddhi server.", e);
	} catch (TransportException e) {
	    throw new SiteWhereException("Transport problem for external Siddhi server.", e);
	}

	createStreams();
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * com.sitewhere.device.event.processor.FilteredOutboundEventProcessor#stop(
     * com.sitewhere.spi.server.lifecycle.ILifecycleProgressMonitor)
     */
    @Override
    public void stop(ILifecycleProgressMonitor monitor) throws SiteWhereException {
	// Stop filters.
	super.stop(monitor);

	// Shut the publisher down.
	if (publisher != null) {
	    publisher.stopNow();
	}
    }

    /**
     * Create the streams for standard SiteWhere event types.
     * 
     * @throws SiteWhereException
     */
    protected void createStreams() throws SiteWhereException {
	try {
	    this.streamMeasurements = getPublisher().defineStream(createMeasurementsStreamDefinition());
	} catch (Throwable e) {
	    LOGGER.info("Unable to create Siddhi measurements stream.", e);
	}

	try {
	    this.streamLocations = getPublisher().defineStream(createLocationsStreamDefinition());
	} catch (Throwable e) {
	    LOGGER.info("Unable to create Siddhi locations stream.", e);
	}

	try {
	    this.streamAlerts = getPublisher().defineStream(createAlertsStreamDefinition());
	} catch (Throwable e) {
	    LOGGER.info("Unable to create Siddhi alerts stream.", e);
	}
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.sitewhere.device.event.processor.FilteredOutboundEventProcessor#
     * onMeasurementsNotFiltered(com.sitewhere.spi.device.event.
     * IDeviceMeasurements)
     */
    @Override
    public void onMeasurementsNotFiltered(IDeviceMeasurements measurements) throws SiteWhereException {
	Map<String, Double> mxs = measurements.getMeasurements();
	for (String key : mxs.keySet()) {
	    Event event = new Event(streamMeasurements, System.currentTimeMillis(), null, null,
		    new Object[] { measurements.getId(), measurements.getSiteToken(),
			    measurements.getDeviceAssignmentToken(), measurements.getAssetModuleId(),
			    measurements.getAssetId(), measurements.getEventDate().getTime(), key, mxs.get(key) });
	    try {
		getPublisher().publish(event);
	    } catch (AgentException e) {
		throw new SiteWhereException("Unable to publish measurement event.", e);
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
	Event event = new Event(streamLocations, System.currentTimeMillis(), null, null,
		new Object[] { location.getId(), location.getSiteToken(), location.getDeviceAssignmentToken(),
			location.getAssetModuleId(), location.getAssetId(), location.getEventDate().getTime(),
			location.getLatitude(), location.getLongitude(), location.getElevation() });
	try {
	    getPublisher().publish(event);
	} catch (AgentException e) {
	    throw new SiteWhereException("Unable to publish location event.", e);
	}
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.sitewhere.device.event.processor.FilteredOutboundEventProcessor#
     * onAlertNotFiltered (com.sitewhere.spi.device.event.IDeviceAlert)
     */
    @Override
    public void onAlertNotFiltered(IDeviceAlert alert) throws SiteWhereException {
	Event event = new Event(streamAlerts, System.currentTimeMillis(), null, null,
		new Object[] { alert.getId(), alert.getSiteToken(), alert.getDeviceAssignmentToken(),
			alert.getAssetModuleId(), alert.getAssetId(), alert.getEventDate().getTime(), alert.getSource(),
			alert.getLevel(), alert.getType(), alert.getMessage() });
	try {
	    getPublisher().publish(event);
	} catch (AgentException e) {
	    throw new SiteWhereException("Unable to publish alert event.", e);
	}
    }

    /**
     * Create stream definition for SiteWhere measurements.
     * 
     * @return
     * @throws MalformedStreamDefinitionException
     */
    protected StreamDefinition createMeasurementsStreamDefinition() throws MalformedStreamDefinitionException {
	StreamDefinition definition = new StreamDefinition(MEAUREMENTS_STREAM_ID,
		SiteWhere.getServer().getVersion().getVersionIdentifier());
	definition.setNickName("SiteWhere Measurements");
	definition.setDescription("SiteWhere Measurements");
	List<Attribute> attributes = new ArrayList<Attribute>();
	attributes.add(new Attribute("id", AttributeType.STRING));
	attributes.add(new Attribute("site", AttributeType.STRING));
	attributes.add(new Attribute("assignment", AttributeType.STRING));
	attributes.add(new Attribute("assetmodule", AttributeType.STRING));
	attributes.add(new Attribute("asset", AttributeType.STRING));
	attributes.add(new Attribute("eventdate", AttributeType.LONG));
	attributes.add(new Attribute("mxname", AttributeType.STRING));
	attributes.add(new Attribute("mxvalue", AttributeType.DOUBLE));
	definition.setPayloadData(attributes);
	return definition;
    }

    /**
     * Create stream definition for SiteWhere locations.
     * 
     * @return
     * @throws MalformedStreamDefinitionException
     */
    protected StreamDefinition createLocationsStreamDefinition() throws MalformedStreamDefinitionException {
	StreamDefinition definition = new StreamDefinition(LOCATIONS_STREAM_ID,
		SiteWhere.getServer().getVersion().getVersionIdentifier());
	definition.setNickName("SiteWhere Locations");
	definition.setDescription("SiteWhere Locations");
	List<Attribute> attributes = new ArrayList<Attribute>();
	attributes.add(new Attribute("id", AttributeType.STRING));
	attributes.add(new Attribute("site", AttributeType.STRING));
	attributes.add(new Attribute("assignment", AttributeType.STRING));
	attributes.add(new Attribute("assetmodule", AttributeType.STRING));
	attributes.add(new Attribute("asset", AttributeType.STRING));
	attributes.add(new Attribute("eventdate", AttributeType.LONG));
	attributes.add(new Attribute("latitude", AttributeType.DOUBLE));
	attributes.add(new Attribute("longitude", AttributeType.DOUBLE));
	attributes.add(new Attribute("elevation", AttributeType.DOUBLE));
	definition.setPayloadData(attributes);
	return definition;
    }

    /**
     * Create stream definition for SiteWhere locations.
     * 
     * @return
     * @throws MalformedStreamDefinitionException
     */
    protected StreamDefinition createAlertsStreamDefinition() throws MalformedStreamDefinitionException {
	StreamDefinition definition = new StreamDefinition(ALERTS_STREAM_ID,
		SiteWhere.getServer().getVersion().getVersionIdentifier());
	definition.setNickName("SiteWhere Alerts");
	definition.setDescription("SiteWhere Alerts");
	List<Attribute> attributes = new ArrayList<Attribute>();
	attributes.add(new Attribute("id", AttributeType.STRING));
	attributes.add(new Attribute("site", AttributeType.STRING));
	attributes.add(new Attribute("assignment", AttributeType.STRING));
	attributes.add(new Attribute("assetmodule", AttributeType.STRING));
	attributes.add(new Attribute("asset", AttributeType.STRING));
	attributes.add(new Attribute("eventdate", AttributeType.LONG));
	attributes.add(new Attribute("source", AttributeType.STRING));
	attributes.add(new Attribute("level", AttributeType.STRING));
	attributes.add(new Attribute("type", AttributeType.STRING));
	attributes.add(new Attribute("message", AttributeType.STRING));
	definition.setPayloadData(attributes);
	return definition;
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

    public String getSiddhiProtocol() {
	return siddhiProtocol;
    }

    public void setSiddhiProtocol(String siddhiProtocol) {
	this.siddhiProtocol = siddhiProtocol;
    }

    public String getSiddhiHost() {
	return siddhiHost;
    }

    public void setSiddhiHost(String siddhiHost) {
	this.siddhiHost = siddhiHost;
    }

    public int getSiddhiPort() {
	return siddhiPort;
    }

    public void setSiddhiPort(int siddhiPort) {
	this.siddhiPort = siddhiPort;
    }

    public String getSiddhiUsername() {
	return siddhiUsername;
    }

    public void setSiddhiUsername(String siddhiUsername) {
	this.siddhiUsername = siddhiUsername;
    }

    public String getSiddhiPassword() {
	return siddhiPassword;
    }

    public void setSiddhiPassword(String siddhiPassword) {
	this.siddhiPassword = siddhiPassword;
    }

    public DataPublisher getPublisher() {
	return publisher;
    }

    public void setPublisher(DataPublisher publisher) {
	this.publisher = publisher;
    }
}