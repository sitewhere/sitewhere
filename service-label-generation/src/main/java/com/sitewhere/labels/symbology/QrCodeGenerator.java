/*
 * Copyright (c) SiteWhere, LLC. All rights reserved. http://www.sitewhere.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package com.sitewhere.labels.symbology;

import java.net.URI;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.sitewhere.server.lifecycle.TenantEngineLifecycleComponent;
import com.sitewhere.spi.SiteWhereException;
import com.sitewhere.spi.area.IArea;
import com.sitewhere.spi.area.IAreaType;
import com.sitewhere.spi.asset.IAsset;
import com.sitewhere.spi.asset.IAssetType;
import com.sitewhere.spi.device.IDevice;
import com.sitewhere.spi.device.IDeviceAssignment;
import com.sitewhere.spi.device.IDeviceType;
import com.sitewhere.spi.device.group.IDeviceGroup;
import com.sitewhere.spi.label.IEntityUriProvider;
import com.sitewhere.spi.label.ILabelGenerator;
import com.sitewhere.spi.server.lifecycle.LifecycleComponentType;

import net.glxn.qrgen.core.image.ImageType;
import net.glxn.qrgen.javase.QRCode;

/**
 * Implementation of {@link ILabelGenerator} that generates QR-Codes for
 * SiteWhere entities.
 * 
 * @author Derek
 */
public class QrCodeGenerator extends TenantEngineLifecycleComponent implements ILabelGenerator {

    /** Static logger instance */
    private static Log LOGGER = LogFactory.getLog(QrCodeGenerator.class);

    /** Generator id */
    private String id;

    /** Generator name */
    private String name;

    /** Image width in pixels */
    private int width = 200;

    /** Image height in pixels */
    private int height = 200;

    /** Foreground color */
    private int foregroundColor = 0xff333333;

    /** Background color */
    private int backgroundColor = 0xffffffff;

    public QrCodeGenerator() {
	super(LifecycleComponentType.LabelGenerator);
    }

    /*
     * @see
     * com.sitewhere.spi.label.ILabelGenerator#getAreaTypeLabel(com.sitewhere.spi.
     * area.IAreaType, com.sitewhere.spi.label.IEntityUriProvider)
     */
    @Override
    public byte[] getAreaTypeLabel(IAreaType areaType, IEntityUriProvider provider) throws SiteWhereException {
	URI uri = provider.getAreaTypeIdentifier(areaType);
	return QRCode.from(uri.toString()).withSize(getWidth(), getHeight())
		.withColor(getForegroundColor(), getBackgroundColor()).to(ImageType.PNG).stream().toByteArray();
    }

    /*
     * @see
     * com.sitewhere.spi.label.ILabelGenerator#getAreaLabel(com.sitewhere.spi.area.
     * IArea, com.sitewhere.spi.label.IEntityUriProvider)
     */
    @Override
    public byte[] getAreaLabel(IArea area, IEntityUriProvider provider) throws SiteWhereException {
	URI uri = provider.getAreaIdentifier(area);
	return QRCode.from(uri.toString()).withSize(getWidth(), getHeight())
		.withColor(getForegroundColor(), getBackgroundColor()).to(ImageType.PNG).stream().toByteArray();
    }

    /*
     * @see
     * com.sitewhere.spi.label.ILabelGenerator#getDeviceTypeLabel(com.sitewhere.spi.
     * device.IDeviceType, com.sitewhere.spi.label.IEntityUriProvider)
     */
    @Override
    public byte[] getDeviceTypeLabel(IDeviceType deviceType, IEntityUriProvider provider) throws SiteWhereException {
	URI uri = provider.getDeviceTypeIdentifier(deviceType);
	return QRCode.from(uri.toString()).withSize(getWidth(), getHeight())
		.withColor(getForegroundColor(), getBackgroundColor()).to(ImageType.PNG).stream().toByteArray();
    }

    /*
     * @see
     * com.sitewhere.spi.label.ILabelGenerator#getDeviceLabel(com.sitewhere.spi.
     * device.IDevice, com.sitewhere.spi.label.IEntityUriProvider)
     */
    @Override
    public byte[] getDeviceLabel(IDevice device, IEntityUriProvider provider) throws SiteWhereException {
	URI uri = provider.getDeviceIdentifier(device);
	return QRCode.from(uri.toString()).withSize(getWidth(), getHeight())
		.withColor(getForegroundColor(), getBackgroundColor()).to(ImageType.PNG).stream().toByteArray();
    }

    /*
     * @see
     * com.sitewhere.spi.label.ILabelGenerator#getDeviceGroupLabel(com.sitewhere.spi
     * .device.group.IDeviceGroup, com.sitewhere.spi.label.IEntityUriProvider)
     */
    @Override
    public byte[] getDeviceGroupLabel(IDeviceGroup group, IEntityUriProvider provider) throws SiteWhereException {
	URI uri = provider.getDeviceGroupIdentifier(group);
	return QRCode.from(uri.toString()).withSize(getWidth(), getHeight())
		.withColor(getForegroundColor(), getBackgroundColor()).to(ImageType.PNG).stream().toByteArray();
    }

    /*
     * @see
     * com.sitewhere.spi.label.ILabelGenerator#getDeviceAssigmentLabel(com.sitewhere
     * .spi.device.IDeviceAssignment, com.sitewhere.spi.label.IEntityUriProvider)
     */
    @Override
    public byte[] getDeviceAssigmentLabel(IDeviceAssignment assignment, IEntityUriProvider provider)
	    throws SiteWhereException {
	URI uri = provider.getDeviceAssignmentIdentifier(assignment);
	return QRCode.from(uri.toString()).withSize(getWidth(), getHeight())
		.withColor(getForegroundColor(), getBackgroundColor()).to(ImageType.PNG).stream().toByteArray();
    }

    /*
     * @see
     * com.sitewhere.spi.label.ILabelGenerator#getAssetTypeLabel(com.sitewhere.spi.
     * asset.IAssetType, com.sitewhere.spi.label.IEntityUriProvider)
     */
    @Override
    public byte[] getAssetTypeLabel(IAssetType assetType, IEntityUriProvider provider) throws SiteWhereException {
	URI uri = provider.getAssetTypeIdentifier(assetType);
	return QRCode.from(uri.toString()).withSize(getWidth(), getHeight())
		.withColor(getForegroundColor(), getBackgroundColor()).to(ImageType.PNG).stream().toByteArray();
    }

    /*
     * @see
     * com.sitewhere.spi.label.ILabelGenerator#getAssetLabel(com.sitewhere.spi.asset
     * .IAsset, com.sitewhere.spi.label.IEntityUriProvider)
     */
    @Override
    public byte[] getAssetLabel(IAsset asset, IEntityUriProvider provider) throws SiteWhereException {
	URI uri = provider.getAssetIdentifier(asset);
	return QRCode.from(uri.toString()).withSize(getWidth(), getHeight())
		.withColor(getForegroundColor(), getBackgroundColor()).to(ImageType.PNG).stream().toByteArray();
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.sitewhere.spi.server.lifecycle.ILifecycleComponent#getLogger()
     */
    @Override
    public Log getLogger() {
	return LOGGER;
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.sitewhere.spi.device.symbology.ISymbolGenerator#getId()
     */
    public String getId() {
	return id;
    }

    public void setId(String id) {
	this.id = id;
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.sitewhere.spi.device.symbology.ISymbolGenerator#getName()
     */
    public String getName() {
	return name;
    }

    public void setName(String name) {
	this.name = name;
    }

    public int getWidth() {
	return width;
    }

    public void setWidth(int width) {
	this.width = width;
    }

    public int getHeight() {
	return height;
    }

    public void setHeight(int height) {
	this.height = height;
    }

    public int getForegroundColor() {
	return foregroundColor;
    }

    public void setForegroundColor(String foregroundColor) {
	this.foregroundColor = parse(foregroundColor);
    }

    public int getBackgroundColor() {
	return backgroundColor;
    }

    public void setBackgroundColor(String backgroundColor) {
	this.backgroundColor = parse(backgroundColor);
    }

    /**
     * Parse an ARGB string into an integer.
     * 
     * @param argb
     * @return
     */
    protected static int parse(String argb) {
	if (argb.length() != 8) {
	    return 0;
	}
	byte a = (byte) Integer.parseInt(argb.substring(0, 2), 16);
	byte r = (byte) Integer.parseInt(argb.substring(2, 4), 16);
	byte g = (byte) Integer.parseInt(argb.substring(4, 6), 16);
	byte b = (byte) Integer.parseInt(argb.substring(6, 8), 16);
	return (a << 24) + ((r & 0xFF) << 16) + ((g & 0xFF) << 8) + (b & 0xFF);
    }
}