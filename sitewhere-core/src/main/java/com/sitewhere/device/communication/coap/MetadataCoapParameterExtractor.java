/*
 * Copyright (c) SiteWhere, LLC. All rights reserved. http://www.sitewhere.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package com.sitewhere.device.communication.coap;

import java.util.Map;

import com.sitewhere.spi.SiteWhereException;
import com.sitewhere.spi.device.IDeviceAssignment;
import com.sitewhere.spi.device.IDeviceNestingContext;
import com.sitewhere.spi.device.command.IDeviceCommandExecution;
import com.sitewhere.spi.device.communication.ICommandDeliveryParameterExtractor;

public class MetadataCoapParameterExtractor implements ICommandDeliveryParameterExtractor<CoapParameters> {

	/** Default metadata field for remote hostname */
	public static final String DEFAULT_HOSTNAME_METADATA = "hostname";

	/** Default metadata field for remote port */
	public static final String DEFAULT_PORT_METADATA = "port";

	/** Default metadata field for CoAP URL */
	public static final String DEFAULT_URL_METADATA = "url";

	/** Hostname metadata field name */
	private String hostnameMetadataField = DEFAULT_HOSTNAME_METADATA;

	/** Port metadata field name */
	private String portMetadataField = DEFAULT_PORT_METADATA;

	/** CoAP URL metadata field name */
	private String urlMetadataField = DEFAULT_URL_METADATA;

	/** Overrides port metadata */
	private Integer portOverride;

	public MetadataCoapParameterExtractor() {
	}

	public MetadataCoapParameterExtractor(int portOverride) {
		this.portOverride = portOverride;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.sitewhere.spi.device.communication.ICommandDeliveryParameterExtractor#
	 * extractDeliveryParameters(com.sitewhere.spi.device.IDeviceNestingContext,
	 * com.sitewhere.spi.device.IDeviceAssignment,
	 * com.sitewhere.spi.device.command.IDeviceCommandExecution)
	 */
	@Override
	public CoapParameters extractDeliveryParameters(IDeviceNestingContext nesting,
			IDeviceAssignment assignment, IDeviceCommandExecution execution) throws SiteWhereException {
		Map<String, String> metadata = nesting.getGateway().getMetadata();
		String hostname = metadata.get(getHostnameMetadataField());
		String port = metadata.get(getPortMetadataField());
		String url = metadata.get(getUrlMetadataField());
		CoapParameters coap = new CoapParameters();
		coap.setHostname(hostname);
		if (port != null) {
			coap.setPort(Integer.parseInt(port));
		}
		if (url != null) {
			coap.setUrl(url);
		}
		return coap;
	}

	public String getHostnameMetadataField() {
		return hostnameMetadataField;
	}

	public void setHostnameMetadataField(String hostnameMetadataField) {
		this.hostnameMetadataField = hostnameMetadataField;
	}

	public String getPortMetadataField() {
		return portMetadataField;
	}

	public void setPortMetadataField(String portMetadataField) {
		this.portMetadataField = portMetadataField;
	}

	public String getUrlMetadataField() {
		return urlMetadataField;
	}

	public void setUrlMetadataField(String urlMetadataField) {
		this.urlMetadataField = urlMetadataField;
	}

	public Integer getPortOverride() {
		return portOverride;
	}

	public void setPortOverride(Integer portOverride) {
		this.portOverride = portOverride;
	}
}