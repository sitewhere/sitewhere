/*
 * Copyright (c) SiteWhere, LLC. All rights reserved. http://www.sitewhere.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package com.sitewhere.sources.azure;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.net.URLEncoder;
import java.util.Hashtable;

import javax.jms.BytesMessage;
import javax.jms.Connection;
import javax.jms.ConnectionFactory;
import javax.jms.Destination;
import javax.jms.MessageProducer;
import javax.jms.Session;
import javax.naming.Context;
import javax.naming.InitialContext;

/**
 * Tests for EventHub messaging.
 */
public class EventHubTest {

    // @Test
    @SuppressWarnings("deprecation")
    public void sendEvent() throws Exception {
	String key = URLEncoder.encode("SAS_KEY_GOES_HERE");
	String connectionString = "amqps://user:" + key + "@sitewhere.servicebus.windows.net";
	File file = File.createTempFile("qpid", "props");
	BufferedWriter writer = new BufferedWriter(new FileWriter(file));
	writer.write("connectionfactory.SBCF = " + connectionString);
	writer.newLine();
	writer.write("queue.EVENTHUB = sitewhere");
	writer.newLine();
	writer.close();

	Hashtable<String, String> env = new Hashtable<String, String>();
	env.put(Context.INITIAL_CONTEXT_FACTORY,
		"org.apache.qpid.amqp_1_0.jms.jndi.PropertiesFileInitialContextFactory");
	env.put(Context.PROVIDER_URL, file.getAbsolutePath());
	Context context = new InitialContext(env);

	// Lookup ConnectionFactory and Queue
	ConnectionFactory cf = (ConnectionFactory) context.lookup("SBCF");
	Destination eventhub = (Destination) context.lookup("EVENTHUB");

	// Create Connection
	Connection connection = cf.createConnection();

	// Create sender-side Session and MessageProducer
	Session session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);
	MessageProducer sender = session.createProducer(eventhub);

	for (int i = 0; i < 100; i++) {
	    String messageId = "ID:bubba" + i;
	    BytesMessage message = session.createBytesMessage();
	    byte[] body = "Test".getBytes();
	    message.writeBytes(body);
	    message.setJMSMessageID(messageId);
	    sender.send(message);
	    System.out.println("Sending " + messageId);
	}

	sender.close();
	session.close();
	connection.close();
    }
}