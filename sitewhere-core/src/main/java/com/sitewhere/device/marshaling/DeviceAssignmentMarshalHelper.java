/*
 * $Id$
 * --------------------------------------------------------------------------------------
 * Copyright (c) Reveal Technologies, LLC. All rights reserved. http://www.reveal-tech.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package com.sitewhere.device.marshaling;

import org.apache.log4j.Logger;

import com.sitewhere.rest.model.asset.HardwareAsset;
import com.sitewhere.rest.model.asset.LocationAsset;
import com.sitewhere.rest.model.asset.PersonAsset;
import com.sitewhere.rest.model.common.MetadataProviderEntity;
import com.sitewhere.rest.model.device.DeviceAssignment;
import com.sitewhere.rest.model.device.DeviceAssignmentState;
import com.sitewhere.rest.model.device.Site;
import com.sitewhere.server.SiteWhereServer;
import com.sitewhere.spi.SiteWhereException;
import com.sitewhere.spi.asset.IAsset;
import com.sitewhere.spi.asset.IAssetModuleManager;
import com.sitewhere.spi.device.DeviceAssignmentType;
import com.sitewhere.spi.device.IDevice;
import com.sitewhere.spi.device.IDeviceAssignment;
import com.sitewhere.spi.device.ISite;

/**
 * Configurable helper class that allows DeviceAssignment model objects to be created from
 * IDeviceAssignment SPI objects.
 * 
 * @author dadams
 */
public class DeviceAssignmentMarshalHelper {

	/** Static logger instance */
	private static Logger LOGGER = Logger.getLogger(DeviceAssignmentMarshalHelper.class);

	/** Indicates whether device asset information is to be included */
	private boolean includeAsset = true;

	/** Indicates whether to include device information */
	private boolean includeDevice = false;

	/** Indicates whether to include site information */
	private boolean includeSite = false;

	/** Used to control marshaling of devices */
	private DeviceMarshalHelper deviceHelper;

	/**
	 * Convert the SPI object into a model object for marshaling.
	 * 
	 * @param source
	 * @param manager
	 * @return
	 * @throws SiteWhereException
	 */
	public DeviceAssignment convert(IDeviceAssignment source, IAssetModuleManager manager)
			throws SiteWhereException {
		DeviceAssignment result = new DeviceAssignment();
		result.setToken(source.getToken());
		result.setActiveDate(source.getActiveDate());
		result.setReleasedDate(source.getReleasedDate());
		result.setStatus(source.getStatus());
		result.setAssignmentType(source.getAssignmentType());
		result.setAssetModuleId(source.getAssetModuleId());
		result.setAssetId(source.getAssetId());
		MetadataProviderEntity.copy(source, result);
		if (source.getState() != null) {
			result.setState(DeviceAssignmentState.copy(source.getState()));
		}
		if (source.getAssignmentType() != DeviceAssignmentType.Unassociated) {
			IAsset asset = manager.getAssetById(source.getAssetModuleId(), source.getAssetId());
			if (isIncludeAsset() || (asset == null)) {
				if (asset instanceof HardwareAsset) {
					result.setAssociatedHardware((HardwareAsset) asset);
				} else if (asset instanceof PersonAsset) {
					result.setAssociatedPerson((PersonAsset) asset);
				} else if (asset instanceof LocationAsset) {
					result.setAssociatedLocation((LocationAsset) asset);
				}
			} else {
				result.setAssetName(asset.getName());
				result.setAssetImageUrl(asset.getImageUrl());
			}
		}
		result.setSiteToken(source.getSiteToken());
		if (isIncludeSite()) {
			ISite site = SiteWhereServer.getInstance().getDeviceManagement().getSiteForAssignment(source);
			result.setSite(Site.copy(site));
		}
		result.setDeviceHardwareId(source.getDeviceHardwareId());
		if (isIncludeDevice()) {
			IDevice device =
					SiteWhereServer.getInstance().getDeviceManagement().getDeviceForAssignment(source);
			if (device != null) {
				result.setDevice(getDeviceHelper().convert(device, manager));
			} else {
				LOGGER.error("Assignment references invalid hardware id.");
			}
		}
		return result;
	}

	/**
	 * Get the helper for marshaling device information.
	 * 
	 * @return
	 */
	protected DeviceMarshalHelper getDeviceHelper() {
		if (deviceHelper == null) {
			deviceHelper = new DeviceMarshalHelper();
			deviceHelper.setIncludeAsset(false);
			deviceHelper.setIncludeAssignment(false);
			deviceHelper.setIncludeSpecification(false);
		}
		return deviceHelper;
	}

	public boolean isIncludeAsset() {
		return includeAsset;
	}

	public DeviceAssignmentMarshalHelper setIncludeAsset(boolean includeAsset) {
		this.includeAsset = includeAsset;
		return this;
	}

	public boolean isIncludeDevice() {
		return includeDevice;
	}

	public DeviceAssignmentMarshalHelper setIncludeDevice(boolean includeDevice) {
		this.includeDevice = includeDevice;
		return this;
	}

	public boolean isIncludeSite() {
		return includeSite;
	}

	public DeviceAssignmentMarshalHelper setIncludeSite(boolean includeSite) {
		this.includeSite = includeSite;
		return this;
	}
}