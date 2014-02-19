/*
 * DeviceSpecificationMarshalHelper.java 
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
import com.sitewhere.rest.model.common.MetadataProviderEntity;
import com.sitewhere.rest.model.device.DeviceSpecification;
import com.sitewhere.server.SiteWhereServer;
import com.sitewhere.spi.SiteWhereException;
import com.sitewhere.spi.SiteWhereSystemException;
import com.sitewhere.spi.asset.AssetType;
import com.sitewhere.spi.asset.IAssetModuleManager;
import com.sitewhere.spi.device.IDeviceSpecification;
import com.sitewhere.spi.error.ErrorCode;
import com.sitewhere.spi.error.ErrorLevel;

/**
 * Configurable helper class that allows {@link DeviceSpecification} model objects to be
 * created from {@link IDeviceSpecification} SPI objects.
 * 
 * @author dadams
 */
public class DeviceSpecificationMarshalHelper {

	/** Static logger instance */
	private static Logger LOGGER = Logger.getLogger(DeviceSpecificationMarshalHelper.class);

	/** Indicates whether device specification asset information is to be included */
	private boolean includeAsset = true;

	/**
	 * Convert a device specification for marshaling.
	 * 
	 * @param source
	 * @param manager
	 * @return
	 * @throws SiteWhereException
	 */
	public DeviceSpecification convert(IDeviceSpecification source, IAssetModuleManager manager)
			throws SiteWhereException {
		DeviceSpecification spec = new DeviceSpecification();
		MetadataProviderEntity.copy(source, spec);
		spec.setToken(source.getToken());
		spec.setName(source.getName());
		HardwareAsset asset =
				(HardwareAsset) SiteWhereServer.getInstance().getAssetModuleManager().getAssetById(
						AssetType.Device, source.getAssetId());
		if (asset == null) {
			LOGGER.warn("Device specification has reference to non-existent asset.");
			throw new SiteWhereSystemException(ErrorCode.InvalidAssetReferenceId, ErrorLevel.ERROR);
		}
		if (isIncludeAsset()) {
			spec.setAsset(asset);
		} else {
			spec.setAssetId(asset.getId());
			spec.setAssetName(asset.getName());
			spec.setAssetImageUrl(asset.getImageUrl());
		}
		return spec;
	}

	public boolean isIncludeAsset() {
		return includeAsset;
	}

	public void setIncludeAsset(boolean includeAsset) {
		this.includeAsset = includeAsset;
	}
}