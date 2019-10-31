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

import com.sitewhere.rest.model.common.BrandedEntity;
import com.sitewhere.rest.model.device.group.DeviceGroup;
import com.sitewhere.spi.SiteWhereException;
import com.sitewhere.spi.device.group.IDeviceGroup;

/**
 * Configurable helper class that allows {@link DeviceGroup} model objects to be
 * created from {@link IDeviceGroup} SPI objects.
 */
public class DeviceGroupMarshalHelper {

    /**
     * Convert API object to model object.
     * 
     * @param source
     * @return
     * @throws SiteWhereException
     */
    public DeviceGroup convert(IDeviceGroup source) throws SiteWhereException {
	DeviceGroup group = new DeviceGroup();
	group.setName(source.getName());
	group.setDescription(source.getDescription());
	List<String> roles = new ArrayList<String>();
	roles.addAll(source.getRoles());
	group.setRoles(roles);
	BrandedEntity.copy(source, group);
	return group;
    }
}