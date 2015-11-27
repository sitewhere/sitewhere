/*
 * Copyright (c) SiteWhere, LLC. All rights reserved. http://www.sitewhere.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package com.sitewhere.device.communication.mqtt;

/**
 * Allows MQTT settings to be configured consistently across components.
 * 
 * @author Derek
 */
public interface IMqttComponent {

	/**
	 * Get protocol used to access the broker.
	 * 
	 * @return
	 */
	public String getProtocol();

	/**
	 * Get broker hostname.
	 * 
	 * @return
	 */
	public String getHostname();

	/**
	 * Get broker port.
	 * 
	 * @return
	 */
	public int getPort();

	/**
	 * Get full path to trust store for TLS/SSL.
	 * 
	 * @return
	 */
	public String getTrustStorePath();

	/**
	 * Get password for TLS/SSL trust store.
	 * 
	 * @return
	 */
	public String getTrustStorePassword();
}