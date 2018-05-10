/*
 * Copyright (c) SiteWhere, LLC. All rights reserved. http://www.sitewhere.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package com.sitewhere.device.marshaling;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.sitewhere.rest.model.common.MetadataProviderEntity;
import com.sitewhere.rest.model.customer.CustomerType;
import com.sitewhere.rest.model.device.marshaling.MarshaledCustomerType;
import com.sitewhere.spi.SiteWhereException;
import com.sitewhere.spi.customer.ICustomerType;
import com.sitewhere.spi.device.IDeviceManagement;

/**
 * Configurable helper class that allows {@link CustomerType} model objects to
 * be created from {@link ICustomerType} SPI objects.
 * 
 * @author Derek
 */
public class CustomerTypeMarshalHelper {

    /** Static logger instance */
    private static Log LOGGER = LogFactory.getLog(CustomerTypeMarshalHelper.class);

    /** Device management */
    private IDeviceManagement deviceManagement;

    /** Indicates whether contained customer types are to be included */
    private boolean includeContainedCustomerTypes = false;

    public CustomerTypeMarshalHelper(IDeviceManagement deviceManagement) {
	this.deviceManagement = deviceManagement;
    }

    /**
     * Convert the SPI into a model object based on marshaling parameters.
     * 
     * @param source
     * @return
     * @throws SiteWhereException
     */
    public MarshaledCustomerType convert(ICustomerType source) throws SiteWhereException {
	MarshaledCustomerType type = new MarshaledCustomerType();
	type.setId(source.getId());
	type.setToken(source.getToken());
	type.setName(source.getName());
	type.setDescription(source.getDescription());
	type.setIcon(source.getIcon());
	type.setContainedCustomerTypeIds(source.getContainedCustomerTypeIds());
	MetadataProviderEntity.copy(source, type);
	if (isIncludeContainedCustomerTypes()) {
	    List<ICustomerType> ccts = new ArrayList<ICustomerType>();
	    for (UUID ctid : source.getContainedCustomerTypeIds()) {
		ICustomerType at = getDeviceManagement().getCustomerType(ctid);
		if (at != null) {
		    ccts.add(at);
		} else {
		    LOGGER.warn("Contained customer types has invalid reference.");
		}
	    }
	    type.setContainedCustomerTypes(ccts);
	}
	return type;
    }

    public boolean isIncludeContainedCustomerTypes() {
	return includeContainedCustomerTypes;
    }

    public void setIncludeContainedCustomerTypes(boolean includeContainedCustomerTypes) {
	this.includeContainedCustomerTypes = includeContainedCustomerTypes;
    }

    public IDeviceManagement getDeviceManagement() {
	return deviceManagement;
    }

    public void setDeviceManagement(IDeviceManagement deviceManagement) {
	this.deviceManagement = deviceManagement;
    }
}
