/*
 * Copyright (c) SiteWhere, LLC. All rights reserved. http://www.sitewhere.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package com.sitewhere.spi.device.symbology;

import java.io.OutputStream;

import com.sitewhere.spi.SiteWhereException;
import com.sitewhere.spi.device.IDevice;
import com.sitewhere.spi.device.IDeviceAssignment;
import com.sitewhere.spi.device.IDeviceSpecification;
import com.sitewhere.spi.device.ISite;

/**
 * Generates symbols which uniquely identify SiteWhere entities.
 * 
 * @author Derek
 */
public interface ISymbolGenerator {

	/**
	 * Get symbol for a site.
	 * 
	 * @param site
	 * @param provider
	 * @return
	 * @throws SiteWhereException
	 */
	public OutputStream getSiteSymbol(ISite site, IEntityUriProvider provider) throws SiteWhereException;

	/**
	 * Get symbol for a device specification.
	 * 
	 * @param specification
	 * @param provider
	 * @return
	 * @throws SiteWhereException
	 */
	public OutputStream getDeviceSpecificationSymbol(IDeviceSpecification specification,
			IEntityUriProvider provider) throws SiteWhereException;

	/**
	 * Get symbol for a device.
	 * 
	 * @param device
	 * @param provider
	 * @return
	 * @throws SiteWhereException
	 */
	public OutputStream getDeviceSymbol(IDevice device, IEntityUriProvider provider)
			throws SiteWhereException;

	/**
	 * Get symbol for a device assignment.
	 * 
	 * @param assignment
	 * @param provider
	 * @return
	 * @throws SiteWhereException
	 */
	public OutputStream getDeviceAssigmentSymbol(IDeviceAssignment assignment, IEntityUriProvider provider)
			throws SiteWhereException;
}