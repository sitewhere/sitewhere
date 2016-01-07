/*
 * Copyright (c) SiteWhere, LLC. All rights reserved. http://www.sitewhere.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package com.sitewhere.device.communication.symbology;

import java.net.URI;

import net.glxn.qrgen.core.image.ImageType;
import net.glxn.qrgen.javase.QRCode;

import org.apache.log4j.Logger;

import com.sitewhere.server.lifecycle.TenantLifecycleComponent;
import com.sitewhere.spi.SiteWhereException;
import com.sitewhere.spi.device.IDevice;
import com.sitewhere.spi.device.IDeviceAssignment;
import com.sitewhere.spi.device.IDeviceSpecification;
import com.sitewhere.spi.device.ISite;
import com.sitewhere.spi.device.symbology.IEntityUriProvider;
import com.sitewhere.spi.device.symbology.ISymbolGenerator;
import com.sitewhere.spi.server.lifecycle.LifecycleComponentType;

/**
 * Implementation of {@link ISymbolGenerator} that generates QR-Codes for SiteWhere
 * entities.
 * 
 * @author Derek
 */
public class QrCodeSymbolGenerator extends TenantLifecycleComponent implements ISymbolGenerator {

	/** Static logger instance */
	private static Logger LOGGER = Logger.getLogger(QrCodeSymbolGenerator.class);

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

	public QrCodeSymbolGenerator() {
		super(LifecycleComponentType.SymbolGenerator);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.sitewhere.spi.server.lifecycle.ILifecycleComponent#start()
	 */
	@Override
	public void start() throws SiteWhereException {
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.sitewhere.spi.server.lifecycle.ILifecycleComponent#stop()
	 */
	@Override
	public void stop() throws SiteWhereException {
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.sitewhere.spi.device.symbology.ISymbolGenerator#getSiteSymbol(com.sitewhere
	 * .spi.device.ISite, com.sitewhere.spi.device.symbology.IEntityUriProvider)
	 */
	@Override
	public byte[] getSiteSymbol(ISite site, IEntityUriProvider provider) throws SiteWhereException {
		URI uri = provider.getSiteIdentifier(site);
		return QRCode.from(uri.toString()).withSize(getWidth(), getHeight()).withColor(getForegroundColor(),
				getBackgroundColor()).to(ImageType.PNG).stream().toByteArray();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.sitewhere.spi.device.symbology.ISymbolGenerator#getDeviceSpecificationSymbol
	 * (com.sitewhere.spi.device.IDeviceSpecification,
	 * com.sitewhere.spi.device.symbology.IEntityUriProvider)
	 */
	@Override
	public byte[] getDeviceSpecificationSymbol(IDeviceSpecification specification, IEntityUriProvider provider)
			throws SiteWhereException {
		URI uri = provider.getDeviceSpecificationIdentifier(specification);
		return QRCode.from(uri.toString()).withSize(getWidth(), getHeight()).withColor(getForegroundColor(),
				getBackgroundColor()).to(ImageType.PNG).stream().toByteArray();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.sitewhere.spi.device.symbology.ISymbolGenerator#getDeviceSymbol(com.sitewhere
	 * .spi.device.IDevice, com.sitewhere.spi.device.symbology.IEntityUriProvider)
	 */
	@Override
	public byte[] getDeviceSymbol(IDevice device, IEntityUriProvider provider) throws SiteWhereException {
		URI uri = provider.getDeviceIdentifier(device);
		return QRCode.from(uri.toString()).withSize(getWidth(), getHeight()).withColor(getForegroundColor(),
				getBackgroundColor()).to(ImageType.PNG).stream().toByteArray();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.sitewhere.spi.device.symbology.ISymbolGenerator#getDeviceAssigmentSymbol(com
	 * .sitewhere.spi.device.IDeviceAssignment,
	 * com.sitewhere.spi.device.symbology.IEntityUriProvider)
	 */
	@Override
	public byte[] getDeviceAssigmentSymbol(IDeviceAssignment assignment, IEntityUriProvider provider)
			throws SiteWhereException {
		URI uri = provider.getDeviceAssignmentIdentifier(assignment);
		return QRCode.from(uri.toString()).withSize(getWidth(), getHeight()).withColor(getForegroundColor(),
				getBackgroundColor()).to(ImageType.PNG).stream().toByteArray();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.sitewhere.spi.server.lifecycle.ILifecycleComponent#getLogger()
	 */
	@Override
	public Logger getLogger() {
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