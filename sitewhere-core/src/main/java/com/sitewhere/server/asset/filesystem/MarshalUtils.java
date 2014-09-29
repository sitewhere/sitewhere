/*
 * Copyright (c) SiteWhere, LLC. All rights reserved. http://www.sitewhere.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package com.sitewhere.server.asset.filesystem;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.Unmarshaller;

import com.sitewhere.rest.model.asset.HardwareAsset;
import com.sitewhere.rest.model.asset.PersonAsset;
import com.sitewhere.spi.SiteWhereException;
import com.sitewhere.spi.asset.AssetType;

/**
 * Utility methods for marshaling XML configuration files for assets.
 * 
 * @author Derek
 */
public class MarshalUtils {

	/**
	 * Loads a list of hardware assets from a file.
	 * 
	 * @param config
	 * @param type
	 * @return
	 * @throws SiteWhereException
	 */
	public static List<HardwareAsset> loadHardwareAssets(File config, AssetType type)
			throws SiteWhereException {
		try {
			List<HardwareAsset> assets = new ArrayList<HardwareAsset>();
			JAXBContext jaxbContext = JAXBContext.newInstance(FileSystemHardwareAssets.class);
			Unmarshaller jaxbUnmarshaller = jaxbContext.createUnmarshaller();
			FileSystemHardwareAssets xmlAssets =
					(FileSystemHardwareAssets) jaxbUnmarshaller.unmarshal(config);
			for (FileSystemHardwareAsset xmlAsset : xmlAssets.getHardwareAssets()) {
				HardwareAsset asset = new HardwareAsset();
				asset.setId(xmlAsset.getId());
				asset.setName(xmlAsset.getName());
				asset.setDescription(xmlAsset.getDescription());
				asset.setSku(xmlAsset.getSku());
				asset.setImageUrl(xmlAsset.getImageUrl());
				for (FileSystemAssetProperty xmlProperty : xmlAsset.getProperties()) {
					asset.setProperty(xmlProperty.getName(), xmlProperty.getValue());
				}
				if ((type == AssetType.Hardware) && (!xmlAsset.isDevice())) {
					assets.add(asset);
				}
				if ((type == AssetType.Device) && (xmlAsset.isDevice())) {
					assets.add(asset);
				}
			}
			return assets;
		} catch (Exception e) {
			throw new SiteWhereException("Unable to unmarshal assets file.", e);
		}
	}

	/**
	 * Loads a list of person assets from a file.
	 * 
	 * @param config
	 * @return
	 * @throws SiteWhereException
	 */
	public static List<PersonAsset> loadPersonAssets(File config) throws SiteWhereException {
		List<PersonAsset> assets = new ArrayList<PersonAsset>();
		try {
			JAXBContext jaxbContext = JAXBContext.newInstance(FileSystemPersonAssets.class);
			Unmarshaller jaxbUnmarshaller = jaxbContext.createUnmarshaller();
			FileSystemPersonAssets xmlAssets = (FileSystemPersonAssets) jaxbUnmarshaller.unmarshal(config);
			for (FileSystemPersonAsset xmlAsset : xmlAssets.getPersonAssets()) {
				PersonAsset asset = new PersonAsset();
				asset.setId(xmlAsset.getId());
				asset.setName(xmlAsset.getName());
				asset.setUserName(xmlAsset.getUserName());
				asset.setEmailAddress(xmlAsset.getEmailAddress());
				asset.setImageUrl(xmlAsset.getPhotoUrl());
				for (FileSystemAssetProperty xmlProperty : xmlAsset.getProperties()) {
					asset.setProperty(xmlProperty.getName(), xmlProperty.getValue());
				}
				if (xmlAsset.getRoles() != null) {
					List<String> roles = xmlAsset.getRoles().getRoles();
					for (String role : roles) {
						asset.getRoles().add(role);
					}
				}
				assets.add(asset);
			}
			return assets;
		} catch (Exception e) {
			throw new SiteWhereException("Unable to unmarshal person assets file.", e);
		}
	}
}
