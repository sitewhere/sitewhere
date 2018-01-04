/*
 * Copyright (c) SiteWhere, LLC. All rights reserved. http://www.sitewhere.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package com.sitewhere.commands.mqtt;

/**
 * Parameters that are needed for interacting with MQTT.
 * 
 * @author Derek
 */
public class MqttParameters {

    /** MQTT topic for sending commands */
    private String commandTopic;

    /** MQTT topic for sending system messages */
    private String systemTopic;

    public String getCommandTopic() {
	return commandTopic;
    }

    public void setCommandTopic(String commandTopic) {
	this.commandTopic = commandTopic;
    }

    public String getSystemTopic() {
	return systemTopic;
    }

    public void setSystemTopic(String systemTopic) {
	this.systemTopic = systemTopic;
    }
}