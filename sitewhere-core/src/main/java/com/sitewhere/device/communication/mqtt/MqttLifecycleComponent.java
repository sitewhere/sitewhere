/*
 * Copyright (c) SiteWhere, LLC. All rights reserved. http://www.sitewhere.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package com.sitewhere.device.communication.mqtt;

import java.io.File;
import java.io.FileInputStream;
import java.net.URISyntaxException;
import java.security.KeyStore;
import java.util.concurrent.TimeUnit;

import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManagerFactory;

import org.apache.log4j.Logger;
import org.fusesource.hawtdispatch.Dispatch;
import org.fusesource.hawtdispatch.DispatchQueue;
import org.fusesource.mqtt.client.Future;
import org.fusesource.mqtt.client.FutureConnection;
import org.fusesource.mqtt.client.MQTT;

import com.sitewhere.server.lifecycle.TenantLifecycleComponent;
import com.sitewhere.spi.SiteWhereException;
import com.sitewhere.spi.server.lifecycle.LifecycleComponentType;

/**
 * Extends {@link TenantLifecycleComponent} with base functionality for connecting to
 * MQTT.
 * 
 * @author Derek
 */
public class MqttLifecycleComponent extends TenantLifecycleComponent implements IMqttComponent {

	/** Static logger instance */
	private static Logger LOGGER = Logger.getLogger(MqttLifecycleComponent.class);

	/** Default protocol if not set via Spring */
	public static final String DEFAULT_PROTOCOL = "tcp";

	/** Default hostname if not set via Spring */
	public static final String DEFAULT_HOSTNAME = "localhost";

	/** Default port if not set from Spring */
	public static final int DEFAULT_PORT = 1883;

	/** Default connection timeout in seconds */
	public static final long DEFAULT_CONNECT_TIMEOUT_SECS = 5;

	private String protocol = DEFAULT_PROTOCOL;

	/** Host name */
	private String hostname = DEFAULT_HOSTNAME;

	/** Port */
	private int port = DEFAULT_PORT;

	/** TrustStore path */
	private String trustStorePath;

	/** TrustStore password */
	private String trustStorePassword;

	/** MQTT client */
	private MQTT mqtt;

	/** Hawtdispatch queue */
	private DispatchQueue queue;

	public MqttLifecycleComponent(LifecycleComponentType type) {
		super(type);
	}

	/**
	 * Gets information about the broker.
	 * 
	 * @return
	 * @throws SiteWhereException
	 */
	public String getBrokerInfo() throws SiteWhereException {
		return mqtt.getHost().toString();
	}

	/**
	 * Get a {@link FutureConnection} to the MQTT broker.
	 * 
	 * @return
	 * @throws SiteWhereException
	 */
	public FutureConnection getConnection() throws SiteWhereException {
		FutureConnection connection = mqtt.futureConnection();
		try {
			Future<Void> future = connection.connect();
			future.await(DEFAULT_CONNECT_TIMEOUT_SECS, TimeUnit.SECONDS);
			return connection;
		} catch (Exception e) {
			throw new SiteWhereException("Unable to connect to MQTT broker.", e);
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.sitewhere.spi.server.lifecycle.ILifecycleComponent#start()
	 */
	@Override
	public void start() throws SiteWhereException {
		this.queue = Dispatch.createQueue(getComponentId());
		this.mqtt = MqttLifecycleComponent.configure(this, queue);
	}

	/**
	 * Configures MQTT parameters based on component settings.
	 * 
	 * @param component
	 * @return
	 * @throws SiteWhereException
	 */
	public static MQTT configure(IMqttComponent component, DispatchQueue queue) throws SiteWhereException {
		MQTT mqtt = new MQTT();
		if ((component.getProtocol().startsWith("ssl")) || (component.getProtocol().startsWith("tls"))) {
			if ((component.getTrustStorePath() != null) && (component.getTrustStorePassword() != null)) {
				try {
					SSLContext sslContext = SSLContext.getInstance("TLS");
					TrustManagerFactory tmf =
							TrustManagerFactory.getInstance(TrustManagerFactory.getDefaultAlgorithm());
					KeyStore ks = KeyStore.getInstance("JKS");
					File trustFile = new File(component.getTrustStorePath());
					ks.load(new FileInputStream(trustFile), component.getTrustStorePassword().toCharArray());
					tmf.init(ks);
					sslContext.init(null, tmf.getTrustManagers(), null);
					mqtt.setSslContext(sslContext);
					LOGGER.info("Created SSL context for MQTT receiver.");
				} catch (Exception e) {
					throw new SiteWhereException("Unable to load SSL context.", e);
				}
			}
		}
		try {
			mqtt.setHost(
					component.getProtocol() + "://" + component.getHostname() + ":" + component.getPort());
			return mqtt;
		} catch (URISyntaxException e) {
			throw new SiteWhereException("Invalid hostname for MQTT server.", e);
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.sitewhere.spi.server.lifecycle.ILifecycleComponent#stop()
	 */
	@Override
	public void stop() throws SiteWhereException {
		if (queue != null) {
			queue.suspend();
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

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.sitewhere.device.communication.mqtt.IMqttComponent#getProtocol()
	 */
	public String getProtocol() {
		return protocol;
	}

	public void setProtocol(String protocol) {
		this.protocol = protocol;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.sitewhere.device.communication.mqtt.IMqttComponent#getHostname()
	 */
	public String getHostname() {
		return hostname;
	}

	public void setHostname(String hostname) {
		this.hostname = hostname;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.sitewhere.device.communication.mqtt.IMqttComponent#getPort()
	 */
	public int getPort() {
		return port;
	}

	public void setPort(int port) {
		this.port = port;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.sitewhere.device.communication.mqtt.IMqttComponent#getTrustStorePath()
	 */
	public String getTrustStorePath() {
		return trustStorePath;
	}

	public void setTrustStorePath(String trustStorePath) {
		this.trustStorePath = trustStorePath;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.sitewhere.device.communication.mqtt.IMqttComponent#getTrustStorePassword()
	 */
	public String getTrustStorePassword() {
		return trustStorePassword;
	}

	public void setTrustStorePassword(String trustStorePassword) {
		this.trustStorePassword = trustStorePassword;
	}
}