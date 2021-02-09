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
package com.sitewhere.connectors.configuration.connector;

import com.fasterxml.jackson.databind.JsonNode;
import com.sitewhere.communication.mqtt.IMqttConfiguration;
import com.sitewhere.spi.SiteWhereException;
import com.sitewhere.spi.microservice.lifecycle.ITenantEngineLifecycleComponent;

/**
 * Configuration for an MQTT outbound connector.
 */
public class MqttOutboundConnectorConfiguration extends OutboundConnectorConfiguration implements IMqttConfiguration {

    /** Default protocol for broker connection */
    public static final String DEFAULT_PROTOCOL = "tcp";

    /** Default hostname for broker connection */
    public static final String DEFAULT_HOSTNAME = "mqtt";

    /** Default port for broker connection */
    public static final int DEFAULT_PORT = 1883;

    /** Default outbound topic */
    public static final String DEFAULT_OUTBOUND_TOPIC = "SiteWhere/${tenant.token}/output/${connector.id}";

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

    /** Outbound topic */
    private String outboundTopic;

    public MqttOutboundConnectorConfiguration(ITenantEngineLifecycleComponent component) {
	super(component);
    }

    /*
     * @see com.sitewhere.connectors.configuration.connector.
     * OutboundConnectorConfiguration#loadFrom(com.fasterxml.jackson.databind.
     * JsonNode)
     */
    @Override
    public void loadFrom(JsonNode json) throws SiteWhereException {
	this.protocol = configurableString("protocol", json, DEFAULT_PROTOCOL);
	this.hostname = configurableString("hostname", json, DEFAULT_HOSTNAME);
	this.port = configurableInt("port", json, DEFAULT_PORT);
	this.trustStorePath = configurableString("trustStorePath", json, null);
	this.trustStorePassword = configurableString("trustStorePassword", json, null);
	this.keyStorePath = configurableString("keyStorePath", json, null);
	this.keyStorePassword = configurableString("keyStorePassword", json, null);
	this.username = configurableString("username", json, null);
	this.password = configurableString("password", json, null);
	this.clientId = configurableString("clientId", json, null);
	this.cleanSession = configurableBoolean("cleanSession", json, true);
	this.outboundTopic = configurableString("outboundTopic", json, DEFAULT_OUTBOUND_TOPIC);
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

    public String getOutboundTopic() {
	return outboundTopic;
    }

    public void setOutboundTopic(String outboundTopic) {
	this.outboundTopic = outboundTopic;
    }
}
