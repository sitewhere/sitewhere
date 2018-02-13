/*
 * Copyright (c) SiteWhere, LLC. All rights reserved. http://www.sitewhere.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package com.sitewhere.rest.model.area;

import java.io.Serializable;

import com.sitewhere.rest.model.common.MetadataProvider;
import com.sitewhere.spi.SiteWhereException;
import com.sitewhere.spi.area.IAreaMapData;

/**
 * Model object for area map information.
 * 
 * @author Derek
 */
public class AreaMapData extends MetadataProvider implements IAreaMapData, Serializable {

    /** Serialization version identifier */
    private static final long serialVersionUID = -6587910408267715129L;

    /** Map type */
    private String type;

    /*
     * @see com.sitewhere.spi.area.IAreaMapData#getType()
     */
    @Override
    public String getType() {
	return type;
    }

    public void setType(String type) {
	this.type = type;
    }

    /**
     * Copy map information from an SPI object.
     * 
     * @param source
     * @return
     */
    public static AreaMapData copy(IAreaMapData source) throws SiteWhereException {
	AreaMapData target = new AreaMapData();
	target.setType(source.getType());
	MetadataProvider.copy(source, target);
	return target;
    }
}