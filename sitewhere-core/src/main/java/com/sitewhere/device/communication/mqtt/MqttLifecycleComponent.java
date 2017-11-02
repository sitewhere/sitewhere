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
import java.security.KeyStore;

import javax.net.ssl.KeyManagerFactory;
import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManagerFactory;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttSecurityException;
import org.springframework.util.StringUtils;

import com.sitewhere.server.lifecycle.TenantLifecycleComponent;
import com.sitewhere.spi.SiteWhereException;
import com.sitewhere.spi.server.lifecycle.ILifecycleProgressMonitor;
import com.sitewhere.spi.server.lifecycle.LifecycleComponentType;

/**
 * Extends {@link TenantLifecycleComponent} with base functionality for
 * connecting to MQTT.
 * 
 * @author Derek
 */
public class MqttLifecycleComponent extends TenantLifecycleComponent implements IMqttComponent {

    /** Static logger instance */
    private static Logger LOGGER = LogManager.getLogger();

    /** Default protocol if not set via Spring */
    public static final String DEFAULT_PROTOCOL = "tcp";

    /** Default hostname if not set via Spring */
    public static final String DEFAULT_HOSTNAME = "localhost";

    /** Default port if not set from Spring */
    public static final String DEFAULT_PORT = "1883";

    /** Default connection timeout in seconds */
    public static final long DEFAULT_CONNECT_TIMEOUT_SECS = 5;

    private String protocol = DEFAULT_PROTOCOL;

    /** Host name */
    private String hostname = DEFAULT_HOSTNAME;

    /** Port */
    private String port = DEFAULT_PORT;

    /** TrustStore path */
    private String trustStorePath;

    /** TrustStore password */
    private String trustStorePassword;

    /** KeyStore path */
    private String keyStorePath;

    /** KeyStore password */
    private String keyStorePassword;

    /** Username */
    private String username;

    /** Password */
    private String password;

    /** Client id */
    private String clientId;

    /** Quality of service */
    private int qos = 0;

    /** Clean session flag */
    private boolean cleanSession = true;

    /** MQTT client */
    private MqttClient mqttClient;

    public MqttLifecycleComponent(LifecycleComponentType type) {
	super(type);
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * com.sitewhere.server.lifecycle.LifecycleComponent#start(com.sitewhere.spi
     * .server.lifecycle.ILifecycleProgressMonitor)
     */
    @Override
    public void start(ILifecycleProgressMonitor monitor) throws SiteWhereException {
	this.mqttClient = MqttLifecycleComponent.connect(this);
    }

    /**
     * Configure trust store.
     * 
     * @param sslContext
     * @param trustStorePath
     * @param trustStorePassword
     * @throws Exception
     */
    protected static TrustManagerFactory configureTrustStore(SSLContext sslContext, String trustStorePath,
	    String trustStorePassword) throws Exception {
	LOGGER.info("MQTT client using truststore path: " + trustStorePath);
	TrustManagerFactory tmf = TrustManagerFactory.getInstance(TrustManagerFactory.getDefaultAlgorithm());
	KeyStore tks = KeyStore.getInstance("JKS");
	File trustFile = new File(trustStorePath);
	tks.load(new FileInputStream(trustFile), trustStorePassword.toCharArray());
	tmf.init(tks);
	return tmf;
    }

    protected static KeyManagerFactory configureKeyStore(SSLContext sslContext, String keyStorePath,
	    String keyStorePassword) throws Exception {
	LOGGER.info("MQTT client using keystore path: " + keyStorePath);
	KeyManagerFactory kmf = KeyManagerFactory.getInstance(KeyManagerFactory.getDefaultAlgorithm());
	KeyStore ks = KeyStore.getInstance("JKS");
	File keyFile = new File(keyStorePath);
	ks.load(new FileInputStream(keyFile), keyStorePassword.toCharArray());
	kmf.init(ks, keyStorePassword.toCharArray());
	return kmf;
    }

    /**
     * Handle configuration of secure transport.
     * 
     * @param component
     * @return
     * @throws SiteWhereException
     */
    protected static SSLContext handleSecureTransport(IMqttComponent component) throws SiteWhereException {
	LOGGER.info("MQTT client using secure protocol '" + component.getProtocol() + "'.");
	boolean trustStoreConfigured = (component.getTrustStorePath() != null)
		&& (component.getTrustStorePassword() != null);
	boolean keyStoreConfigured = (component.getKeyStorePath() != null) && (component.getKeyStorePassword() != null);
	try {
	    SSLContext sslContext = SSLContext.getInstance("TLS");
	    TrustManagerFactory tmf = null;
	    KeyManagerFactory kmf;

	    // Handle trust store configuration.
	    if (trustStoreConfigured) {
		tmf = configureTrustStore(sslContext, component.getTrustStorePath(), component.getTrustStorePassword());
	    } else {
		LOGGER.info("No trust store configured for MQTT client.");
	    }

	    // Handle key store configuration.
	    if (keyStoreConfigured) {
		kmf = configureKeyStore(sslContext, component.getKeyStorePath(), component.getKeyStorePassword());
		sslContext.init(kmf.getKeyManagers(), tmf != null ? tmf.getTrustManagers() : null, null);
	    } else if (trustStoreConfigured) {
		sslContext.init(null, tmf != null ? tmf.getTrustManagers() : null, null);
	    }

	    LOGGER.info("Created SSL context for MQTT component.");
	    return sslContext;
	} catch (Throwable t) {
	    throw new SiteWhereException("Unable to configure secure transport.", t);
	}
    }

    /**
     * Configures MQTT parameters based on component settings and connects to
     * broker.
     * 
     * @param component
     * @return
     * @throws SiteWhereException
     */
    public static MqttClient connect(IMqttComponent component) throws SiteWhereException {
	try {
	    String clientId = (component.getClientId() != null) ? component.getClientId()
		    : MqttClient.generateClientId();
	    LOGGER.info("MQTT connection using client id '" + clientId + "'.");

	    // Detect secure transports.
	    boolean usingSSL = component.getProtocol().startsWith("ssl");
	    boolean usingTLS = component.getProtocol().startsWith("tls");
	    String protocol = (usingSSL || usingTLS) ? "ssl" : "tcp";
	    String serverUri = protocol + "://" + component.getHostname() + ":" + component.getPort();

	    MqttClient client = new MqttClient(serverUri, clientId);
	    MqttConnectOptions options = new MqttConnectOptions();
	    options.setAutomaticReconnect(true);
	    options.setMaxInflight(100);

	    // Set flag for clean session.
	    options.setCleanSession(component.isCleanSession());
	    LOGGER.info("MQTT clean session flag being set to '" + component.isCleanSession() + "'.");

	    // Handle secure transports.
	    if (usingSSL || usingTLS) {
		SSLContext sslContext = handleSecureTransport(component);
		options.setSocketFactory(sslContext.getSocketFactory());
	    }

	    // Set username if provided.
	    if (!StringUtils.isEmpty(component.getUsername())) {
		options.setUserName(component.getUsername());
	    }

	    // Set password if provided.
	    if (!StringUtils.isEmpty(component.getPassword())) {
		options.setPassword(component.getPassword().toCharArray());
	    }

	    client.connect(options);
	    return client;
	} catch (MqttSecurityException e) {
	    throw new SiteWhereException("Security check for MQTT connection failed.", e);
	} catch (MqttException e) {
	    throw new SiteWhereException("Error in MQTT connection.", e);
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
    @Override
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
    @Override
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
    @Override
    public String getPort() {
	return port;
    }

    public void setPort(String port) {
	this.port = port;
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.sitewhere.device.communication.mqtt.IMqttComponent#getUsername()
     */
    @Override
    public String getUsername() {
	return username;
    }

    public void setUsername(String username) {
	this.username = username;
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.sitewhere.device.communication.mqtt.IMqttComponent#getPassword()
     */
    @Override
    public String getPassword() {
	return password;
    }

    public void setPassword(String password) {
	this.password = password;
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * com.sitewhere.device.communication.mqtt.IMqttComponent#getTrustStorePath(
     * )
     */
    @Override
    public String getTrustStorePath() {
	return trustStorePath;
    }

    public void setTrustStorePath(String trustStorePath) {
	this.trustStorePath = trustStorePath;
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.sitewhere.device.communication.mqtt.IMqttComponent#
     * getTrustStorePassword()
     */
    @Override
    public String getTrustStorePassword() {
	return trustStorePassword;
    }

    public void setTrustStorePassword(String trustStorePassword) {
	this.trustStorePassword = trustStorePassword;
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * com.sitewhere.device.communication.mqtt.IMqttComponent#getKeyStorePath()
     */
    @Override
    public String getKeyStorePath() {
	return keyStorePath;
    }

    public void setKeyStorePath(String keyStorePath) {
	this.keyStorePath = keyStorePath;
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.sitewhere.device.communication.mqtt.IMqttComponent#
     * getKeyStorePassword()
     */
    @Override
    public String getKeyStorePassword() {
	return keyStorePassword;
    }

    public void setKeyStorePassword(String keyStorePassword) {
	this.keyStorePassword = keyStorePassword;
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.sitewhere.device.communication.mqtt.IMqttComponent#getClientId()
     */
    @Override
    public String getClientId() {
	return clientId;
    }

    public void setClientId(String clientId) {
	this.clientId = clientId;
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * com.sitewhere.device.communication.mqtt.IMqttComponent#isCleanSession()
     */
    @Override
    public boolean isCleanSession() {
	return cleanSession;
    }

    public void setCleanSession(boolean cleanSession) {
	this.cleanSession = cleanSession;
    }

    /*
     * @see com.sitewhere.device.communication.mqtt.IMqttComponent#getQos()
     */
    @Override
    public int getQos() {
	return qos;
    }

    public void setQos(String label) {
	this.qos = QoS.getValueFor(label);
	LOGGER.info("Using QoS of " + this.qos);
    }

    public MqttClient getMqttClient() {
	return mqttClient;
    }

    public void setMqttClient(MqttClient mqttClient) {
	this.mqttClient = mqttClient;
    }
}