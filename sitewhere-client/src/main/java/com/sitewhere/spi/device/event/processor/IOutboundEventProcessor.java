/*
 * Copyright (c) SiteWhere, LLC. All rights reserved. http://www.sitewhere.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package com.sitewhere.spi.device.event.processor;

import com.sitewhere.spi.SiteWhereException;
import com.sitewhere.spi.device.batch.IBatchOperation;
import com.sitewhere.spi.device.event.IDeviceAlert;
import com.sitewhere.spi.device.event.IDeviceCommandInvocation;
import com.sitewhere.spi.device.event.IDeviceCommandResponse;
import com.sitewhere.spi.device.event.IDeviceLocation;
import com.sitewhere.spi.device.event.IDeviceMeasurements;
import com.sitewhere.spi.server.lifecycle.ILifecycleComponent;

/**
 * Allows intereseted entities to interact with SiteWhere outbound event processing.
 * 
 * @author Derek
 */
public interface IOutboundEventProcessor extends ILifecycleComponent {

	/**
	 * Executes code after device measurements have been successfully saved.
	 * 
	 * @param measurements
	 * @throws SiteWhereException
	 */
	public void onMeasurements(IDeviceMeasurements measurements) throws SiteWhereException;

	/**
	 * Executes code after device location has been successfully saved.
	 * 
	 * @param location
	 * @throws SiteWhereException
	 */
	public void onLocation(IDeviceLocation location) throws SiteWhereException;

	/**
	 * Executes code after device alert has been successfully saved.
	 * 
	 * @param location
	 * @throws SiteWhereException
	 */
	public void onAlert(IDeviceAlert alert) throws SiteWhereException;

	/**
	 * Executes code after device command invocation has been successfully saved.
	 * 
	 * @param invocation
	 * @throws SiteWhereException
	 */
	public void onCommandInvocation(IDeviceCommandInvocation invocation) throws SiteWhereException;

	/**
	 * Executes code after device command response has been successfully saved.
	 * 
	 * @param response
	 * @throws SiteWhereException
	 */
	public void onCommandResponse(IDeviceCommandResponse response) throws SiteWhereException;

	/**
	 * Executes code after batch operation has been successfully saved.
	 * 
	 * @param operation
	 * @throws SiteWhereException
	 */
	public void onBatchOperation(IBatchOperation operation) throws SiteWhereException;
}