/*
 * Copyright (c) SiteWhere, LLC. All rights reserved. http://www.sitewhere.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package com.sitewhere.commands.configuration.destinations.mqtt;

import com.fasterxml.jackson.databind.JsonNode;
import com.sitewhere.commands.configuration.destinations.CommandDestinationConfiguration;
import com.sitewhere.communication.mqtt.IMqttConfiguration;
import com.sitewhere.spi.SiteWhereException;
import com.sitewhere.spi.microservice.lifecycle.ITenantEngineLifecycleComponent;

/**
 * Configuration for an MQTT command destination.
 */
public class MqttConfiguration extends CommandDestinationConfiguration implements IMqttConfiguration {

    /** Communication protocol */
    private String protocol;

    /** Broker hostname */
    private String hostname;

    /** Broker port */
    private int port;

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

    /** Clean session flag */
    private boolean cleanSession = true;

    public MqttConfiguration(ITenantEngineLifecycleComponent component) {
	super(component);
    }

    /*
     * @see
     * com.sitewhere.sources.configuration.eventsource.EventSourceConfiguration#
     * loadFrom(com.fasterxml.jackson.databind.JsonNode)
     */
    @Override
    public void loadFrom(JsonNode json) throws SiteWhereException {
	this.protocol = configurableString("protocol", json, IMqttConfiguration.DEFAULT_PROTOCOL);
	this.hostname = configurableString("hostname", json, IMqttConfiguration.DEFAULT_HOSTNAME);
	this.port = configurableInt("port", json, IMqttConfiguration.DEFAULT_PORT);
	this.trustStorePath = configurableString("qos", json, null);
	this.trustStorePassword = configurableString("trustStorePassword", json, null);
	this.keyStorePath = configurableString("keyStorePath", json, null);
	this.keyStorePassword = configurableString("keyStorePassword", json, null);
	this.username = configurableString("username", json, null);
	this.password = configurableString("password", json, null);
	this.clientId = configurableString("clientId", json, null);
	this.cleanSession = configurableBoolean("cleanSession", json, true);
    }

    /*
     * @see com.sitewhere.communication.mqtt.IMqttConfiguration#getProtocol()
     */
    @Override
    public String getProtocol() {
	return protocol;
    }

    public void setProtocol(String protocol) {
	this.protocol = protocol;
    }

    /*
     * @see com.sitewhere.communication.mqtt.IMqttConfiguration#getHostname()
     */
    @Override
    public String getHostname() {
	return hostname;
    }

    public void setHostname(String hostname) {
	this.hostname = hostname;
    }

    /*
     * @see com.sitewhere.communication.mqtt.IMqttConfiguration#getPort()
     */
    @Override
    public int getPort() {
	return port;
    }

    public void setPort(int port) {
	this.port = port;
    }

    /*
     * @see com.sitewhere.communication.mqtt.IMqttConfiguration#getTrustStorePath()
     */
    @Override
    public String getTrustStorePath() {
	return trustStorePath;
    }

    public void setTrustStorePath(String trustStorePath) {
	this.trustStorePath = trustStorePath;
    }

    /*
     * @see
     * com.sitewhere.communication.mqtt.IMqttConfiguration#getTrustStorePassword()
     */
    @Override
    public String getTrustStorePassword() {
	return trustStorePassword;
    }

    public void setTrustStorePassword(String trustStorePassword) {
	this.trustStorePassword = trustStorePassword;
    }

    /*
     * @see com.sitewhere.communication.mqtt.IMqttConfiguration#getKeyStorePath()
     */
    @Override
    public String getKeyStorePath() {
	return keyStorePath;
    }

    public void setKeyStorePath(String keyStorePath) {
	this.keyStorePath = keyStorePath;
    }

    /*
     * @see
     * com.sitewhere.communication.mqtt.IMqttConfiguration#getKeyStorePassword()
     */
    @Override
    public String getKeyStorePassword() {
	return keyStorePassword;
    }

    public void setKeyStorePassword(String keyStorePassword) {
	this.keyStorePassword = keyStorePassword;
    }

    /*
     * @see com.sitewhere.communication.mqtt.IMqttConfiguration#getUsername()
     */
    @Override
    public String getUsername() {
	return username;
    }

    public void setUsername(String username) {
	this.username = username;
    }

    /*
     * @see com.sitewhere.communication.mqtt.IMqttConfiguration#getPassword()
     */
    @Override
    public String getPassword() {
	return password;
    }

    public void setPassword(String password) {
	this.password = password;
    }

    /*
     * @see com.sitewhere.communication.mqtt.IMqttConfiguration#getClientId()
     */
    @Override
    public String getClientId() {
	return clientId;
    }

    public void setClientId(String clientId) {
	this.clientId = clientId;
    }

    /*
     * @see com.sitewhere.communication.mqtt.IMqttConfiguration#isCleanSession()
     */
    @Override
    public boolean isCleanSession() {
	return cleanSession;
    }

    public void setCleanSession(boolean cleanSession) {
	this.cleanSession = cleanSession;
    }
}
