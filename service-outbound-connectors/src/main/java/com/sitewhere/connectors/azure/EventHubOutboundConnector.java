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
package com.sitewhere.connectors.azure;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.net.URLEncoder;
import java.util.Hashtable;

import javax.naming.Context;

import com.sitewhere.connectors.SerialOutboundConnector;
import com.sitewhere.connectors.spi.IOutboundConnector;
import com.sitewhere.spi.SiteWhereException;
import com.sitewhere.spi.device.event.IDeviceAlert;
import com.sitewhere.spi.device.event.IDeviceCommandInvocation;
import com.sitewhere.spi.device.event.IDeviceCommandResponse;
import com.sitewhere.spi.device.event.IDeviceEvent;
import com.sitewhere.spi.device.event.IDeviceEventContext;
import com.sitewhere.spi.device.event.IDeviceLocation;
import com.sitewhere.spi.device.event.IDeviceMeasurement;
import com.sitewhere.spi.microservice.lifecycle.ILifecycleProgressMonitor;

/**
 * Implementation of {@link IOutboundConnector} that sends events to an EventHub
 * running on Azure.
 */
public class EventHubOutboundConnector extends SerialOutboundConnector {

    /** SAS identity name */
    private String sasName;

    /** SAS key */
    private String sasKey;

    /** Service bus name */
    private String serviceBusName;

    /** Event hub name */
    private String eventHubName;

    /** JMS objects */
    // private ConnectionFactory factory;
    // private Destination destination;
    // private Connection connection;
    // private Session session;
    // private MessageProducer sender;

    /*
     * @see
     * com.sitewhere.connectors.FilteredOutboundConnector#start(com.sitewhere.spi.
     * microservice.lifecycle.ILifecycleProgressMonitor)
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
	    // Context context = new InitialContext(env);

	    // this.factory = (ConnectionFactory) context.lookup("SBCF");
	    // this.destination = (Destination) context.lookup("EVENTHUB");
	    // this.connection = factory.createConnection();
	    // this.session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);
	    // this.sender = session.createProducer(destination);
	} catch (IOException e) {
	    throw new SiteWhereException(e);
	}
	// catch (NamingException e) {
	// throw new SiteWhereException(e);
	// }
	// catch (JMSException e) {
	// throw new SiteWhereException(e);
	// }
    }

    /*
     * @see
     * com.sitewhere.connectors.FilteredOutboundConnector#stop(com.sitewhere.spi.
     * microservice.lifecycle.ILifecycleProgressMonitor)
     */
    @Override
    public void stop(ILifecycleProgressMonitor monitor) throws SiteWhereException {
	super.stop(monitor);
	// if (sender != null) {
	// try {
	// sender.close();
	// } catch (JMSException e) {
	// getLogger().warn("Error closing message source for EventHub processor.", e);
	// }
	// }
	// if (session != null) {
	// try {
	// session.close();
	// } catch (JMSException e) {
	// getLogger().warn("Error closing session for EventHub processor.", e);
	// }
	// }
	// if (connection != null) {
	// try {
	// connection.close();
	// } catch (JMSException e) {
	// getLogger().warn("Error closing session for EventHub processor.", e);
	// }
	// }
    }

    /*
     * @see
     * com.sitewhere.connectors.SerialOutboundConnector#onMeasurement(com.sitewhere.
     * spi.device.event.IDeviceEventContext,
     * com.sitewhere.spi.device.event.IDeviceMeasurement)
     */
    @Override
    public void onMeasurement(IDeviceEventContext context, IDeviceMeasurement mx) throws SiteWhereException {
	sendEvent(mx);
    }

    /*
     * @see
     * com.sitewhere.connectors.SerialOutboundConnector#onLocation(com.sitewhere.spi
     * .device.event.IDeviceEventContext,
     * com.sitewhere.spi.device.event.IDeviceLocation)
     */
    @Override
    public void onLocation(IDeviceEventContext context, IDeviceLocation location) throws SiteWhereException {
	sendEvent(location);
    }

    /*
     * @see
     * com.sitewhere.connectors.SerialOutboundConnector#onAlert(com.sitewhere.spi.
     * device.event.IDeviceEventContext,
     * com.sitewhere.spi.device.event.IDeviceAlert)
     */
    @Override
    public void onAlert(IDeviceEventContext context, IDeviceAlert alert) throws SiteWhereException {
	sendEvent(alert);
    }

    /*
     * @see
     * com.sitewhere.connectors.SerialOutboundConnector#onCommandInvocation(com.
     * sitewhere.spi.device.event.IDeviceEventContext,
     * com.sitewhere.spi.device.event.IDeviceCommandInvocation)
     */
    @Override
    public void onCommandInvocation(IDeviceEventContext context, IDeviceCommandInvocation invocation)
	    throws SiteWhereException {
	sendEvent(invocation);
    }

    /*
     * @see com.sitewhere.connectors.SerialOutboundConnector#onCommandResponse(com.
     * sitewhere.spi.device.event.IDeviceEventContext,
     * com.sitewhere.spi.device.event.IDeviceCommandResponse)
     */
    @Override
    public void onCommandResponse(IDeviceEventContext context, IDeviceCommandResponse response)
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
	// try {
	// BytesMessage message = session.createBytesMessage();
	// message.writeBytes(MarshalUtils.marshalJson(event));
	// message.setJMSMessageID("ID:" + event.getId());
	// sender.send(message);
	// } catch (JMSException e) {
	// throw new SiteWhereException(e);
	// }
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