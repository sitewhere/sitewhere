/*
 * Copyright (c) SiteWhere, LLC. All rights reserved. http://www.sitewhere.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package com.sitewhere.azure.device.communication;

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

import org.apache.log4j.Logger;

import com.sitewhere.common.MarshalUtils;
import com.sitewhere.device.event.processor.FilteredOutboundEventProcessor;
import com.sitewhere.spi.SiteWhereException;
import com.sitewhere.spi.device.event.IDeviceAlert;
import com.sitewhere.spi.device.event.IDeviceCommandInvocation;
import com.sitewhere.spi.device.event.IDeviceCommandResponse;
import com.sitewhere.spi.device.event.IDeviceEvent;
import com.sitewhere.spi.device.event.IDeviceLocation;
import com.sitewhere.spi.device.event.IDeviceMeasurements;
import com.sitewhere.spi.device.event.processor.IOutboundEventProcessor;

/**
 * Implementation of {@link IOutboundEventProcessor} that sends events to an EventHub
 * running on Azure.
 * 
 * @author Derek
 */
public class EventHubOutboundEventProcessor extends FilteredOutboundEventProcessor {

	/** Static logger instance */
	private static final Logger LOGGER = Logger.getLogger(EventHubOutboundEventProcessor.class);

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
	 * @see com.sitewhere.spi.server.lifecycle.ILifecycleComponent#start()
	 */
	@Override
	public void start() throws SiteWhereException {
		// Required for filters.
		super.start();

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
	 * @see com.sitewhere.spi.server.lifecycle.ILifecycleComponent#stop()
	 */
	@Override
	public void stop() throws SiteWhereException {
		super.stop();
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
	 * (non-Javadoc)
	 * 
	 * @see com.sitewhere.device.event.processor.FilteredOutboundEventProcessor#
	 * onMeasurementsNotFiltered(com.sitewhere.spi.device.event.IDeviceMeasurements)
	 */
	@Override
	public void onMeasurementsNotFiltered(IDeviceMeasurements measurements) throws SiteWhereException {
		sendEvent(measurements);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.sitewhere.device.event.processor.FilteredOutboundEventProcessor#
	 * onLocationNotFiltered(com.sitewhere.spi.device.event.IDeviceLocation)
	 */
	@Override
	public void onLocationNotFiltered(IDeviceLocation location) throws SiteWhereException {
		sendEvent(location);
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
		sendEvent(alert);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.sitewhere.device.event.processor.FilteredOutboundEventProcessor#
	 * onCommandInvocationNotFiltered
	 * (com.sitewhere.spi.device.event.IDeviceCommandInvocation)
	 */
	@Override
	public void onCommandInvocationNotFiltered(IDeviceCommandInvocation invocation) throws SiteWhereException {
		sendEvent(invocation);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.sitewhere.device.event.processor.FilteredOutboundEventProcessor#
	 * onCommandResponseNotFiltered(com.sitewhere.spi.device.event.IDeviceCommandResponse)
	 */
	@Override
	public void onCommandResponseNotFiltered(IDeviceCommandResponse response) throws SiteWhereException {
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