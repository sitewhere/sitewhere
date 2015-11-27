/*
 * Copyright (c) SiteWhere, LLC. All rights reserved. http://www.sitewhere.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package com.sitewhere.spark;

import org.apache.spark.storage.StorageLevel;
import org.apache.spark.streaming.receiver.Receiver;

import com.hazelcast.client.HazelcastClient;
import com.hazelcast.client.config.ClientConfig;
import com.hazelcast.core.HazelcastInstance;
import com.hazelcast.core.ITopic;
import com.hazelcast.core.Message;
import com.hazelcast.core.MessageListener;
import com.sitewhere.rest.model.device.event.DeviceAlert;
import com.sitewhere.rest.model.device.event.DeviceLocation;
import com.sitewhere.rest.model.device.event.DeviceMeasurements;
import com.sitewhere.spi.device.event.IDeviceEvent;
import com.sitewhere.spi.server.hazelcast.ISiteWhereHazelcast;

/**
 * Implements a {@link Receiver} that listens for events on the default Hazelcast topics
 * published by SiteWhere and makes them available to Spark.
 * 
 * @author Derek
 */
public class SiteWhereReceiver extends Receiver<IDeviceEvent> {

	/** Serial version UID */
	private static final long serialVersionUID = 5103117471275649264L;

	/** Number of milliseconds to wait for Hazelcast to connect */
	private static final int HAZELCAST_CONNECTION_TIMEOUT = 10000;

	/** Hazelcast client for SiteWhere */
	private HazelcastInstance hazelcast;

	/** Measurements topic */
	private ITopic<DeviceMeasurements> measurements;

	/** Locations topic */
	private ITopic<DeviceLocation> locations;

	/** Alerts topic */
	private ITopic<DeviceAlert> alerts;

	/** Hazelcast address set in connect */
	private String hazelcastAddress;

	/** Hazelcast username set in connect */
	private String username;

	/** Hazelcast password set in connect */
	private String password;

	/** Tenant id set in connect */
	private String tenantId;

	public SiteWhereReceiver(String hazelcastAddress, String username, String password, String tenantId) {
		super(StorageLevel.MEMORY_AND_DISK_SER_2());
		this.hazelcastAddress = hazelcastAddress;
		this.username = username;
		this.password = password;
		this.tenantId = tenantId;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.apache.spark.streaming.receiver.Receiver#onStart()
	 */
	@Override
	public void onStart() {
		try {
			// Configure the Hazelcast client.
			ClientConfig clientConfig = new ClientConfig();
			clientConfig.getGroupConfig().setName(getUsername());
			clientConfig.getGroupConfig().setPassword(getPassword());

			clientConfig.getNetworkConfig().setConnectionTimeout(HAZELCAST_CONNECTION_TIMEOUT);
			clientConfig.getNetworkConfig().addAddress(getHazelcastAddress());
			clientConfig.getNetworkConfig().setSmartRouting(true);

			this.hazelcast = HazelcastClient.newHazelcastClient(clientConfig);

			// Subscribe to measurements and process them.
			this.measurements = hazelcast.getTopic(ISiteWhereHazelcast.TOPIC_MEASUREMENTS_ADDED);
			measurements.addMessageListener(new MeasurementsEventListener());

			// Subscribe to locations and process them.
			this.locations = hazelcast.getTopic(ISiteWhereHazelcast.TOPIC_LOCATION_ADDED);
			locations.addMessageListener(new LocationsEventListener());

			// Subscribe to alerts and process them.
			this.alerts = hazelcast.getTopic(ISiteWhereHazelcast.TOPIC_ALERT_ADDED);
			alerts.addMessageListener(new AlertsEventListener());
		} catch (Exception e) {
			reportError("Unable to start SiteWhere receiver.", e);
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.apache.spark.streaming.receiver.Receiver#onStop()
	 */
	@Override
	public void onStop() {
		if (hazelcast != null) {
			hazelcast.shutdown();
		}
	}

	/**
	 * Handles inbound measurements events.
	 * 
	 * @author Derek
	 */
	private class MeasurementsEventListener implements MessageListener<DeviceMeasurements> {

		/*
		 * (non-Javadoc)
		 * 
		 * @see com.hazelcast.core.MessageListener#onMessage(com.hazelcast.core.Message)
		 */
		@Override
		public void onMessage(Message<DeviceMeasurements> message) {
			try {
				DeviceMeasurements mx = message.getMessageObject();
				SiteWhereReceiver.this.store(mx);
			} catch (Exception e) {
				SiteWhereReceiver.this.reportError("Error receiving SiteWhere measurements.", e);
			}
		}
	}

	/**
	 * Handles inbound location events.
	 * 
	 * @author Derek
	 */
	private class LocationsEventListener implements MessageListener<DeviceLocation> {

		/*
		 * (non-Javadoc)
		 * 
		 * @see com.hazelcast.core.MessageListener#onMessage(com.hazelcast.core.Message)
		 */
		@Override
		public void onMessage(Message<DeviceLocation> message) {
			try {
				DeviceLocation location = message.getMessageObject();
				SiteWhereReceiver.this.store(location);
			} catch (Exception e) {
				SiteWhereReceiver.this.reportError("Error receiving SiteWhere location.", e);
			}
		}
	}

	/**
	 * Handles inbound alert events.
	 * 
	 * @author Derek
	 */
	private class AlertsEventListener implements MessageListener<DeviceAlert> {

		/*
		 * (non-Javadoc)
		 * 
		 * @see com.hazelcast.core.MessageListener#onMessage(com.hazelcast.core.Message)
		 */
		@Override
		public void onMessage(Message<DeviceAlert> message) {
			try {
				DeviceAlert alert = message.getMessageObject();
				SiteWhereReceiver.this.store(alert);
			} catch (Exception e) {
				SiteWhereReceiver.this.reportError("Error receiving SiteWhere alert.", e);
			}
		}
	}

	public String getHazelcastAddress() {
		return hazelcastAddress;
	}

	public void setHazelcastAddress(String hazelcastAddress) {
		this.hazelcastAddress = hazelcastAddress;
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public String getTenantId() {
		return tenantId;
	}

	public void setTenantId(String tenantId) {
		this.tenantId = tenantId;
	}
}