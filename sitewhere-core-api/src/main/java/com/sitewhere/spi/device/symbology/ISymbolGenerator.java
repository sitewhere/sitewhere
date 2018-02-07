/*
 * Copyright (c) SiteWhere, LLC. All rights reserved. http://www.sitewhere.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package com.sitewhere.spi.device.symbology;

import com.sitewhere.spi.SiteWhereException;
import com.sitewhere.spi.device.IDevice;
import com.sitewhere.spi.device.IDeviceAssignment;
import com.sitewhere.spi.device.IDeviceType;
import com.sitewhere.spi.device.ISite;
import com.sitewhere.spi.server.lifecycle.ITenantEngineLifecycleComponent;

/**
 * Generates symbols which uniquely identify SiteWhere entities.
 * 
 * @author Derek
 */
public interface ISymbolGenerator extends ITenantEngineLifecycleComponent {

    /**
     * Get unique generator id.
     * 
     * @return
     * @throws SiteWhereException
     */
    public String getId() throws SiteWhereException;

    /**
     * Get name of symbol generator.
     * 
     * @return
     * @throws SiteWhereException
     */
    public String getName() throws SiteWhereException;

    /**
     * Get symbol for a site.
     * 
     * @param site
     * @param provider
     * @return
     * @throws SiteWhereException
     */
    public byte[] getSiteSymbol(ISite site, IEntityUriProvider provider) throws SiteWhereException;

    /**
     * Get symbol for a device type.
     * 
     * @param deviceType
     * @param provider
     * @return
     * @throws SiteWhereException
     */
    public byte[] getDeviceTypeSymbol(IDeviceType deviceType, IEntityUriProvider provider) throws SiteWhereException;

    /**
     * Get symbol for a device.
     * 
     * @param device
     * @param provider
     * @return
     * @throws SiteWhereException
     */
    public byte[] getDeviceSymbol(IDevice device, IEntityUriProvider provider) throws SiteWhereException;

    /**
     * Get symbol for a device assignment.
     * 
     * @param assignment
     * @param provider
     * @return
     * @throws SiteWhereException
     */
    public byte[] getDeviceAssigmentSymbol(IDeviceAssignment assignment, IEntityUriProvider provider)
	    throws SiteWhereException;
}