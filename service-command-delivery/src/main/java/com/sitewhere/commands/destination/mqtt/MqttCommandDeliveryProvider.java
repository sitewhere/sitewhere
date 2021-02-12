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

import java.util.List;

import org.fusesource.hawtdispatch.ShutdownException;
import org.fusesource.mqtt.client.FutureConnection;
import org.fusesource.mqtt.client.QoS;

import com.sitewhere.commands.spi.ICommandDeliveryProvider;
import com.sitewhere.communication.mqtt.IMqttConfiguration;
import com.sitewhere.communication.mqtt.MqttLifecycleComponent;
import com.sitewhere.spi.SiteWhereException;
import com.sitewhere.spi.device.IDeviceAssignment;
import com.sitewhere.spi.device.IDeviceNestingContext;
import com.sitewhere.spi.device.command.IDeviceCommandExecution;
import com.sitewhere.spi.microservice.lifecycle.ILifecycleProgressMonitor;
import com.sitewhere.spi.microservice.lifecycle.LifecycleComponentType;

/**
 * Implementation of {@link ICommandDeliveryProvider} that publishes commands to
 * an MQTT topic so that they can be processed asynchronously by a device
 * listening on the topic.
 */
public class MqttCommandDeliveryProvider extends MqttLifecycleComponent
	implements ICommandDeliveryProvider<byte[], MqttParameters> {

    /** Shared MQTT connection */
    private FutureConnection connection;

    public MqttCommandDeliveryProvider(IMqttConfiguration configuration) {
	super(LifecycleComponentType.CommandDeliveryProvider, configuration);
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * com.sitewhere.device.communication.mqtt.MqttLifecycleComponent#start(com.
     * sitewhere.spi.server.lifecycle.ILifecycleProgressMonitor)
     */
    @Override
    public void start(ILifecycleProgressMonitor monitor) throws SiteWhereException {
	super.start(monitor);

	getLogger().info("Connecting to MQTT broker at '" + getConfiguration().getHostname() + ":"
		+ getConfiguration().getPort() + "'...");
	connection = getConnection();
	getLogger().info("Connected to MQTT broker.");
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.sitewhere.device.communication.mqtt.MqttLifecycleComponent#stop(com.
     * sitewhere.spi.server.lifecycle.ILifecycleProgressMonitor)
     */
    @Override
    public void stop(ILifecycleProgressMonitor monitor) throws SiteWhereException {
	if (connection != null) {
	    try {
		connection.disconnect().await();
		connection.kill().await();
	    } catch (ShutdownException e) {
		getLogger().info("Dispatcher has already been shut down.");
	    } catch (Exception e) {
		getLogger().error("Error shutting down MQTT device event receiver.", e);
	    }
	}
	super.stop(monitor);
    }

    /*
     * @see
     * com.sitewhere.commands.spi.ICommandDeliveryProvider#deliver(com.sitewhere.spi
     * .device.IDeviceNestingContext, java.util.List,
     * com.sitewhere.spi.device.command.IDeviceCommandExecution, java.lang.Object,
     * java.lang.Object)
     */
    @Override
    public void deliver(IDeviceNestingContext nested, List<? extends IDeviceAssignment> assignments,
	    IDeviceCommandExecution execution, byte[] encoded, MqttParameters params) throws SiteWhereException {
	try {
	    getLogger().info("About to publish command message to topic: " + params.getCommandTopic());
	    connection.publish(params.getCommandTopic(), encoded, QoS.AT_LEAST_ONCE, false);
	    getLogger().info("Command published.");
	} catch (Exception e) {
	    throw new SiteWhereException("Unable to publish command to MQTT topic.", e);
	}
    }

    /*
     * @see
     * com.sitewhere.commands.spi.ICommandDeliveryProvider#deliverSystemCommand(com.
     * sitewhere.spi.device.IDeviceNestingContext, java.util.List, java.lang.Object,
     * java.lang.Object)
     */
    @Override
    public void deliverSystemCommand(IDeviceNestingContext nested, List<? extends IDeviceAssignment> assignments,
	    byte[] encoded, MqttParameters params) throws SiteWhereException {
	try {
	    getLogger().info("About to publish system message to topic: " + params.getSystemTopic());
	    connection.publish(params.getSystemTopic(), encoded, QoS.AT_LEAST_ONCE, false);
	    getLogger().info("Command published.");
	} catch (Exception e) {
	    throw new SiteWhereException("Unable to publish command to MQTT topic.", e);
	}
    }
}