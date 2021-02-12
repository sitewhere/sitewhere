/**
 * Copyright © 2014-2021 The SiteWhere Authors
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.sitewhere.labels.qrcode;

import java.net.URI;

import com.sitewhere.labels.configuration.qrcode.QrCodeGeneratorConfiguration;
import com.sitewhere.labels.spi.IEntityUriProvider;
import com.sitewhere.labels.spi.ILabelGenerator;
import com.sitewhere.microservice.lifecycle.TenantEngineLifecycleComponent;
import com.sitewhere.spi.SiteWhereException;
import com.sitewhere.spi.area.IArea;
import com.sitewhere.spi.area.IAreaType;
import com.sitewhere.spi.asset.IAsset;
import com.sitewhere.spi.asset.IAssetType;
import com.sitewhere.spi.customer.ICustomer;
import com.sitewhere.spi.customer.ICustomerType;
import com.sitewhere.spi.device.IDevice;
import com.sitewhere.spi.device.IDeviceAssignment;
import com.sitewhere.spi.device.IDeviceType;
import com.sitewhere.spi.device.group.IDeviceGroup;
import com.sitewhere.spi.microservice.lifecycle.LifecycleComponentType;

import net.glxn.qrgen.core.image.ImageType;
import net.glxn.qrgen.javase.QRCode;

/**
 * Implementation of {@link ILabelGenerator} that generates QR-Codes for
 * SiteWhere entities.
 */
public class QrCodeGenerator extends TenantEngineLifecycleComponent implements ILabelGenerator {

    /** Configuration */
    private QrCodeGeneratorConfiguration configuration;

    public QrCodeGenerator(QrCodeGeneratorConfiguration configuration) {
	super(LifecycleComponentType.LabelGenerator);
	this.configuration = configuration;
    }

    /*
     * @see com.sitewhere.microservice.api.label.ILabelGenerator#getId()
     */
    @Override
    public String getId() throws SiteWhereException {
	return getConfiguration().getId();
    }

    /*
     * @see com.sitewhere.microservice.api.label.ILabelGenerator#getName()
     */
    @Override
    public String getName() throws SiteWhereException {
	return getConfiguration().getName();
    }

    /*
     * @see
     * com.sitewhere.spi.label.ILabelGenerator#getCustomerTypeLabel(com.sitewhere.
     * spi.customer.ICustomerType, com.sitewhere.spi.label.IEntityUriProvider)
     */
    @Override
    public byte[] getCustomerTypeLabel(ICustomerType customerType, IEntityUriProvider provider)
	    throws SiteWhereException {
	URI uri = provider.getCustomerTypeIdentifier(customerType);
	return toQrCodeImage(uri);
    }

    /*
     * @see
     * com.sitewhere.spi.label.ILabelGenerator#getCustomerLabel(com.sitewhere.spi.
     * customer.ICustomer, com.sitewhere.spi.label.IEntityUriProvider)
     */
    @Override
    public byte[] getCustomerLabel(ICustomer customer, IEntityUriProvider provider) throws SiteWhereException {
	URI uri = provider.getCustomerIdentifier(customer);
	return toQrCodeImage(uri);
    }

    /*
     * @see
     * com.sitewhere.spi.label.ILabelGenerator#getAreaTypeLabel(com.sitewhere.spi.
     * area.IAreaType, com.sitewhere.spi.label.IEntityUriProvider)
     */
    @Override
    public byte[] getAreaTypeLabel(IAreaType areaType, IEntityUriProvider provider) throws SiteWhereException {
	URI uri = provider.getAreaTypeIdentifier(areaType);
	return toQrCodeImage(uri);
    }

    /*
     * @see
     * com.sitewhere.spi.label.ILabelGenerator#getAreaLabel(com.sitewhere.spi.area.
     * IArea, com.sitewhere.spi.label.IEntityUriProvider)
     */
    @Override
    public byte[] getAreaLabel(IArea area, IEntityUriProvider provider) throws SiteWhereException {
	URI uri = provider.getAreaIdentifier(area);
	return toQrCodeImage(uri);
    }

    /*
     * @see
     * com.sitewhere.spi.label.ILabelGenerator#getDeviceTypeLabel(com.sitewhere.spi.
     * device.IDeviceType, com.sitewhere.spi.label.IEntityUriProvider)
     */
    @Override
    public byte[] getDeviceTypeLabel(IDeviceType deviceType, IEntityUriProvider provider) throws SiteWhereException {
	URI uri = provider.getDeviceTypeIdentifier(deviceType);
	return toQrCodeImage(uri);
    }

    /*
     * @see
     * com.sitewhere.spi.label.ILabelGenerator#getDeviceLabel(com.sitewhere.spi.
     * device.IDevice, com.sitewhere.spi.label.IEntityUriProvider)
     */
    @Override
    public byte[] getDeviceLabel(IDevice device, IEntityUriProvider provider) throws SiteWhereException {
	URI uri = provider.getDeviceIdentifier(device);
	return toQrCodeImage(uri);
    }

    /*
     * @see
     * com.sitewhere.spi.label.ILabelGenerator#getDeviceGroupLabel(com.sitewhere.spi
     * .device.group.IDeviceGroup, com.sitewhere.spi.label.IEntityUriProvider)
     */
    @Override
    public byte[] getDeviceGroupLabel(IDeviceGroup group, IEntityUriProvider provider) throws SiteWhereException {
	URI uri = provider.getDeviceGroupIdentifier(group);
	return toQrCodeImage(uri);
    }

    /*
     * @see com.sitewhere.spi.label.ILabelGenerator#getDeviceAssignmentLabel(com.
     * sitewhere.spi.device.IDeviceAssignment,
     * com.sitewhere.spi.label.IEntityUriProvider)
     */
    @Override
    public byte[] getDeviceAssignmentLabel(IDeviceAssignment assignment, IEntityUriProvider provider)
	    throws SiteWhereException {
	URI uri = provider.getDeviceAssignmentIdentifier(assignment);
	return toQrCodeImage(uri);
    }

    /*
     * @see
     * com.sitewhere.spi.label.ILabelGenerator#getAssetTypeLabel(com.sitewhere.spi.
     * asset.IAssetType, com.sitewhere.spi.label.IEntityUriProvider)
     */
    @Override
    public byte[] getAssetTypeLabel(IAssetType assetType, IEntityUriProvider provider) throws SiteWhereException {
	URI uri = provider.getAssetTypeIdentifier(assetType);
	return toQrCodeImage(uri);
    }

    /*
     * @see
     * com.sitewhere.spi.label.ILabelGenerator#getAssetLabel(com.sitewhere.spi.asset
     * .IAsset, com.sitewhere.spi.label.IEntityUriProvider)
     */
    @Override
    public byte[] getAssetLabel(IAsset asset, IEntityUriProvider provider) throws SiteWhereException {
	URI uri = provider.getAssetIdentifier(asset);
	return toQrCodeImage(uri);
    }

    protected QrCodeGeneratorConfiguration getConfiguration() {
	return configuration;
    }

    protected byte[] toQrCodeImage(URI uri) {
	return QRCode.from(uri.toString()).withSize(getConfiguration().getWidth(), getConfiguration().getHeight())
		.withColor(parse(getConfiguration().getForegroundColor()),
			parse(getConfiguration().getBackgroundColor()))
		.to(ImageType.PNG).stream().toByteArray();
    }

    /**
     * Parse an ARGB string into an integer.
     * 
     * @param argb
     * @return
     */
    protected static int parse(String rgba) {
	if (rgba.startsWith("0x")) {
	    rgba = rgba.substring(2);
	}
	if (rgba.length() != 8) {
	    return 0;
	}
	byte r = (byte) Integer.parseInt(rgba.substring(0, 2), 16);
	byte g = (byte) Integer.parseInt(rgba.substring(2, 4), 16);
	byte b = (byte) Integer.parseInt(rgba.substring(4, 6), 16);
	byte a = (byte) Integer.parseInt(rgba.substring(6, 8), 16);
	return (a << 24) + ((r & 0xFF) << 16) + ((g & 0xFF) << 8) + (b & 0xFF);
    }
}