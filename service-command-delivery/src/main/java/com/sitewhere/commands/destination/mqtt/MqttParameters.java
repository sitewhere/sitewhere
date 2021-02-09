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
package com.sitewhere.commands.destination.mqtt;

/**
 * Parameters that are needed for interacting with MQTT.
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