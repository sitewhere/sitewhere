/*
 * Copyright (c) SiteWhere, LLC. All rights reserved. http://www.sitewhere.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package com.sitewhere.connectors.azure;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.net.URLEncoder;
import java.util.Hashtable;

import javax.jms.BytesMessage;
import javax.jms.Connection;
import javax.jms.ConnectionFactory;
import javax.jms.Destination;
import javax.jms.JMSException;
import javax.jms.MessageProducer;
import javax.jms.Session;
import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.sitewhere.common.MarshalUtils;
import com.sitewhere.connectors.FilteredOutboundConnector;
import com.sitewhere.connectors.spi.IOutboundConnector;
import com.sitewhere.spi.SiteWhereException;
import com.sitewhere.spi.device.event.IDeviceAlert;
import com.sitewhere.spi.device.event.IDeviceCommandInvocation;
import com.sitewhere.spi.device.event.IDeviceCommandResponse;
import com.sitewhere.spi.device.event.IDeviceEvent;
import com.sitewhere.spi.device.event.IDeviceEventContext;
import com.sitewhere.spi.device.event.IDeviceLocation;
import com.sitewhere.spi.device.event.IDeviceMeasurements;
import com.sitewhere.spi.server.lifecycle.ILifecycleProgressMonitor;

/**
 * Implementation of {@link IOutboundConnector} that sends events to an
 * EventHub running on Azure.
 * 
 * @author Derek
 */
public class EventHubOutboundEventProcessor extends FilteredOutboundConnector {

    /** Static logger instance */
    private static final Logger LOGGER = LogManager.getLogger();

    /** SAS identity name */
    private String sasName;

    /** SAS key */
    private String sasKey;

    /** Service bus name */
    private String serviceBusName;

    /** Event hub name */
    private String eventHubName;

    /** JMS objects */
    private ConnectionFactory factory;
    private Destination destination;
    private Connection connection;
    private Session session;
    private MessageProducer sender;

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
	    String key = URLEncoder.encode(getSasKey(), "UTF8");
	    String connectionString = "amqps://" + getSasName() + ":" + key + "@" + getServiceBusName();
	    File file = File.createTempFile("eventhub", ".props");
	    BufferedWriter writer = new BufferedWriter(new FileWriter(file));
	    writer.write("connectionfactory.SBCF = " + connectionString);
	    writer.newLine();
	    writer.write("queue.EVENTHUB = " + getEventHubName());
	    writer.newLine();
	    writer.close();

	    Hashtable<String, String> env = new Hashtable<String, String>();
	    env.put(Context.INITIAL_CONTEXT_FACTORY,
		    "org.apache.qpid.amqp_1_0.jms.jndi.PropertiesFileInitialContextFactory");
	    env.put(Context.PROVIDER_URL, file.getAbsolutePath());
	    Context context = new InitialContext(env);

	    this.factory = (ConnectionFactory) context.lookup("SBCF");
	    this.destination = (Destination) context.lookup("EVENTHUB");
	    this.connection = factory.createConnection();
	    this.session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);
	    this.sender = session.createProducer(destination);
	} catch (IOException e) {
	    throw new SiteWhereException(e);
	} catch (NamingException e) {
	    throw new SiteWhereException(e);
	} catch (JMSException e) {
	    throw new SiteWhereException(e);
	}
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
	super.stop(monitor);
	if (sender != null) {
	    try {
		sender.close();
	    } catch (JMSException e) {
		LOGGER.warn("Error closing message source for EventHub processor.", e);
	    }
	}
	if (session != null) {
	    try {
		session.close();
	    } catch (JMSException e) {
		LOGGER.warn("Error closing session for EventHub processor.", e);
	    }
	}
	if (connection != null) {
	    try {
		connection.close();
	    } catch (JMSException e) {
		LOGGER.warn("Error closing session for EventHub processor.", e);
	    }
	}
    }

    /*
     * @see com.sitewhere.outbound.FilteredOutboundEventProcessor#
     * onMeasurementsNotFiltered(com.sitewhere.spi.device.event.IDeviceEventContext,
     * com.sitewhere.spi.device.event.IDeviceMeasurements)
     */
    @Override
    public void onMeasurementsNotFiltered(IDeviceEventContext context, IDeviceMeasurements measurements)
	    throws SiteWhereException {
	sendEvent(measurements);
    }

    /*
     * @see
     * com.sitewhere.outbound.FilteredOutboundEventProcessor#onLocationNotFiltered(
     * com.sitewhere.spi.device.event.IDeviceEventContext,
     * com.sitewhere.spi.device.event.IDeviceLocation)
     */
    @Override
    public void onLocationNotFiltered(IDeviceEventContext context, IDeviceLocation location) throws SiteWhereException {
	sendEvent(location);
    }

    /*
     * @see
     * com.sitewhere.outbound.FilteredOutboundEventProcessor#onAlertNotFiltered(com.
     * sitewhere.spi.device.event.IDeviceEventContext,
     * com.sitewhere.spi.device.event.IDeviceAlert)
     */
    @Override
    public void onAlertNotFiltered(IDeviceEventContext context, IDeviceAlert alert) throws SiteWhereException {
	sendEvent(alert);
    }

    /*
     * @see com.sitewhere.outbound.FilteredOutboundEventProcessor#
     * onCommandInvocationNotFiltered(com.sitewhere.spi.device.event.
     * IDeviceEventContext, com.sitewhere.spi.device.event.IDeviceCommandInvocation)
     */
    @Override
    public void onCommandInvocationNotFiltered(IDeviceEventContext context, IDeviceCommandInvocation invocation)
	    throws SiteWhereException {
	sendEvent(invocation);
    }

    /*
     * @see com.sitewhere.outbound.FilteredOutboundEventProcessor#
     * onCommandResponseNotFiltered(com.sitewhere.spi.device.event.
     * IDeviceEventContext, com.sitewhere.spi.device.event.IDeviceCommandResponse)
     */
    @Override
    public void onCommandResponseNotFiltered(IDeviceEventContext context, IDeviceCommandResponse response)
	    throws SiteWhereException {
	sendEvent(response);
    }

    /**
     * Marshals an event to JSON and sends it to EventHub via AMQP.
     * 
     * @param event
     * @throws SiteWhereException
     */
    protected void sendEvent(IDeviceEvent event) throws SiteWhereException {
	try {
	    BytesMessage message = session.createBytesMessage();
	    message.writeBytes(MarshalUtils.marshalJson(event));
	    message.setJMSMessageID("ID:" + event.getId());
	    sender.send(message);
	} catch (JMSException e) {
	    throw new SiteWhereException(e);
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

    public String getSasName() {
	return sasName;
    }

    public void setSasName(String sasName) {
	this.sasName = sasName;
    }

    public String getSasKey() {
	return sasKey;
    }

    public void setSasKey(String sasKey) {
	this.sasKey = sasKey;
    }

    public String getServiceBusName() {
	return serviceBusName;
    }

    public void setServiceBusName(String serviceBusName) {
	this.serviceBusName = serviceBusName;
    }

    public String getEventHubName() {
	return eventHubName;
    }

    public void setEventHubName(String eventHubName) {
	this.eventHubName = eventHubName;
    }
}