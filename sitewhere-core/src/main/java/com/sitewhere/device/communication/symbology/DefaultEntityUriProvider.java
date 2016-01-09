/*
 * Copyright (c) SiteWhere, LLC. All rights reserved. http://www.sitewhere.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package com.sitewhere.device.communication.symbology;

import java.net.URI;
import java.net.URISyntaxException;

import com.sitewhere.spi.SiteWhereException;
import com.sitewhere.spi.device.IDevice;
import com.sitewhere.spi.device.IDeviceAssignment;
import com.sitewhere.spi.device.IDeviceSpecification;
import com.sitewhere.spi.device.ISite;
import com.sitewhere.spi.device.symbology.IEntityUriProvider;

/**
 * Default implementation of {@link IEntityUriProvider}.
 * 
 * @author Derek
 */
public class DefaultEntityUriProvider implements IEntityUriProvider {

	/** SiteWhere protocol prefix */
	public static final String SITEWHERE_PROTOCOL = "sitewhere://";

	/** Singleton instance */
	public static DefaultEntityUriProvider INSTANCE;

	protected DefaultEntityUriProvider() {
	}

	public static DefaultEntityUriProvider getInstance() {
		if (INSTANCE == null) {
			INSTANCE = new DefaultEntityUriProvider();
		}
		return INSTANCE;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.sitewhere.spi.device.symbology.IEntityUriProvider#getSiteIdentifier(com.sitewhere
	 * .spi.device.ISite)
	 */
	@Override
	public URI getSiteIdentifier(ISite site) throws SiteWhereException {
		return createUri(SITEWHERE_PROTOCOL + "site/" + site.getToken());
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.sitewhere.spi.device.symbology.IEntityUriProvider#getDeviceSpecificationIdentifier
	 * (com.sitewhere.spi.device.IDeviceSpecification)
	 */
	@Override
	public URI getDeviceSpecificationIdentifier(IDeviceSpecification specification) throws SiteWhereException {
		return createUri(SITEWHERE_PROTOCOL + "specification/" + specification.getToken());
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.sitewhere.spi.device.symbology.IEntityUriProvider#getDeviceIdentifier(com.sitewhere
	 * .spi.device.IDevice)
	 */
	@Override
	public URI getDeviceIdentifier(IDevice device) throws SiteWhereException {
		return createUri(SITEWHERE_PROTOCOL + "device/" + device.getHardwareId());
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.sitewhere.spi.device.symbology.IEntityUriProvider#getDeviceAssignmentIdentifier
	 * (com.sitewhere.spi.device.IDeviceAssignment)
	 */
	@Override
	public URI getDeviceAssignmentIdentifier(IDeviceAssignment assignment) throws SiteWhereException {
		return createUri(SITEWHERE_PROTOCOL + "assignment/" + assignment.getToken());
	}

	/**
	 * Create a URI.
	 * 
	 * @param uri
	 * @return
	 */
	protected URI createUri(String uri) {
		try {
			return new URI(uri);
		} catch (URISyntaxException e) {
			throw new RuntimeException(e);
		}
	}
}