/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.sitewhere.messagebroker;

import com.sitewhere.common.MarshalUtils;
import com.sitewhere.device.event.processor.OutboundEventProcessor;
import com.sitewhere.spi.SiteWhereException;
import com.sitewhere.spi.device.event.IDeviceAlert;
import com.sitewhere.spi.device.event.IDeviceLocation;
import com.sitewhere.spi.device.event.IDeviceMeasurements;
import java.net.URISyntaxException;
import java.util.concurrent.TimeUnit;
import org.apache.log4j.Logger;
import org.fusesource.hawtdispatch.internal.DispatcherConfig;
import org.fusesource.mqtt.client.Future;
import org.fusesource.mqtt.client.FutureConnection;
import org.fusesource.mqtt.client.MQTT;
import org.fusesource.mqtt.client.QoS;

/**
 *
 * @author CBICK
 */
public class BrokerOutboundEventProcessor extends OutboundEventProcessor {

    /**
     * Static logger instance
     */
    private static final Logger LOGGER = Logger.getLogger(BrokerOutboundEventProcessor.class);

    /**
     * Default hostname if not set via Spring
     */
    public static final String DEFAULT_HOSTNAME = "localhost";

    /**
     * Default port if not set from Spring
     */
    public static final int DEFAULT_PORT = 1883;

    /**
     * Default connection timeout in seconds
     */
    public static final long DEFAULT_CONNECT_TIMEOUT_SECS = 5;

    /**
     * Host name
     */
    private String hostname = DEFAULT_HOSTNAME;

    /**
     * Port
     */
    private int port = DEFAULT_PORT;

    /**
     * MQTT client
     */
    private MQTT mqtt;

    /**
     * Shared MQTT connection
     */
    private FutureConnection connection;

    @Override
    public void start() throws SiteWhereException {
        this.mqtt = new MQTT();
        try {
            mqtt.setHost(getHostname(), getPort());
        } catch (URISyntaxException e) {
            throw new SiteWhereException("Invalid hostname for MQTT server.", e);
        }
        LOGGER.info("Outbound connecting to MQTT broker at '" + getHostname() + ":" + getPort() + "'...");
        connection = mqtt.futureConnection();
        try {
            Future<Void> future = connection.connect();
            future.await(DEFAULT_CONNECT_TIMEOUT_SECS, TimeUnit.SECONDS);
        } catch (Exception e) {
            throw new SiteWhereException("Unable to connect to MQTT broker.", e);
        }
        LOGGER.info("Outbound connected to MQTT broker.");
    }

    @Override
    public void stop() throws SiteWhereException {
        if (connection != null) {
            try {
                connection.disconnect();
                connection.kill();
            } catch (Exception e) {
                LOGGER.error("Error shutting down MQTT device event Outbound.", e);
            }
        }
        DispatcherConfig.getDefaultDispatcher().shutdown();
    }

    @Override
    public Logger getLogger() {
        return LOGGER;
    }

    @Override
    public void onMeasurements(IDeviceMeasurements measurements) throws SiteWhereException {
        String assignmentToken = measurements.getDeviceAssignmentToken();
        connection.publish("SiteWhere/system/" + assignmentToken + "/measurements", MarshalUtils.marshalJson(measurements), QoS.AT_MOST_ONCE, false);
    }

    @Override
    public void onAlert(IDeviceAlert alert) throws SiteWhereException {
        String assignmentToken = alert.getDeviceAssignmentToken();
        connection.publish("SiteWhere/system/" + assignmentToken + "/alert", MarshalUtils.marshalJson(alert), QoS.AT_MOST_ONCE, false);
    }

    @Override
    public void onLocation(IDeviceLocation location) throws SiteWhereException {
        String assignmentToken = location.getDeviceAssignmentToken();
        connection.publish("SiteWhere/system/" + assignmentToken + "/location", MarshalUtils.marshalJson(location), QoS.AT_MOST_ONCE, false);
    }
    
    /**
     * @return the hostname
     */
    public String getHostname() {
        return hostname;
    }

    /**
     * @return the port
     */
    public int getPort() {
        return port;
    }

    /**
     * @param hostname the hostname to set
     */
    public void setHostname(String hostname) {
        this.hostname = hostname;
    }

    /**
     * @param port the port to set
     */
    public void setPort(int port) {
        this.port = port;
    }

}
