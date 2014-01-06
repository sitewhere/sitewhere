/*
 * $Id$
 * --------------------------------------------------------------------------------------
 * Copyright (c) Reveal Technologies, LLC. All rights reserved. http://www.reveal-tech.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package com.sitewhere.web.rest.model;

import org.apache.log4j.Logger;

import com.sitewhere.rest.model.asset.HardwareAsset;
import com.sitewhere.rest.model.common.MetadataProviderEntity;
import com.sitewhere.rest.model.device.Device;
import com.sitewhere.server.SiteWhereServer;
import com.sitewhere.spi.SiteWhereException;
import com.sitewhere.spi.asset.AssetType;
import com.sitewhere.spi.asset.IAssetModuleManager;
import com.sitewhere.spi.device.IDevice;
import com.sitewhere.spi.device.IDeviceAssignment;

/**
 * Configurable helper class that allows Device model objects to be created from IDevice
 * SPI objects.
 * 
 * @author dadams
 */
public class DeviceMarshalHelper {

	/** Static logger instance */
	private static Logger LOGGER = Logger.getLogger(DeviceMarshalHelper.class);

	/** Indicates whether device asset information is to be included */
	private boolean includeAsset = true;

	/** Indicates whether device assignment information is to be copied */
	private boolean includeAssignment = false;

	/** Helper for marshaling device assignement information */
	private DeviceAssignmentMarshalHelper assignmentHelper;

	/**
	 * Convert an IDevice SPI object into a model object for marshaling.
	 * 
	 * @param source
	 * @param manager
	 * @return
	 * @throws SiteWhereException
	 */
	public Device convert(IDevice source, IAssetModuleManager manager) throws SiteWhereException {
		Device result = new Device();
		result.setHardwareId(source.getHardwareId());
		result.setComments(source.getComments());
		MetadataProviderEntity.copy(source, result);
		HardwareAsset deviceAsset = (HardwareAsset) manager.getAssetById(AssetType.Device,
				source.getAssetId());
		if ((includeAsset) || (deviceAsset == null)) {
			result.setDeviceAsset(deviceAsset);
		} else {
			result.setAssetId(source.getAssetId());
			result.setAssetName(deviceAsset.getName());
			result.setAssetImageUrl(deviceAsset.getImageUrl());
		}
		if (source.getAssignmentToken() != null) {
			if (includeAssignment) {
				try {
					IDeviceAssignment assignment = SiteWhereServer.getInstance().getDeviceManagement()
							.getCurrentDeviceAssignment(source);
					result.setAssignment(getAssignmentHelper().convert(assignment, manager));
				} catch (SiteWhereException e) {
					LOGGER.warn("Device has token for non-existent assignment.");
				}
			} else {
				result.setAssignmentToken(source.getAssignmentToken());
			}
		}
		return result;
	}

	/**
	 * Get helper class for marshaling assignment.
	 * 
	 * @return
	 */
	protected DeviceAssignmentMarshalHelper getAssignmentHelper() {
		if (assignmentHelper == null) {
			assignmentHelper = new DeviceAssignmentMarshalHelper();
			assignmentHelper.setIncludeAsset(false);
			assignmentHelper.setIncludeDevice(false);
			assignmentHelper.setIncludeSite(false);
		}
		return assignmentHelper;
	}

	public boolean isIncludeAsset() {
		return includeAsset;
	}

	public void setIncludeAsset(boolean includeAsset) {
		this.includeAsset = includeAsset;
	}

	public boolean isIncludeAssignment() {
		return includeAssignment;
	}

	public void setIncludeAssignment(boolean includeAssignment) {
		this.includeAssignment = includeAssignment;
	}
}